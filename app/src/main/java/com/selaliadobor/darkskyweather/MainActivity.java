package com.selaliadobor.darkskyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.selaliadobor.darkskyweather.search.SearchFragment;

public class MainActivity extends AppCompatActivity {


    public static final String SEARCH_FRAGMENT_TAG = "com.selaliadobor.darkskyweather.search.SearchFragment";

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SearchFragment searchFragment = null;

        if(savedInstanceState != null){
            searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
        }

        if(searchFragment == null){
            searchFragment = SearchFragment.create();
        }

        getSupportFragmentManager()
                .beginTransaction()
                 .replace(R.id.content, searchFragment, SEARCH_FRAGMENT_TAG)
                 .addToBackStack(null)
                 .commit();
    }

}
