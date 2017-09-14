package com.selaliadobor.darkskyweather.apis.darksky.responseobjects;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a Data Point response from Dark Sky api.
 *
 * @apiNote Only valid for hourly and daily values
 */
@AutoValue
public abstract class DataPoint {

    @SerializedName("time")
    public abstract double time();

    @Nullable
    @SerializedName("apparentTemperatureHigh")
    public abstract Double apparentTemperatureHigh();

    @Nullable
    @SerializedName("apparentTemperatureHighTime")
    public abstract Double apparentTemperatureHighTime();

    @Nullable
    @SerializedName("apparentTemperatureLow")
    public abstract Double apparentTemperatureLow();

    @Nullable
    @SerializedName("apparentTemperatureLowTime")
    public abstract Double apparentTemperatureLowTime();

    @Nullable
    @SerializedName("moonPhase")
    public abstract Double moonPhase();

    @Nullable
    @SerializedName("icon")
    public abstract String icon();

    @Nullable
    @SerializedName("summary")
    public abstract String summary();

    public static TypeAdapter<DataPoint> typeAdapter(Gson gson) {
        return new AutoValue_DataPoint.GsonTypeAdapter(gson);
    }
}
