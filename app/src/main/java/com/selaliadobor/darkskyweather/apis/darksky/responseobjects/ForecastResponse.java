package com.selaliadobor.darkskyweather.apis.darksky.responseobjects;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents a Data Block response from Dark Sky api.
 */
@AutoValue
public abstract class ForecastResponse {
    public static TypeAdapter<ForecastResponse> typeAdapter(Gson gson) {
        return new AutoValue_ForecastResponse.GsonTypeAdapter(gson);
    }

    @Nullable
    @SerializedName("latitude")
    public abstract String latitude();

    @Nullable
    @SerializedName("longitude")
    public abstract String longitude();

    @Nullable
    @SerializedName("timezone")
    public abstract String timezone();

    @Nullable
    @SerializedName("icon")
    public abstract String icon();

    @Nullable
    @SerializedName("hourly")
    public abstract DataBlock hourly();

    @Nullable
    @SerializedName("daily")
    public abstract DataBlock daily();
}
