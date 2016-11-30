package com.scottlindley.touchmelabs;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Scott Lindley on 11/30/2016.
 */

public class NewsService extends JobService implements NewsXmlParser.ParseFinishedListener{
    private static final String mBaseURL = "http://api.smmry.com";
    private NewsXmlParser mParser;
    private List<String> mStoryLinks;
    private List<GsonStory> mStories;
    private int mFailedResponses = 0;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mParser = new NewsXmlParser(this);
        mParser.getXML();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    //Implemented from OnParseFinished interface, only takes action once NewsXmlParser is finished
    @Override
    public void onXmlParseFinished() {
        mStoryLinks = mParser.getStoryLinks();
        for (String link : mStoryLinks){
            makeRetroFitCall(link);
        }
    }

    //Makes a call using retrofit with a single story url
    private void makeRetroFitCall(final String link){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SmmryService service = retrofit.create(SmmryService.class);
        Call<GsonStory> call = service.getSummaryLength3(link);
        call.enqueue(new Callback<GsonStory>() {
            @Override
            public void onResponse(Call<GsonStory> call, Response<GsonStory> response) {
                //Takes the GsonStory response and adds it to mStories
                mStories.add(response.body());
                if(mStories.size()+mFailedResponses == mStoryLinks.size()){
                    //TODO: call DBHelper method here to send completed list of stories
                }
            }

            @Override
            public void onFailure(Call<GsonStory> call, Throwable t) {
                t.printStackTrace();
                mFailedResponses++;
                if(mStories.size()+mFailedResponses == mStoryLinks.size()){
                    //TODO: call DBHelper method here to send completed list of stories
                }
            }

        });
    }
}
