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
import com.jakewharton.rxbinding.view.RxView;
import com.selaliadobor.darkskyweather.R;
import com.selaliadobor.darkskyweather.data.HourlyReport;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJob;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJobSetupException;
import com.selaliadobor.darkskyweather.layoutspecs.HourlyReportListItemLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.Sort;
import rx.subscriptions.CompositeSubscription;


public class DayWeatherFragment extends Fragment {

    public DayWeatherFragment() {
    }
    public static DayWeatherFragment create(){
        DayWeatherFragment fragment = new DayWeatherFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_day_weather, container, false);
        return inflatedView;
    }

}
