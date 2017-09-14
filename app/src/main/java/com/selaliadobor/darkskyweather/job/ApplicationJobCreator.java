package com.selaliadobor.darkskyweather.job;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;


public class ApplicationJobCreator implements JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag){
            case RetrieveWeatherJob.JOB_TAG:
                return new RetrieveWeatherJob();
            default:
                throw new RuntimeException("No Job defined for tag: "+tag);
        }
    }
}
