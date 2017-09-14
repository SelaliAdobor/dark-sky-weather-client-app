package com.selaliadobor.darkskyweather.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represents a weather report with hourly granularity
 */
public class HourlyReport extends RealmObject {
    @PrimaryKey
    long date;

    String zipCode;

    double temperature;

    String summary;

    String weatherType;

    public HourlyReport() {
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public WeatherType getWeatherType() {
        return WeatherType.valueOf(weatherType);
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType.name();
    }
}
