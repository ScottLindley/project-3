package com.scottlindley.touchmelabs;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class OAuthManager {
    String mBearerToken;

    public String obtainBearerToken() {

        String credentials = TwitterAppInfo.CONSUMER_KEY+ ":" + TwitterAppInfo.CONSUMER_SECRET;
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        OkHttpClient client = new OkHttpClient();
        Headers headers = new Headers.Builder()
                .add("Authorization", "Basic " + encodedCredentials)
                .add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();
        final Request request = new Request.Builder()
                .url("https://api.twitter.com/oauth2/token")
                .headers(headers)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    throw new IOException("Unexpected code: "+response);
                }
                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    mBearerToken = responseObject.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return mBearerToken;
    }
}
