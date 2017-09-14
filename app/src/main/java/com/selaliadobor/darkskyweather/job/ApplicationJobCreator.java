package com.selaliadobor.darkskyweather.job;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import timber.log.Timber;


public class ApplicationJobCreator implements JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag){
            case RetrieveWeatherJob.JOB_TAG:
                return new RetrieveWeatherJob();
            default:
                Timber.e("No Job defined for tag: " + tag);
                return new Job() {
                    @NonNull
                    @Override
                    protected Result onRunJob(Params params) {
                        return Result.FAILURE;
                    }
                };
        }
    }
}
