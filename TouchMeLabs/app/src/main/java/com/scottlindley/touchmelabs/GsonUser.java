package com.scottlindley.touchmelabs;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class GsonUser {
    private String name, screen_name;

    public GsonUser(String name, String screen_name) {
        this.name = name;
        this.screen_name = screen_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return screen_name;
    }
}
