package com.selaliadobor.darkskyweather.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.LinearLayoutInfo;
import com.facebook.litho.widget.Recycler;
import com.facebook.litho.widget.RecyclerBinder;
import com.selaliadobor.darkskyweather.R;
import com.selaliadobor.darkskyweather.data.HourlyReport;
import com.selaliadobor.darkskyweather.layoutspecs.HourlyReportListItemLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.subscriptions.CompositeSubscription;


public class FullDayWeatherFragment extends Fragment {
    public static final int DAY_VIEW_ITEM_HEIGHT_DP = 50;
    public static final int DAY_ITEM_COUNT = 24;
    CompositeSubscription lifecycleSubscriptions = new CompositeSubscription();
    private Unbinder unbinder;
    @BindView(R.id.full_day_lithoView)
    LithoView lithoView;
    public FullDayWeatherFragment() {
    }
    public static FullDayWeatherFragment create(){
        FullDayWeatherFragment fragment = new FullDayWeatherFragment();
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        unbinder.unbind();
        lifecycleSubscriptions.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_day_weather, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        setupLithoView();
        return inflatedView;
    }

    private void setupLithoView() {
        ComponentContext componentContext = new ComponentContext(getActivity());
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


        lithoView.setComponent(hourlyRecyclerComponent);
    }

    private void updateHourlyReports(Realm realm, ComponentContext componentContext, RecyclerBinder recyclerBinder, RealmResults<HourlyReport> hourlyReports) {
        int itemCount = Math.min(DAY_ITEM_COUNT, hourlyReports.size());
        if (itemCount < recyclerBinder.getItemCount() && recyclerBinder.getItemCount() > 0) {
            recyclerBinder.removeRangeAt(Math.max(0, itemCount - 1), Math.max(0,  recyclerBinder.getItemCount() - itemCount));
        }
        for (int i = 0; i < itemCount; i++) {
            HourlyReport hourlyReport = realm.copyFromRealm(hourlyReports.get(i));
            Component<HourlyReportListItemLayout> listItem = HourlyReportListItemLayout.create(componentContext)
                    .heightDip(DAY_VIEW_ITEM_HEIGHT_DP)
                    .clickEventHandler(() -> {})
                    .hourlyReport(hourlyReport)
                    .build();
            if (recyclerBinder.isValidPosition(i)) {
                recyclerBinder.updateItemAt(i, listItem);
            } else {
                recyclerBinder.insertItemAt(i, listItem);
            }
        }
    }
}
