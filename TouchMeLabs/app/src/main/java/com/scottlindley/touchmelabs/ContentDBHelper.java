package com.scottlindley.touchmelabs;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

public class ContentDBHelper extends SQLiteOpenHelper {
    private Context mContext;
    private static final int NEWS_SERVICE = 3;
    private static final int TWITTER_SERVICE = 9;

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
        mContext = context;
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

    /**
     * Uses helper methods {@link #clearTable(SQLiteDatabase, String)},
     * {@link #addTweet(SQLiteDatabase, String, String, String, String, String)}, and
     * {@link #addNews(SQLiteDatabase, String, String, String)} to clear the news and tweets tables
     * in the local SQLite database.
     *
     * {@link GoogleNewsService} sends String arrays to be converted into {@link NewsStory} objects
     * {@link TwitterService} sends String arrays to be converted into {@link TweetInfo} objects
     *
     * This method should never be called from the UI thread. To access the new tables,
     * see {@link #getNews()} and {@link #getTweets()}
     */

    private void refreshDB() {
        startRefreshService();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<CardContent> newContent = new ArrayList<>();

                Bundle newsInfo = intent.getExtras();
                String key = newsInfo.getString("service name");

                SQLiteDatabase db = getWritableDatabase();
                switch(key) {
                    case "news service":
                        clearTable(db, TABLE_NEWS);

                        String[] titles = newsInfo.getStringArray("titles");
                        String[] summaries = newsInfo.getStringArray("summaries");
                        String[] links = newsInfo.getStringArray("links");


                        for(int i=0;i<titles.length;i++) {
                            String title = titles[i];
                            String summary = summaries[i];
                            String link = links[i];

                            if(addNews(db, title, summary, link) != -1) {
                                NewsStory story = new NewsStory(title, summary, link);
                                newContent.add(story);
                            }
                        }
                        db.close();
                        break;
                    case "twitter service":
                        clearTable(db, TABLE_TWEETS);

                        String[] handles = newsInfo.getStringArray("handles");
                        String[] usernames = newsInfo.getStringArray("usernames");
                        String[] tweets = newsInfo.getStringArray("tweets");
                        String[] times = newsInfo.getStringArray("times");
                        String[] ids = newsInfo.getStringArray("ids");

                        for(int i=0;i<handles.length;i++) {
                            String handle = handles[i];
                            String username = usernames[i];
                            String tweet = tweets[i];
                            String time = times[i];
                            String id = ids[i];

                            if(addTweet(db, handle, tweet, username, time, id) != -1) {
                                TweetInfo info = new TweetInfo(handle, username, tweet, time, id);
                                newContent.add(info);
                            }
                        }
                        db.close();
                        break;
                    default:

                }
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter("service intent"));
    }

    /**
     * Combines {@value TABLE_NEWS} and {@value TABLE_TWEETS} into a list of {@link CardContent}s.
     *
     * @param weather will set the element at index 0 to the weather card displayed first
     * @return a combined list of all tweet, news, and weather objects being
     * displayed
     */
    public List<CardContent> getUpdatedCardList(CurrentWeather weather) {
        refreshDB();
        List<CardContent> cards = new ArrayList<>();
        cards.add(weather);
        SQLiteDatabase db = getReadableDatabase();
        Cursor tweets = db.query(TABLE_TWEETS,
                null,
                null,
                null,
                null,
                null,
                null);
        Cursor news = db.query(TABLE_NEWS,
                null,
                null,
                null,
                null,
                null,
                null);
        combineResults(cards, tweets, news);
        return cards;
    }

    public List<NewsStory> getNews() {
        List<NewsStory> stories = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NEWS,
                null,
                null,
                null,
                null,
                null,
                null);
        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                String title = cursor.getString(cursor.getColumnIndex(COL_NAME));
                String summary = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                String link = cursor.getString(cursor.getColumnIndex(COL_URL));
                stories.add(new NewsStory(title, summary, link));

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return stories;
    }

    public List<TweetInfo> getTweets() {
        List<TweetInfo> tweets = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TWEETS,
                null,
                null,
                null,
                null,
                null,
                null);
        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                String handle = cursor.getString(cursor.getColumnIndex(COL_NAME));
                String username = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                String tweet = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                String time = cursor.getString(cursor.getColumnIndex(COL_TIME));
                String id = cursor.getString(cursor.getColumnIndex(COL_ID));

                tweets.add(new TweetInfo(handle, tweet, username, time, id));
                cursor.moveToNext();
            }
        }
        db.close();
        cursor.close();
        return tweets;
    }

    private void clearTable(SQLiteDatabase db, String table) {
        int removed = db.delete(table,"1",null);
        if(removed < 1) {
            //
        }
    }

    /**
     * Insert a new row into the tweets table
     * @param db
     * @param handle
     * @param tweet
     * @param username
     * @param time
     * @param id
     * @return The row number that was inserted, or -1 if nothing was inserted
     */
    private long addTweet(SQLiteDatabase db, String handle, String tweet, String username, String time, String id){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, handle);
        values.put(COL_CONTENT, tweet);
        values.put(COL_USERNAME, username);
        values.put(COL_TIME, time);
        values.put(COL_ID, id);
        return db.insertOrThrow(TABLE_TWEETS, null, values);
    }

    private long addNews(SQLiteDatabase db, String title, String summary, String link) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, title);
        values.put(COL_CONTENT, summary);
        values.put(COL_URL, link);
        return db.insertOrThrow(TABLE_NEWS, null, values);
    }

    private void combineResults(List<CardContent> cards, Cursor tweetCursor, Cursor newsCursor) {
        int count = 1;
        if(tweetCursor.moveToFirst()&&newsCursor.moveToFirst()) {
            while(!tweetCursor.isAfterLast()) {
                if(count%3==0) {
                    String title = newsCursor.getString(newsCursor.getColumnIndex(COL_NAME));
                    String summary = newsCursor.getString(newsCursor.getColumnIndex(COL_CONTENT));
                    String link = newsCursor.getString(newsCursor.getColumnIndex(COL_URL));
                    cards.add(new NewsStory(title, summary, link));
                } else {
                    String handle = tweetCursor.getString(tweetCursor.getColumnIndex(COL_NAME));
                    String tweet = tweetCursor.getString(tweetCursor.getColumnIndex(COL_CONTENT));
                    String username = tweetCursor.getString(tweetCursor.getColumnIndex(COL_USERNAME));
                    String time = tweetCursor.getString(tweetCursor.getColumnIndex(COL_TIME));
                    String id = tweetCursor.getString(tweetCursor.getColumnIndex(COL_ID));
                    cards.add(new TweetInfo(handle, tweet, username, time, id));
                }
            }
        }
        tweetCursor.close();
        newsCursor.close();
    }

    private void startRefreshService() {
        JobScheduler jobScheduler = (JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo newsInfo = new JobInfo.Builder(NEWS_SERVICE, new ComponentName(mContext, NewsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        JobInfo twitterInfo = new JobInfo.Builder(TWITTER_SERVICE, new ComponentName(mContext, TwitterService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        jobScheduler.schedule(newsInfo);
        jobScheduler.schedule(twitterInfo);
    }
}
