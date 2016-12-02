package com.scottlindley.touchmelabs.ModelObjects;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class CurrentWeather extends CardContent {
    private String mTemperature;
    private String[] mLocation;
    private String mZip;
    private boolean mHasLocationPermission;

    public CurrentWeather(String title, String content, String temperature, String[] location) {
        super(title, content);
        mTemperature = temperature;
        mLocation = location;
        mHasLocationPermission = true;
    }

    public CurrentWeather(String title, String content, String temperature, String zip){
        super(title, content);
        mTemperature = temperature;
        mZip = zip;
        mHasLocationPermission = false;
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

    public void setLocation(String[] location) {
        mLocation = location;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getContent() {
        return mContent;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public String[] getLocation() {
        return mLocation;
    }

    public String getZip() {
        return mZip;
    }

    public boolean getHasLocationPermission() {
        return mHasLocationPermission;
    }
}
