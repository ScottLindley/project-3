package com.scottlindley.touchmelabs.GsonObjects;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class GsonNewsStory {
    private String sm_api_title, sm_api_content;

    public GsonNewsStory(String sm_api_title, String sm_api_content) {
        this.sm_api_title = sm_api_title;
        this.sm_api_content = sm_api_content;
    }

    public void setSm_api_title(String sm_api_title) {
        this.sm_api_title = sm_api_title;
    }

    public void setSm_api_content(String sm_api_content) {
        this.sm_api_content = sm_api_content;
    }

    public String getTitle(){
        return sm_api_title;
    }

    public String getContent(){
        return sm_api_content;
    }
}
