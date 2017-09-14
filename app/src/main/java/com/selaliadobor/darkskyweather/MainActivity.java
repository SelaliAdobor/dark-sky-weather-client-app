package com.selaliadobor.darkskyweather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentInfo;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.LinearLayoutInfo;
import com.facebook.litho.widget.Recycler;
import com.facebook.litho.widget.RecyclerBinder;
import com.selaliadobor.darkskyweather.data.HourlyReport;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJob;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJobSetupException;
import com.selaliadobor.darkskyweather.layoutspecs.HourlyReportListItemLayout;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            RetrieveWeatherJob.startWeatherRetrievalJob("06103",this);
        } catch (RetrieveWeatherJobSetupException e) {
            throw new RuntimeException(e);
        }

        Realm realm = Realm.getDefaultInstance();


        final ComponentContext componentContext = new ComponentContext(this);

        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder()
                .layoutInfo( new LinearLayoutInfo(this, OrientationHelper.VERTICAL, false))
                .build(componentContext);


        Date value = new Date();
        realm.where(HourlyReport.class).greaterThan("date", value).findAllSorted("date", Sort.ASCENDING).asObservable().subscribe(hourlyReports -> {
            for (int i = 0; i < hourlyReports.size(); i++) {
                recyclerBinder.insertItemAt(
                        i,
                        HourlyReportListItemLayout.create(componentContext)
                                .hourlyReport(realm.copyFromRealm(hourlyReports.get(i))).build());
            }
        });
        final Component<Recycler> recyclerComponent = Recycler.create(componentContext)
                .binder(recyclerBinder)
                .build();

        final LithoView lithoView = LithoView.create(
                this,
                recyclerComponent);
        setContentView(lithoView);
    }

}
