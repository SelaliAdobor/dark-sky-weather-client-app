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
public abstract class DataBlock {

    @SerializedName("data")
    public abstract List<DataPoint> data();

    @Nullable
    @SerializedName("summary")
    public abstract String summary();

    @Nullable
    @SerializedName("icon")
    public abstract String icon();

    public static TypeAdapter<DataBlock> typeAdapter(Gson gson) {
        return new AutoValue_DataBlock.GsonTypeAdapter(gson);
    }
}
