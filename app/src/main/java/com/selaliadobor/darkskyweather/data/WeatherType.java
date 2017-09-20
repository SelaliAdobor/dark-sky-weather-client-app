package com.selaliadobor.darkskyweather.data;

import com.selaliadobor.darkskyweather.R;

/**
 * Represents the type of weather dominant for a given time period
 */
public enum WeatherType {
    UNKNOWN(R.drawable.unknown),
    DAY(R.drawable.day),
    NIGHT(R.drawable.night),
    RAIN(R.drawable.rain),
    SNOW(R.drawable.snow),
    SLEET(R.drawable.sleet),
    WIND(R.drawable.wind),
    FOG(R.drawable.fog),
    CLOUDY(R.drawable.cloudy),
    PARTLY_CLOUDY_DAY(R.drawable.partly_cloudy_day),
    PARTLY_CLOUDY_NIGHT(R.drawable.partly_cloudy_night);

    private int drawableId;

    public int getDrawableId() {
        return drawableId;
    }

    WeatherType(int drawableId) {
        this.drawableId = drawableId;
    }
}
