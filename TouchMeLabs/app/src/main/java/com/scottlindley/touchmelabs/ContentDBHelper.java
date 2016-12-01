package com.scottlindley.touchmelabs;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ContentDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "content";
    private static final int DB_VERSION = 1;

    //Table names for news and twitter API responses
    private static final String TABLE_NEWS = "news";
    private static final String TABLE_TWEETS = "tweets";

    //Column names for tables
    private static final String COL_NAME = "name";
    private static final String COL_CONTENT = "content";
    private static final String COL_URL = "url";
    private static final String COL_USERNAME = "username";
    private static final String COL_TIME = "time";
    private static final String COL_ID = "id";

    //Create news table
    private static final String CREATE_NEWS_TABLE = "CREATE TABLE "
            +TABLE_NEWS+"("
            +COL_URL+" TEXT PRIMARY KEY,"
            +COL_NAME+" TEXT,"
            +COL_CONTENT+" TEXT)";

    //Create tweet table
    private static final String CREATE_TWEETS_TABLE = "CREATE TABLE "
            +TABLE_TWEETS+"("
            +COL_NAME+" TEXT PRIMARY KEY, "
            +COL_CONTENT+" TEXT, "
            +COL_USERNAME+" TEXT, "
            +COL_TIME+" TEXT, "
            +COL_ID+" TEXT";

    private ContentDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static ContentDBHelper sInstance = null;

    public static ContentDBHelper getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new ContentDBHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NEWS_TABLE);
        sqLiteDatabase.execSQL(CREATE_TWEETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS "+TABLE_TWEETS);
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS "+TABLE_NEWS);
        onCreate(sqLiteDatabase);
    }

    public List<CardContent> refreshList() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<CardContent> newContent = new ArrayList<>();

                Bundle newsInfo = intent.getExtras();
                String key = newsInfo.getString("service name");

                switch(key) {
                    case "news service":
                        String[] titles = newsInfo.getStringArray("titles");
                        String[] summaries = newsInfo.getStringArray("summaries");
                        String[] links = newsInfo.getStringArray("links");

                        for(int i=0;i<titles.length;i++) {
                            String title = titles[i];
                            String summary = summaries[i];
                            String link = links[i];

                            if(addNews(title, summary, link) != -1) {
                                NewsStory story = new NewsStory(title, summary, link);
                                newContent.add(story);
                            }
                        }
                        break;
                    case "twitter service":
                        String[] handles = newsInfo.getStringArray("handles");
                        String[] usernames = newsInfo.getStringArray("usernames");
                        String[] tweets = newsInfo.getStringArray("tweets");
                        String[] times = newsInfo.getStringArray("times");
                        String[] ids = newsInfo.getStringArray("ids");
                        break;
                    default:

                }
            }
        };
        return newContent;
    }

//    public List<NewsStory> getNews() {
//    }
//
//    public List<TweetInfo> getTweets() {
//    }

    private void clearTable(String table) {
    }

    private long addTweet(String handle, String tweet, ){
    }

    private long addNews(String title, String summary, String link) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, title);
        values.put(COL_CONTENT, summary);
        values.put(COL_URL, link);
        long inserted = db.insertOrThrow(TABLE_NEWS, null, values);
        return inserted;
    }
}
