package com.selaliadobor.darkskyweather.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HourlyReport extends RealmObject {
    @PrimaryKey
    double date;

    String zipCode;

    double highTemp;

    double lowTemp;

    String summary;

    WeatherType type;
}
