package com.scottlindley.touchmelabs;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class GsonTweetInfo {
    private String created_at, text;
    private long id;
    private GsonUser user;

    public GsonTweetInfo(String created_at, String text, long id, GsonUser user) {
        this.created_at = created_at;
        this.text = text;
        this.id = id;
        this.user = user;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(GsonUser user) {
        this.user = user;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public GsonUser getUser() {
        return user;
    }
}
