package com.scottlindley.touchmelabs;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class TweetInfo extends CardContent{
    private String mUsername, mTime, mId;

    public TweetInfo(String title, String content, String username, String time, String id) {
        super(title, content);
        mUsername = username;
        mTime = time;
        mId = id;
    }

    public void setTitle(String title){
        mTime = title;
    }

    public void setContent(String tweetText){
        mContent = tweetText;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getTime() {
        return mTime;
    }

    public String getId() {
        return mId;
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
