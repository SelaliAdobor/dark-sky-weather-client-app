package com.selaliadobor.darkskyweather.apis.darksky;

import com.selaliadobor.darkskyweather.data.WeatherType;

import java.util.HashMap;
import java.util.Map;


public class DarkSkyWeatherServiceUtil {
    private static Map<String,WeatherType> iconToWeatherTypeMap;

    static {

        iconToWeatherTypeMap = new HashMap<>();

        iconToWeatherTypeMap.put("clear-day",WeatherType.DAY);
        iconToWeatherTypeMap.put("clear-night",WeatherType.NIGHT);
        iconToWeatherTypeMap.put("rain",WeatherType.RAIN);
        iconToWeatherTypeMap.put("snow",WeatherType.SNOW);
        iconToWeatherTypeMap.put("sleet",WeatherType.SLEET);
        iconToWeatherTypeMap.put("wind",WeatherType.WIND);
        iconToWeatherTypeMap.put("fog",WeatherType.FOG);
        iconToWeatherTypeMap.put("cloudy",WeatherType.CLOUDY);
        iconToWeatherTypeMap.put("partly-cloudy-day",WeatherType.PARTLY_CLOUDY_DAY);
        iconToWeatherTypeMap.put("partly-cloudy-night",WeatherType.PARTLY_CLOUDY_NIGHT);
    }

    /**
     * Convert from a DarkSky API "icon" response to a @{@link WeatherType}
     * @param icon An icon type from the Dark Sky API
     * @return An weather type matching the given icon, or {@link WeatherType#UNKNOWN} if not found
     */
    public static WeatherType weatherTypeFromIcon(String icon){
        return iconToWeatherTypeMap.containsKey(icon) ?
                iconToWeatherTypeMap.get(icon) :
                WeatherType.UNKNOWN;
    }
}
