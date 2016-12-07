package com.scottlindley.Bundle.ModelObjects;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class NewsStory extends CardContent {
    private String mURL;

    public NewsStory(String title, String content, String URL) {
        super(title, content);
        mURL = URL;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public void setContent(String summary){
        mContent = summary;
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    public String getURL() {
        return mURL;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getContent() {
        return mContent;
    }

}
