package com.scottlindley.touchmelabs.Services;

import com.scottlindley.touchmelabs.GsonObjects.GsonCurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public interface OpenWeatherMapService {

    String string = "api.openweathermap.org/data/2.5/weather?lat=35&lon=139";
    @GET("weather")
    Call<GsonCurrentWeather> getWeatherByLongLat(
            @Query("apikey") String apiKey,
            @Query("lat") String lat,
            @Query("lon") String lon);
    Call<GsonCurrentWeather> getWeatherByZip(
            @Query("apikey") String apiKey,
            @Query("zip") String zip);
}
