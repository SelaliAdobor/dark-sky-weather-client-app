package com.selaliadobor.darkskyweather.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.LinearLayoutInfo;
import com.facebook.litho.widget.Recycler;
import com.facebook.litho.widget.RecyclerBinder;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.selaliadobor.darkskyweather.R;
import com.selaliadobor.darkskyweather.data.DailyReport;
import com.selaliadobor.darkskyweather.data.HourlyReport;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJob;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJobSetupException;
import com.selaliadobor.darkskyweather.layoutspecs.DailyReportListItemLayout;
import com.selaliadobor.darkskyweather.layoutspecs.HourlyReportListItemLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class SearchFragment extends Fragment {
    public static final int HOURLY_VIEW_ITEM_HEIGHT_DP = 50;
    public static final int HOURLY_ITEM_COUNT = 4;

    public static final int DAILY_VIEW_ITEM_HEIGHT_DP = 70;
    public static final int DAILY_EXPANDED_ITEM_COUNT = 7;
    public static final int DAILY_TERM_VIEW_EXPANDED_HEIGHT = DAILY_EXPANDED_ITEM_COUNT * DAILY_VIEW_ITEM_HEIGHT_DP;
    public static final int DAILY_TERM_COMPRESSED_ITEM_COUNT = 3;
    public static final int DAILY_TERM_VIEW_COMPRESSED_HEIGHT = DAILY_TERM_COMPRESSED_ITEM_COUNT * DAILY_VIEW_ITEM_HEIGHT_DP;

    @BindView(R.id.search_daily_lithoView)
    LithoView dailyLithoView;

    @BindView(R.id.search_hourly_lithoView)
    LithoView hourlyLithoView;

    @BindView(R.id.search_zip_code_editText)
    EditText zipCodeEditText;

    @BindView(R.id.search_update_zip_button)
    Button updateZipButton;


    CompositeSubscription lifecycleSubscriptions = new CompositeSubscription();
    Action1<Boolean> updateDailyRecycler;
    boolean isHourlyViewExpanded = false;
    private Unbinder unbinder;

    public SearchFragment() {
    }

    public static SearchFragment create() {
        return new SearchFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        lifecycleSubscriptions.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);


        final ComponentContext componentContext = new ComponentContext(getActivity());
        setupHourlyView(componentContext);
        setupDailyView(componentContext);

        lifecycleSubscriptions.addAll(
                RxTextView.textChanges(zipCodeEditText)
                        .subscribe(charSequence -> {
                            if (charSequence.length() < 5) {
                                updateZipButton.setEnabled(false);
                                updateZipButton.setText("Invalid Zip");
                            } else {
                                updateZipButton.setEnabled(true);
                                updateZipButton.setText("Set Zip");
                            }
                        }),
                RxView.clicks(updateZipButton)
                        .subscribe(aVoid -> {
                            String zipCode = zipCodeEditText.getText().toString();
                            try {
                                RetrieveWeatherJob.startWeatherRetrievalJob(zipCode, getActivity());
                            } catch (RetrieveWeatherJobSetupException exception) {
                                Toast
                                        .makeText(getContext(), "Failed to update zip: " + exception.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }));
        return inflatedView;
    }

    private void setupHourlyView(ComponentContext componentContext) {
        Realm realm = Realm.getDefaultInstance();


        final RecyclerBinder hourlyRecyclerBinder = new RecyclerBinder.Builder()
                .layoutInfo(new LinearLayoutInfo(getActivity(), OrientationHelper.VERTICAL, false))
                .build(componentContext);


        long startOfLastHour = new Date().getTime() - 3600000L;
        lifecycleSubscriptions.add(
                realm
                        .where(HourlyReport.class)
                        .greaterThan("date", startOfLastHour)
                        .findAllSorted("date", Sort.ASCENDING)
                        .asObservable()
                        .subscribe(hourlyReports -> {
                            updateHourlyReports(realm, componentContext, hourlyRecyclerBinder, hourlyReports);
                        })
        );


        final Component<Recycler> hourlyRecyclerComponent = Recycler.create(componentContext)
                .binder(hourlyRecyclerBinder)
                .build();


        hourlyLithoView.setComponent(hourlyRecyclerComponent);
        hourlyLithoView.getLayoutParams().height = ((int) (getResources().getDisplayMetrics().density * HOURLY_ITEM_COUNT * HOURLY_VIEW_ITEM_HEIGHT_DP));
        hourlyLithoView.requestLayout();
    }

    private void setupDailyView(ComponentContext componentContext) {
        Realm realm = Realm.getDefaultInstance();


        final RecyclerBinder dailyRecyclerBinder = new RecyclerBinder.Builder()
                .layoutInfo(new LinearLayoutInfo(getActivity(), OrientationHelper.VERTICAL, false))
                .build(componentContext);


        lifecycleSubscriptions.add(
                realm
                        .where(DailyReport.class)
                        .greaterThan("date", new Date().getTime())
                        .findAllSorted("date", Sort.ASCENDING)
                        .asObservable()
                        .subscribe(dailyReports -> {
                            updateDailyReports(realm, componentContext, dailyRecyclerBinder, dailyReports);
                        })
        );


        final Component<Recycler> dailyRecyclerComponent = Recycler.create(componentContext)
                .binder(dailyRecyclerBinder)
                .build();


        dailyLithoView.setComponent(dailyRecyclerComponent);
        dailyLithoView.getLayoutParams().height = ((int) (getResources().getDisplayMetrics().density * DAILY_TERM_COMPRESSED_ITEM_COUNT * DAILY_VIEW_ITEM_HEIGHT_DP));
        dailyLithoView.requestLayout();
    }

    private void updateHourlyReports(Realm realm, ComponentContext componentContext, RecyclerBinder recyclerBinder, RealmResults<HourlyReport> hourlyReports) {
        int itemCount = Math.min(HOURLY_ITEM_COUNT, hourlyReports.size());
        if (itemCount < recyclerBinder.getItemCount() && recyclerBinder.getItemCount() > 0) {
            recyclerBinder.removeRangeAt(Math.max(0, itemCount - 1), Math.max(0, recyclerBinder.getItemCount() - itemCount));
        }
        for (int i = 0; i < itemCount; i++) {
            HourlyReport hourlyReport = realm.copyFromRealm(hourlyReports.get(i));
            Component<HourlyReportListItemLayout> listItem = HourlyReportListItemLayout.create(componentContext)
                    .heightDip(HOURLY_VIEW_ITEM_HEIGHT_DP)
                    .clickEventHandler(() -> {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, FullDayWeatherFragment.create(), null)
                                .addToBackStack(null)
                                .commit();
                    })
                    .hourlyReport(hourlyReport)
                    .build();
            if (recyclerBinder.isValidPosition(i)) {
                recyclerBinder.updateItemAt(i, listItem);
            } else {
                recyclerBinder.insertItemAt(i, listItem);
            }
        }
    }

    private void updateDailyReports(Realm realm, ComponentContext componentContext, RecyclerBinder recyclerBinder, RealmResults<DailyReport> dailyReports) {
        updateDailyRecycler = (isExpanded) -> {
            int itemCount = isExpanded ?
                    dailyReports.size() :
                    Math.min(DAILY_TERM_COMPRESSED_ITEM_COUNT, dailyReports.size());
            if (itemCount < recyclerBinder.getItemCount() && recyclerBinder.getItemCount() > 0) {
                recyclerBinder.removeRangeAt(Math.max(0, itemCount - 1), Math.max(0, recyclerBinder.getItemCount() - itemCount));
            }
            for (int i = 0; i < itemCount; i++) {
                DailyReport dailyReport = realm.copyFromRealm(dailyReports.get(i));
                Component<DailyReportListItemLayout> listItem = DailyReportListItemLayout.create(componentContext)
                        .heightDip(DAILY_VIEW_ITEM_HEIGHT_DP)
                        .clickEventHandler(this::toggleDailyViewExpansion)
                        .dailyReport(dailyReport)
                        .build();
                if (recyclerBinder.isValidPosition(i)) {
                    recyclerBinder.updateItemAt(i, listItem);
                } else {
                    recyclerBinder.insertItemAt(i, listItem);
                }
            }
        };
        updateDailyRecycler.call(isHourlyViewExpanded);
    }

    void toggleDailyViewExpansion() {
        if (updateDailyRecycler == null) {
            return;
        }

        isHourlyViewExpanded = !isHourlyViewExpanded;


        hourlyLithoView.animate()
                .scaleY(isHourlyViewExpanded ? 0f : 1f)
                .alpha(isHourlyViewExpanded ? 0f : 1f)
                .setUpdateListener((value) -> {
                    final float scale = getResources().getDisplayMetrics().density;
                    float startingHeight = isHourlyViewExpanded ?
                            DAILY_TERM_VIEW_COMPRESSED_HEIGHT :
                            DAILY_TERM_VIEW_EXPANDED_HEIGHT;
                    float targetHeight = isHourlyViewExpanded ?
                            DAILY_TERM_VIEW_EXPANDED_HEIGHT :
                            DAILY_TERM_VIEW_COMPRESSED_HEIGHT;
                    float delta = targetHeight - startingHeight;

                    int currentDelta = (int) (delta * (1.0 - value.getAnimatedFraction()));

                    dailyLithoView.getLayoutParams().height = (int) (scale * (targetHeight - currentDelta));
                    dailyLithoView.requestLayout();
                })
                //If the list is expanding, immediately add new items, otherwise wait until animation finishes
                .withStartAction(() -> {
                    if (isHourlyViewExpanded) {
                        updateDailyRecycler.call(isHourlyViewExpanded);
                    }
                })
                .withEndAction(() -> {
                    if (!isHourlyViewExpanded) {
                        updateDailyRecycler.call(isHourlyViewExpanded);
                    }
                })
                .setDuration(500);
    }
}
