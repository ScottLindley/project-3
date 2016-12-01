package com.scottlindley.touchmelabs;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class CurrentWeather extends CardContent{
    private String mTemperature;
    LatLng mLocation;

    public CurrentWeather(String title, String content, String temperature, LatLng location) {
        super(title, content);
        mTemperature = temperature;
        mLocation = location;
    }

    public void setTitle(String cityName){
        mTitle = cityName;
    }

    public void setContent(String conditions){
        mContent = conditions;
    }

    public void setTemperature(String temperature) {
        mTemperature = temperature;
    }

    public void setLocation(LatLng location) {
        mLocation = location;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }
}
