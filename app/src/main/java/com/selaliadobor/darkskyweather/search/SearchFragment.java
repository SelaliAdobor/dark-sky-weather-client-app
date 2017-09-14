package com.selaliadobor.darkskyweather.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.text.Editable;
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

public class SearchFragment extends Fragment {
    public static final int HOURLY_VIEW_ITEM_HEIGHT_DP = 50;
    public static final int SHORT_TERM_ITEM_COUNT = 4;

    @BindView(R.id.search_short_term_lithoView)
    LithoView shortTermLithoView;

    @BindView(R.id.search_short_term_frame)
    ViewGroup shortTermViewGroup;

    @BindView(R.id.search_zip_code_editText)
    EditText zipCodeEditText;

    @BindView(R.id.search_update_zip_button)
    Button updateZipButton;

    CompositeSubscription lifecycleSubscriptions = new CompositeSubscription();
    private Unbinder unbinder;

    public SearchFragment() {
    }
    public static SearchFragment create(){
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(unbinder != null){
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
        setupLitho();
        lifecycleSubscriptions.add(
                RxTextView.textChanges(zipCodeEditText)
                        .subscribe(charSequence -> {
                            if(charSequence.length() < 5){
                                updateZipButton.setEnabled(false);
                                updateZipButton.setText("Invalid Zip");
                            }else{
                                updateZipButton.setEnabled(true);
                                updateZipButton.setText("Set Zip");
                            }
                        }));
        lifecycleSubscriptions.add(
                RxView.clicks(updateZipButton)
                        .subscribe(aVoid -> {
                            String zipCode = zipCodeEditText.getText().toString();
                            try {
                                RetrieveWeatherJob.startWeatherRetrievalJob(zipCode,getActivity());
                            } catch (RetrieveWeatherJobSetupException exception) {
                                Toast
                                        .makeText(getContext(),"Failed to update zip: "+exception.getMessage(),Toast.LENGTH_LONG)
                                        .show();
                            }
                        }));
        return inflatedView;
    }

    private void setupLitho() {

        Realm realm = Realm.getDefaultInstance();


        final ComponentContext componentContext = new ComponentContext(getActivity());

        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder()
                .layoutInfo( new LinearLayoutInfo(getActivity(), OrientationHelper.VERTICAL, false))
                .build(componentContext);


        Date value = new Date();
        lifecycleSubscriptions.add(
                realm
                    .where(HourlyReport.class)
                    .greaterThan("date", value.getTime())
                    .findAllSorted("date", Sort.ASCENDING)
                    .asObservable()
                    .subscribe(hourlyReports -> {
                        int itemCount = Math.min(SHORT_TERM_ITEM_COUNT,hourlyReports.size());
                        for (int i = 0; i < itemCount; i++) {
                            HourlyReport hourlyReport = realm.copyFromRealm(hourlyReports.get(i));
                            Component<HourlyReportListItemLayout> listItem = HourlyReportListItemLayout.create(componentContext)
                                    .heightDip(HOURLY_VIEW_ITEM_HEIGHT_DP)
                                    .hourlyReport(hourlyReport).build();
                            if(recyclerBinder.isValidPosition(i)){
                                recyclerBinder.updateItemAt(i,listItem);
                            }else{
                                recyclerBinder.insertItemAt(i,listItem);
                            }

                            if(hourlyReports.size() < SHORT_TERM_ITEM_COUNT){
                                recyclerBinder.removeRangeAt(hourlyReports.size() - 1,SHORT_TERM_ITEM_COUNT-hourlyReports.size());
                            }

                        }
                    })
        );

        final Component<Recycler> recyclerComponent = Recycler.create(componentContext)
                .binder(recyclerBinder)
                .build();
        shortTermLithoView.setComponent(recyclerComponent);
    }
}
