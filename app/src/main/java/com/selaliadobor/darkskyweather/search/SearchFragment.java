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
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJob;
import com.selaliadobor.darkskyweather.job.RetrieveWeatherJobSetupException;
import com.selaliadobor.darkskyweather.layoutspecs.DailyReportListItemLayout;

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
    public static final int DAILY_VIEW_ITEM_HEIGHT_DP = 70;
    public static final int DAILY_EXPANDED_ITEM_COUNT = 7;
    public static final int DAILY_TERM_VIEW_EXPANDED_HEIGHT = DAILY_EXPANDED_ITEM_COUNT * DAILY_VIEW_ITEM_HEIGHT_DP;
    public static final int DAILY_TERM_COMPRESSED_ITEM_COUNT = 3;
    public static final int DAILY_TERM_VIEW_COMPRESSED_HEIGHT = DAILY_TERM_COMPRESSED_ITEM_COUNT * DAILY_VIEW_ITEM_HEIGHT_DP;

    @BindView(R.id.search_daily_lithoView)
    LithoView dailyLithoView;


    @BindView(R.id.search_zip_code_editText)
    EditText zipCodeEditText;

    @BindView(R.id.search_update_zip_button)
    Button updateZipButton;


    CompositeSubscription lifecycleSubscriptions = new CompositeSubscription();
    Action1<Boolean> updateRecycler;
    boolean isExpanded = false;
    private Unbinder unbinder;

    public SearchFragment() {
    }

    public static SearchFragment create() {
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
        setupLitho();
        lifecycleSubscriptions.add(
                RxTextView.textChanges(zipCodeEditText)
                        .subscribe(charSequence -> {
                            if (charSequence.length() < 5) {
                                updateZipButton.setEnabled(false);
                                updateZipButton.setText("Invalid Zip");
                            } else {
                                updateZipButton.setEnabled(true);
                                updateZipButton.setText("Set Zip");
                            }
                        }));
        lifecycleSubscriptions.add(
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

    private void setupLitho() {

        Realm realm = Realm.getDefaultInstance();


        final ComponentContext componentContext = new ComponentContext(getActivity());

        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder()
                .layoutInfo(new LinearLayoutInfo(getActivity(), OrientationHelper.VERTICAL, false))
                .build(componentContext);


        Date value = new Date();
        lifecycleSubscriptions.add(
                realm
                        .where(DailyReport.class)
                        .greaterThan("date", value.getTime())
                        .findAllSorted("date", Sort.ASCENDING)
                        .asObservable()
                        .subscribe(dailyReports -> {
                            updateDailyReports(realm, componentContext, recyclerBinder, dailyReports);
                        })
        );


        final Component<Recycler> recyclerComponent = Recycler.create(componentContext)
                .binder(recyclerBinder)
                .build();


        dailyLithoView.setComponent(recyclerComponent);
        dailyLithoView.getLayoutParams().height = ((int) (getResources().getDisplayMetrics().density * DAILY_TERM_COMPRESSED_ITEM_COUNT * DAILY_VIEW_ITEM_HEIGHT_DP));
        dailyLithoView.requestLayout();
    }

    private void updateDailyReports(Realm realm, ComponentContext componentContext, RecyclerBinder recyclerBinder, RealmResults<DailyReport> dailyReports) {
        updateRecycler = (isExpanded) -> {
            int itemCount = isExpanded ?
                    dailyReports.size() :
                    Math.min(DAILY_TERM_COMPRESSED_ITEM_COUNT, dailyReports.size());
            if (itemCount < recyclerBinder.getItemCount() && recyclerBinder.getItemCount() > 0) {
                recyclerBinder.removeRangeAt(Math.max(0, itemCount - 1), Math.max(0,  recyclerBinder.getItemCount() - itemCount));
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
        updateRecycler.call(isExpanded);
    }

    void toggleDailyViewExpansion() {
        if (updateRecycler == null) {
            return;
        }

        isExpanded = !isExpanded;


        dailyLithoView.animate()
                .scaleY(1f)
                .setUpdateListener((value) -> {
                    final float scale = getResources().getDisplayMetrics().density;
                    float startingHeight = isExpanded ?
                            DAILY_TERM_VIEW_COMPRESSED_HEIGHT :
                            DAILY_TERM_VIEW_EXPANDED_HEIGHT;
                    float targetHeight = isExpanded ?
                            DAILY_TERM_VIEW_EXPANDED_HEIGHT :
                            DAILY_TERM_VIEW_COMPRESSED_HEIGHT;
                    float delta = targetHeight - startingHeight;

                    int currentDelta = (int) (delta * (1.0 - value.getAnimatedFraction()));

                    dailyLithoView.getLayoutParams().height = (int) (scale * (targetHeight - currentDelta));
                    dailyLithoView.requestLayout();
                })
                //If the list is expanding, immediately add new items, otherwise wait until animation finishes
                .withStartAction(() -> {
                    if (isExpanded) {
                        updateRecycler.call(isExpanded);
                    }
                })
                .withEndAction(() -> {
                    if (!isExpanded) {
                        updateRecycler.call(isExpanded);
                    }
                })
                .setDuration(500);
    }
}
