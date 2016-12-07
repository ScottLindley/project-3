package com.scottlindley.Bundle.Services;

import com.scottlindley.Bundle.GsonObjects.GsonNewsStory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public interface SmmryService {

    //Here the API key is included because all requests require it
    @GET("?SM_API_KEY=4FBF7A1B12&SM_LENGTH=3")
    Call<GsonNewsStory> getSummaryLength3(@Query("SM_URL") String link);
}
