package com.scottlindley.touchmelabs;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class GsonWeather {
    private String description;

    public GsonWeather(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
