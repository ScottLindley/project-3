package com.scottlindley.touchmelabs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class TwitterService extends JobService{
    public static final String TWITTER_BASE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private List<GsonTweetInfo> mGsonTweets;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mGsonTweets = new ArrayList<GsonTweetInfo>();

        //This pulls the bearer token out from the jobParameters
        PersistableBundle bundle = jobParameters.getExtras();
        String bearerToken = bundle.getString("bearer token");

        //Build a retrofit request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TWITTER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TwitterAPIEndpointsService service =
                retrofit.create(TwitterAPIEndpointsService.class);
        Call<GsonTwitterAPIResponse> call = service.get20Tweets("Bearer " + bearerToken);
        call.enqueue(new Callback<GsonTwitterAPIResponse>() {
            @Override
            public void onResponse(Call<GsonTwitterAPIResponse> call, Response<GsonTwitterAPIResponse> response) {
                if(response.isSuccessful()){
                    //This object is a GsonObject that contains a list of GsonTweetInfo objects
                    GsonTwitterAPIResponse gsonResponse = response.body();
                    mGsonTweets = gsonResponse.getGsonTweets();

                    //Make arrays that hold tweet info strings
                    int arraySize = mGsonTweets.size();
                    String[] names = new String[arraySize];
                    String[] times = new String[arraySize];
                    String[] ids = new String[arraySize];
                    String[] handles = new String[arraySize];
                    String[] tweets = new String[arraySize];

                    for(int i=0; i<mGsonTweets.size(); i++) {
                        names[i] = mGsonTweets.get(i).getUser.getName();
                        handles[i] = mGsonTweets.get(i).getUser.getHandle();
                        times[i] = mGsonTweets.get(i).getCreated_at();
                        ids[i] = mGsonTweets.get(i).getId();
                        tweets[i] = mGsonTweets.get(i).getText();
                    }

                    //Broadcast those string arrays through an intent to be received by the ContentDBHelper
                    Intent intent = new Intent ("service intent");
                    intent.putExtra("service name", "twitter service");
                    intent.putExtra("usernames", names);
                    intent.putExtra("handles", handles);
                    intent.putExtra("times", times);
                    intent.putExtra("ids", ids);
                    intent.putExtra("tweets", tweets);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    jobFinished(jobParameters, false);
                }
            }

            @Override
            public void onFailure(Call<GsonTwitterAPIResponse> call, Throwable t) {
                t.printStackTrace();
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
