package com.scottlindley.touchmelabs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.handle;
import static android.R.attr.name;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class WeatherService extends JobService{
    private static final String WEATHER_BASE_URL = "api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "8125261db99aefc2183578b967646acc";


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //Pulls the location from the jobParameters
        PersistableBundle bundle = jobParameters.getExtras();
        String zip = bundle.getString("zip");
        String[] latLong = bundle.getStringArray("long lat");
        /*One of these will be null, the other will contain location info.

        If zip isn't null then the user has declined to share their current location
        and instead provided their zip code.

        If latLong isn't null then the user is sharing their current location and data
        is coming through as a String[] with longitude and latitude values.
        */
        if(zip != null && latLong == null){
            requestWithZip(zip, jobParameters);
        } else if (zip == null && latLong != null){
            requestWithLongLat(latLong, jobParameters);
        } else {
            jobFinished(jobParameters, false);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void requestWithZip(String zip, final JobParameters jobParameters){
        //Build retrofit request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);
        //Make the api call using the provided zip code
        Call<GsonCurrentWeather> call = service.getWeatherByZip(API_KEY, zip);
        call.enqueue(new Callback<GsonCurrentWeather>() {
            @Override
            public void onResponse(Call<GsonCurrentWeather> call, Response<GsonCurrentWeather> response) {
                if(response.isSuccessful()){
                    //See helper method
                    handleResponse();
                    jobFinished(jobParameters, false);
                }
            }

            @Override
            public void onFailure(Call<GsonCurrentWeather> call, Throwable t) {
                t.printStackTrace();
                //Send a name of 'failure' that will trigger the default case in the BroadcastReceiver
                Intent intent = new Intent("service intent");
                intent.putExtra("service name", "failure");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                jobFinished(jobParameters, false);
            }
        });
    }

    public void requestWithLongLat(String[] latLong, final JobParameters jobParameters){
        String latitude = latLong[0];
        String longitude = latLong[1];

        //Build retrofit request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);
        Call<GsonCurrentWeather> call = service.getWeatherByLongLat(API_KEY, latitude, longitude);
        call.enqueue(new Callback<GsonCurrentWeather>() {
            @Override
            public void onResponse(Call<GsonCurrentWeather> call, Response<GsonCurrentWeather> response) {
                if(response.isSuccessful()){
                    //see helper method
                    handleResponse();
                    jobFinished(jobParameters, false);
                }
            }

            @Override
            public void onFailure(Call<GsonCurrentWeather> call, Throwable t) {
                //Send a name of 'failure' that will trigger the default case in the BroadcastReceiver
                Intent intent = new Intent("service intent");
                intent.putExtra("service name", "failure");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                jobFinished(jobParameters, false);
            }
        });
    }

    public void handleResponse(){
        /*Regardless of the api call (zip or longitude/latitude), the pieces we need in the JSON respose
        are identical. Therefore we can use this block of code for both responses.
        */

        GSonCurrentWeather gsonWeather = response.body();
        String cityName = gsonWeather.getName();
        String description = gsonWeather.getWeather().getDescription();
        String temperature = gsonWeather.getMain().getTemp();

        Intent intent = new Intent("service intent");
        intent.putExtra("service name", "weather service");
        intent.putExtra("city name", cityName);
        intent.putExtra("description", description);
        intent.putExtra("temperature", temperature);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }
}
