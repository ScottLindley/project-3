package com.scottlindley.touchmelabs.Services;

import com.scottlindley.touchmelabs.GsonObjects.GsonCurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public interface OpenWeatherMapService {

    @GET("/data/2.5/weather")
    Call<GsonCurrentWeather> getWeatherByLongLat(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("appid") String appid,
            @Query("units") String units);
    @GET("/data/2.5/weather")
    Call<GsonCurrentWeather> getWeatherByZip(
            @Query("zip") String zip,
            @Query("appid") String appid,
            @Query("units") String units);
}
