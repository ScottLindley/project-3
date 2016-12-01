package com.scottlindley.touchmelabs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.id.list;


/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class NewsService extends JobService implements NewsXmlParser.ParseFinishedListener{
    private static final String mBaseURL = "http://api.smmry.com";
    private JobParameters mJobParameters;
    private NewsXmlParser mParser;
    private List<String> mStoryLinks;
    private List<GsonStory> mGsonStories;
    private static List<NewsStory> mStories;
    private String[] mTitles;
    private String[] mSummary;
    private String[] mLinks;
    private int mFailedResponses = 0;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mGsonStories = new ArrayList<GsonStory>();
        mStories = new ArrayList<NewsStory>();
        mJobParameters = jobParameters;

        //See NewsXmlParser class
        mParser = new NewsXmlParser(this);
        mParser.getXML();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    //Implemented from OnParseFinished interface, only takes action once NewsXmlParser is finished
    @Override
    public void onXmlParseFinished() {
        mStoryLinks = mParser.getStoryLinks();
        //Make a Smmry api call for each story link
        for (String link : mStoryLinks){
            makeRetroFitCall(link, mJobParameters);
        }
    }

    //Makes a call using retrofit with a single story url
    private void makeRetroFitCall(final String link, final JobParameters jobParameters){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SmmryService service = retrofit.create(SmmryService.class);
        Call<GsonStory> call = service.getSummaryLength3(link);
        call.enqueue(new Callback<GsonStory>() {
            @Override
            public void onResponse(Call<GsonStory> call, Response<GsonStory> response) {
                if(response.isSuccessful()) {
                    //Takes the GsonStory response and adds it to mStories
                    GsonStory gsonStory = (response.body());
                    mGsonStories.add(gsonStory);
                    mStories.add(new NewsStory(
                            gsonStory.getTitle(),
                            gsonStory.getContent(),
                            link
                    ));
                    //This checks if all links have been run through a retrofit call (successful or not)
                    if (mGsonStories.size() + mFailedResponses == mStoryLinks.size()) {
                        if (mFailedResponses == 0) {
                            convertStoryToStringArrays();

                            Intent intent = new Intent("service intent");
                            intent.putExtra("titles", mTitles);
                            intent.putExtra("summaries", mSummary);
                            intent.putExtra("links", mLinks);
                            intent.putExtra("service name", "news service");

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            jobFinished(jobParameters, false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GsonStory> call, Throwable t) {
                t.printStackTrace();
                mFailedResponses++;
                //This checks if all links have been run through a retrofit call (successful or not)
                if(mGsonStories.size()+mFailedResponses == mStoryLinks.size()){
                    Intent intent = new Intent("service intent");
                    intent.putExtra("service name", "news service");
                    intent.putExtra("failure", "NewsService Failed");

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    jobFinished(jobParameters, false);
                }
            }

        });
    }

    public void convertStoryToStringArrays(){
        for (int i=0; i<mStories.size(); i++){
            mTitles[i] = mStories.get(i).getTitle();
            mSummary[i] = mStories.get(i).getContent();
            mLinks[i] = mStories.get(i).getURL();
        }
    }

}
