package com.scottlindley.Bundle.Services;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.scottlindley.Bundle.GsonObjects.GsonCurrentWeather;
import com.scottlindley.Bundle.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class WeatherService extends JobService {
    private static final String TAG = "WeatherService";
    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/";
    private static final String API_KEY = "8125261db99aefc2183578b967646acc";
    private static final String IMPERIAL_UNITS = "imperial";


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: ");
        //Pulls the location from the jobParameters
        PersistableBundle bundle = jobParameters.getExtras();
        String zip = bundle.getString("zip");
        String latLong = bundle.getString("long lat");
        /*One of these will be null, the other will contain location info.

        If zip isn't null then the user has declined to share their current location
        and instead provided their zip code.

        If latLong isn't null then the user is sharing their current location and data
        is coming through as a String[] with longitude and latitude values.
        */
        if (zip != null && latLong == null) {
            requestWithZip(zip, jobParameters);
        } else if (zip == null && latLong != null) {
            requestWithLongLat(latLong, jobParameters);
        } else {
            Log.d(TAG, "onStartJob: BOTH NULL");
            jobFinished(jobParameters, false);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void requestWithZip(String zip, final JobParameters jobParameters) {
        //Build retrofit request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d(TAG, "requestWithZip: ");
        OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);
        //Make the api call using the provided zip code
        Call<GsonCurrentWeather> call = service.getWeatherByZip(zip, API_KEY, IMPERIAL_UNITS);
        call.enqueue(new Callback<GsonCurrentWeather>() {
            @Override
            public void onResponse(Call<GsonCurrentWeather> call, Response<GsonCurrentWeather> response) {
                if (response.isSuccessful()) {
                    //See helper method
                    handleResponse(response);
                    Log.d(TAG, "onResponse: ");
                    jobFinished(jobParameters, false);
                }
                Log.d(TAG, "onResponse: BAD RESPONSE");
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

    public void requestWithLongLat(String latLong, final JobParameters jobParameters) {
        Log.d(TAG, "requestWithLongLat: ");
        String latitude;
        String longitude;

        LocationManager manager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (latLong.equals("lat long")) {
            Log.d(TAG, "requestWithLongLat: in the IF");
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = Double.toString(location.getLatitude());
            longitude = Double.toString(location.getLongitude());
        } else 
            return;

        //Build retrofit request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d(TAG, "requestWithLongLat: BUILT REQUEST");
        OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);
        Call<GsonCurrentWeather> call = service.getWeatherByLongLat(latitude, longitude, API_KEY, IMPERIAL_UNITS);
        call.enqueue(new Callback<GsonCurrentWeather>() {
            @Override
            public void onResponse(Call<GsonCurrentWeather> call, Response<GsonCurrentWeather> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "onResponse: WEATHER SERVICE");
                    //see helper method
                    handleResponse(response);
                    jobFinished(jobParameters, false);
                }else {
                    Log.d(TAG, "onResponse: RESPONSE IS GARBAGE "+response.code());
                }
            }

            @Override
            public void onFailure(Call<GsonCurrentWeather> call, Throwable t) {
                t.printStackTrace();
                //Send a name of 'failure' that will trigger the default case in the BroadcastReceiver
                Intent intent = new Intent("service intent");
                intent.putExtra("service name", "failure");
                Log.d(TAG, "onFailure: ");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                jobFinished(jobParameters, false);
            }
        });
    }

    public void handleResponse(Response<GsonCurrentWeather> response){
        /*Regardless of the api call (zip or longitude/latitude), the pieces we need in the JSON respose
        are identical. Therefore we can use this block of code for both responses.
        */
        Log.d(TAG, "handleResponse: ");

        GsonCurrentWeather gsonWeather = response.body();
        String cityName = gsonWeather.getName();
        String description = gsonWeather.getWeather().get(0).getDescription();
        String temperature = gsonWeather.getMain().getTemp();

        showPersistentWeatherNotification(cityName, temperature);

        Intent intent = new Intent("weather service");
        intent.putExtra("city name", cityName);
        intent.putExtra("description", description);
        intent.putExtra("temperature", temperature);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    public void showPersistentWeatherNotification(String cityName, String temperature) {
        Log.d(TAG, "showPersistentWeatherNotification: ");
        String cityInNotification = "Weather for "+cityName+":";
        String currentTempNotification = "Current temperature: "+temperature;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setContentTitle(cityInNotification);
        builder.setContentInfo(currentTempNotification);
        builder.setSmallIcon(R.drawable.ic_wb_cloudy_black_24dp);
        builder.setOngoing(true);

        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(999, builder.build());
    }
}
