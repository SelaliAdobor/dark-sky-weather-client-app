package com.selaliadobor.darkskyweather.job;


public class RetrieveWeatherJobSetupException extends Exception {

    RetrieveWeatherJobSetupException(String message) {
        super(message);
    }

    RetrieveWeatherJobSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrieveWeatherJobSetupException(Throwable cause) {
        super(cause);
    }
}
