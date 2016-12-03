package com.scottlindley.touchmelabs.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class TwitterService extends JobService{
    private long userId;
    private static final String TAG = "TwitterService";

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterAppInfo.CONSUMER_KEY,TwitterAppInfo.CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig),new TweetUi());
        //Gets the userId of the user currently logged in
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        Call<User> userCall = Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false);
        userCall.enqueue(new com.twitter.sdk.android.core.Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User user = result.data;
                userId = user.getId();
                Log.d(TAG, "success: userID = "+userId);
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
                Log.d(TAG, "failure: Failed user call");
            }
        });

        //Gets the users timeline as a list of Tweet objects
        Call<List<Tweet>> timelineCall = Twitter.getApiClient(session).getStatusesService().homeTimeline(
                18, null, null, null, null, null, null);
        timelineCall.enqueue(new com.twitter.sdk.android.core.Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Creates string arrays with the same length as the result list
                int arraySize = result.data.size();
                String[] names = new String[arraySize];
                String[] times = new String[arraySize];
                String[] ids = new String[arraySize];
                String[] handles = new String[arraySize];
                String[] tweets = new String[arraySize];

                //Loop through and add data to String arrays
                //CustomUser and CustomTweet extent User and Tweet and contain getter methods to
                //grab the desired data
                for (int i = 0; i < result.data.size(); i++) {

                    names[i] = result.data.get(i).user.screenName;
                    handles[i] = result.data.get(i).user.name;
                    times[i] = result.data.get(i).createdAt;
                    tweets[i] = result.data.get(i).text;
                    ids[i] = String.valueOf(result.data.get(i).id);
                }

                //Put data into an intent and broadcast the intent
                Intent intent = new Intent("service intent");
                intent.putExtra("service name", "twitter service");
                intent.putExtra("usernames", names);
                intent.putExtra("handles", handles);
                intent.putExtra("times", times);
                intent.putExtra("ids", ids);
                intent.putExtra("tweets", tweets);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                jobFinished(jobParameters, false);
            }

            @Override
            public void failure(TwitterException exception) {
                //Send an intent with name 'failure' to trigger the default case in onReceive
                exception.printStackTrace();
                Log.d(TAG, "failure: Failed timeline call");
                Intent intent = new Intent("service intent");
                intent.putExtra("service name", "failure");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                jobFinished(jobParameters, false);
            }
        });

        return false;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
