package com.scottlindley.touchmelabs.GsonObjects;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class GsonCurrentWeather {
    private String name;
    private GsonWeather weather;
    private GsonMain main;

    public GsonCurrentWeather(String name, GsonWeather weather, GsonMain main) {
        this.name = name;
        this.weather = weather;
        this.main = main;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeather(GsonWeather weather) {
        this.weather = weather;
    }

    public void setMain(GsonMain main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public GsonWeather getWeather() {
        return weather;
    }

    public GsonMain getMain() {
        return main;
    }
}
