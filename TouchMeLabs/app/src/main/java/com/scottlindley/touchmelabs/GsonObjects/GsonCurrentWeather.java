package com.scottlindley.touchmelabs.GsonObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class GsonCurrentWeather {
    private String name;
    private List<GsonWeather> weather = new ArrayList<>();
    private GsonMain main;

    public GsonCurrentWeather(String name, List<GsonWeather> weather, GsonMain main) {
        this.name = name;
        this.weather = weather;
        this.main = main;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setMain(GsonMain main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public List<GsonWeather> getWeather() {
        return weather;
    }

    public void setWeather(List<GsonWeather> weather) {
        this.weather = weather;
    }

    public GsonMain getMain() {
        return main;
    }
}
