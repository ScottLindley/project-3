package com.scottlindley.touchmelabs;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Scott Lindley on 11/30/2016.
 */

public interface SmmryService {

    //Here the API key is included because all requests require it
    @GET("?SM_API_KEY=4FBF7A1B12&SM_LENGTH=3&SM_URL={link}")
    Call<GsonNewsStory> getSummaryLength3(@Path("link") String link);
}
