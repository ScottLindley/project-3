package com.scottlindley.touchmelabs;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public abstract class CardContent {
    public String mTitle, mContent;

    public CardContent(String title, String content) {
        mTitle = title;
        mContent = content;
    }

    public abstract String getTitle();

    public abstract String getContent();
}
