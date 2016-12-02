package com.scottlindley.touchmelabs.ModelObjects;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class CurrentWeather extends CardContent {
    private String mTemperature;

    public CurrentWeather(String title, String content, String temperature) {
        super(title, content);
        mTemperature = temperature;
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
}
