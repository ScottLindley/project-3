package com.scottlindley.touchmelabs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public interface TwitterAPIEndpointsService {

    @GET("?count=18")
    Call<GsonTwitterAPIResponse> get20Tweets(@Header("Authorization") String authorization);
}
