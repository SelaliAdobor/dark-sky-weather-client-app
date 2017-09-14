package com.selaliadobor.darkskyweather.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represents a weather report with daily granularity
 */
public class DailyReport extends RealmObject {
    @PrimaryKey
    long date;

    String weatherType;

    String zipCode;

    String summary;

    double highTemp;

    double lowTemp;

    public DailyReport(long date, WeatherType weatherType, String zipCode, String summary, double highTemp, double lowTemp) {
        this.date = date;
        this.weatherType = weatherType.name();
        this.zipCode = zipCode;
        this.summary = summary;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;

    }

    public DailyReport() {
    }

    public long getDate() {
        return date;
    }

    /**
     * Set the date the report represents.
     *
     * @param date The unix timestamp of the first moment of the day
     */
    public void setDate(long date) {
        this.date = date;
    }

    public WeatherType getWeatherType() {
        return WeatherType.valueOf(weatherType);
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType.name();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public double getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(double highTemp) {
        this.highTemp = highTemp;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(double lowTemp) {
        this.lowTemp = lowTemp;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}