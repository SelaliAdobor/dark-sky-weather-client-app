package com.selaliadobor.darkskyweather.job;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.selaliadobor.darkskyweather.BuildConfig;
import com.selaliadobor.darkskyweather.GsonTypeAdaptorFactory;
import com.selaliadobor.darkskyweather.apis.darksky.DarkSkyWeatherService;
import com.selaliadobor.darkskyweather.apis.darksky.DarkSkyWeatherServiceUtil;
import com.selaliadobor.darkskyweather.apis.darksky.responseobjects.DataPoint;
import com.selaliadobor.darkskyweather.apis.darksky.responseobjects.ForecastResponse;
import com.selaliadobor.darkskyweather.data.DailyReport;
import com.selaliadobor.darkskyweather.data.HourlyReport;
import com.selaliadobor.darkskyweather.data.WeatherType;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Periodically retrieves forecasts from Dark Skys API and writes them to Realm
 *
 * Update interval defined by @{@link BuildConfig}
 */
public class RetrieveWeatherJob extends Job {
    static final String JOB_TAG = "com.selaliadobor.darkskyweather.job.RetrieveWeatherJob";

    private static final String EXTRA_LATITUTDE = "latitude";
    private static final String EXTRA_LONGITUDE = "longitude";
    private static final String EXTRA_ZIP_CODE = "zipCode";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        String zipCode = params.getExtras().getString(EXTRA_ZIP_CODE, null);
        double latitude = params.getExtras().getDouble(EXTRA_LATITUTDE,0.0);
        double longitude = params.getExtras().getDouble(EXTRA_LONGITUDE,0.0);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(GsonTypeAdaptorFactory.create())
                .create();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.darksky.net/")
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        DarkSkyWeatherService darkSkyWeatherService = retrofit.create(DarkSkyWeatherService.class);
        darkSkyWeatherService.listEvents(BuildConfig.DARK_SKY_API_KEY,latitude, longitude).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                Timber.i("Updating forecast from Dark Sky API");
                ForecastResponse forecastResponse = response.body();

                writeResponseToRealm(forecastResponse, zipCode);
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Timber.e(t,"Failed to get forecast from DarkSkyApi %s", call);
            }
        });
        return Result.SUCCESS;
    }

    private void writeResponseToRealm(ForecastResponse forecastResponse, String zipCode) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(transaction -> {
            for(DataPoint dailyDataPoint : forecastResponse.daily().data()){
                DailyReport dailyReport = new DailyReport();

                dailyReport.setDate(dailyDataPoint.time());

                WeatherType weatherType = DarkSkyWeatherServiceUtil.weatherTypeFromIcon(dailyDataPoint.icon());
                dailyReport.setWeatherType(weatherType);

                dailyReport.setZipCode(zipCode);

                dailyReport.setSummary(dailyDataPoint.summary());

                dailyReport.setHighTemp(dailyDataPoint.apparentTemperatureHigh());

                dailyReport.setLowTemp(dailyDataPoint.apparentTemperatureLow());

                transaction.copyToRealmOrUpdate(dailyReport);
                Timber.i("Wrote daily weather info for %d",dailyReport.getDate());
            }

            for(DataPoint hourlyDataPoint : forecastResponse.hourly().data()){
                HourlyReport hourlyReport = new HourlyReport();

                hourlyReport.setDate(hourlyDataPoint.time());

                WeatherType weatherType = DarkSkyWeatherServiceUtil.weatherTypeFromIcon(hourlyDataPoint.icon());
                hourlyReport.setWeatherType(weatherType);

                hourlyReport.setZipCode(zipCode);

                hourlyReport.setSummary(hourlyDataPoint.summary());

                hourlyReport.setTemperature(hourlyDataPoint.apparentTemperature());

                transaction.copyToRealmOrUpdate(hourlyReport);
                Timber.i("Wrote hourly weather info for %d",hourlyReport.getDate());

                deleteOldRealmEntries(zipCode, transaction);

            }
        });
    }

    private void deleteOldRealmEntries(String zipCode, Realm transaction) {
            long startOfToday = getStartOfDate(new Date());

            RealmResults<DailyReport> oldDailyReports = transaction
                    .where(DailyReport.class)
                    .notEqualTo("zipCode", zipCode)
                    .or()
                    .lessThan("date", startOfToday)
                    .findAll();
            Timber.i("Deleting %d old daily entries",oldDailyReports);
            oldDailyReports.deleteAllFromRealm();

            RealmResults<HourlyReport> oldHourlyReports = transaction
                    .where(HourlyReport.class)
                    .notEqualTo("zipCode", zipCode)
                    .or()
                    .lessThan("date", startOfToday)
                    .findAll();
            Timber.i("Deleting %d old hourly entries",oldHourlyReports);
            oldHourlyReports .deleteAllFromRealm();
    }

    /**
     * Starts periodic updates of weather forecast
     * @param zipCode The zip code used to check the weather forecase
     * @param context An android context used to access @{@link Geocoder}
     * @throws RetrieveWeatherJobSetupException Thrown if the job could not be configured with the given parameters
     */
    public static void startWeatherRetrievalJob(String zipCode, Context context) throws RetrieveWeatherJobSetupException {
        Geocoder geocoder = new Geocoder(context);

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(zipCode, 1);
        } catch (IOException exception) {
            throw new RetrieveWeatherJobSetupException("Could not encode zip as address",exception);
        }

        if(addresses.size() < 1){
            throw new RetrieveWeatherJobSetupException("Geocoder failed to translate zip to address: "+zipCode);
        }

        Address address = addresses.get(0);

        startUpdates(zipCode, address);
    }

    private static void startUpdates(String zipCode, Address address) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString("zipCode",zipCode);
        extras.putDouble(EXTRA_LATITUTDE, address.getLatitude());
        extras.putDouble(EXTRA_LONGITUDE, address.getLongitude());

        new JobRequest.Builder(JOB_TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(BuildConfig.DARK_SKY_UPDATE_INTERVAL))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setExtras(extras)
                .setRequirementsEnforced(true)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
    public long getStartOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTime().getTime();
    }

}
