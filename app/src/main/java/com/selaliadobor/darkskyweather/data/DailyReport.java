package com.selaliadobor.darkskyweather.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DailyReport extends RealmObject {
    public double getDate() {
        return date;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public String getZipCode() {
        return zipCode;
    }

    public double getHighTemp() {
        return highTemp;
    }

    public double getLowTemp() {
        return lowTemp;
    }


    public String getSummary() {
        return summary;
    }

    @PrimaryKey
    double date;

    WeatherType weatherType;

    String zipCode;

    String summary;

    double highTemp;

    double lowTemp;
}