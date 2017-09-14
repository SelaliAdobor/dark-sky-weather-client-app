package com.selaliadobor.darkskyweather.apis.darksky;

import com.selaliadobor.darkskyweather.apis.darksky.responseobjects.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DarkSkyWeatherService {

    @GET("forecast/{apiKey}/{latitude},{longitude}")
    Call<ForecastResponse> listEvents(@Path("apiKey")String apiKey,@Path("latitude") double latitude, @Path("longitude") double longitude);

}
