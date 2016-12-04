package com.scottlindley.touchmelabs.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.scottlindley.touchmelabs.GsonObjects.GsonNewsStory;
import com.scottlindley.touchmelabs.ModelObjects.NewsStory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class NewsService extends JobService implements NewsXmlParser.ParseFinishedListener {
    private static final String mBaseURL = "http://api.smmry.com";
    private JobParameters mJobParameters;
    private NewsXmlParser mParser;
    private List<String> mStoryLinks;
    private List<GsonNewsStory> mGsonStories;
    private static List<NewsStory> mStories;
    private String[] mTitles;
    private String[] mSummaries;
    private String[] mLinks;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mGsonStories = new ArrayList<>();
        mStories = new ArrayList<>();
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
        //These conditionals limit the number of articles to 8
        if(mStoryLinks.size()<=8) {
            for (String link : mStoryLinks) {
                makeRetroFitCall(link, mJobParameters);
            }
        }else{
            for (int i=0; i<8; i++){
                makeRetroFitCall(mStoryLinks.get(i), mJobParameters);
            }
        }
    }

    //Makes a call using retrofit with a single story url
    private void makeRetroFitCall(final String link, final JobParameters jobParameters){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SmmryService service = retrofit.create(SmmryService.class);
        Call<GsonNewsStory> call = service.getSummaryLength3(link);
        call.enqueue(new Callback<GsonNewsStory>() {
            @Override
            public void onResponse(Call<GsonNewsStory> call, Response<GsonNewsStory> response) {
                if(response.isSuccessful()) {
                    //Takes the GsonStory response and adds it to mStories
                    GsonNewsStory gsonStory = (response.body());
                    mGsonStories.add(gsonStory);
                    mStories.add(new NewsStory(
                            gsonStory.getTitle(),
                            gsonStory.getContent(),
                            link
                    ));

                    if(mStories.size()==8) {
                        convertStoryToStringArrays();

                        Intent intent = new Intent("service intent");
                        intent.putExtra("titles", mTitles);
                        intent.putExtra("summaries", mSummaries);
                        intent.putExtra("links", mLinks);
                        intent.putExtra("service name", "news service");

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        jobFinished(jobParameters, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<GsonNewsStory> call, Throwable t) {
//                Intent intent = new Intent("service intent");
//                intent.putExtra("service name", "failure");
//
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                jobFinished(jobParameters, false);
            }
        });
    }

    public void convertStoryToStringArrays(){
        int arraySize = mStories.size();

        mTitles = new String[arraySize];
        mSummaries = new String[arraySize];
        mLinks = new String[arraySize];


        for (int i=0; i<mStories.size(); i++){
            mTitles[i] = mStories.get(i).getTitle();
            mSummaries[i] = mStories.get(i).getContent();
            mLinks[i] = mStories.get(i).getURL();
        }
    }

}
