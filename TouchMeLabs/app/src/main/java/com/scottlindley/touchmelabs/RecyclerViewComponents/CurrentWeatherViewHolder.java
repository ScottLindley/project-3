package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlindley.touchmelabs.MainActivity;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
import com.scottlindley.touchmelabs.OnLocationPermissionResponseListener;
import com.scottlindley.touchmelabs.R;
import com.scottlindley.touchmelabs.Services.WeatherService;

import java.util.regex.Pattern;

/**
 * ViewHolder for {@link CurrentWeather} card.
 */

public class CurrentWeatherViewHolder extends RecyclerView.ViewHolder implements OnLocationPermissionResponseListener{
    public TextView mCityName, mTemperature, mDescription;
    public RelativeLayout mWeatherCard;
    public Button mSetLocation, mSetZipCode;
    public EditText mZipCode;

    private static final int WEATHER_JOB_SERVICE_ID = 49;
    private static final int TEN_MINUTE_REFRESH = 600000;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 235;

    public CurrentWeatherViewHolder(View itemView) {
        super(itemView);

        mCityName = (TextView)itemView.findViewById(R.id.city_name_light);
        mTemperature = (TextView)itemView.findViewById(R.id.temperature_light);
        mDescription = (TextView)itemView.findViewById(R.id.weather_conditions_light);
        mWeatherCard = (RelativeLayout)itemView.findViewById(R.id.weather_card_light);
        //TODO: make weather card button (A.K.A. THE BUTTON)
        //TODO: make weather card editText
        mSetLocation = (Button)itemView.findViewById(R.id.find_location_btn);
        mZipCode = (EditText)itemView.findViewById(R.id.zip_code_entry);
        mSetZipCode = (Button)itemView.findViewById(R.id.find_zip_code_btn);
    }

    /**
     * Helper method to assign a given {@link CurrentWeather} object's contents to this view
     */
    public void bindDataToViews(CurrentWeather weather, final Context context) {
        SharedPreferences currentData = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
        //Check if weather data is empty
        if(!currentData.contains("city name")) {

            //Get permission info
            int cLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int fLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

            JobScheduler weatherScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            //Check for either location permission
            if(cLocation == PackageManager.PERMISSION_GRANTED||fLocation == PackageManager.PERMISSION_GRANTED) {
                scheduleWeather(weatherScheduler, context);
                updateWeatherCard(context, weather);

            //No location permission, request it
            } else {
                showLocationButton();
                mSetLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions((MainActivity)mCityName.getContext(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_LOCATION_REQUEST_CODE);
                    }
                });
            }

        //If data exists, populate the card with most recent weather data
        } else {
            SharedPreferences weatherContext = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

            String city = weatherContext.getString("city name", null);
            String temperature = weatherContext.getString("temperature", null);
            String description = weatherContext.getString("description", null);

            mCityName.setText(city);
            mDescription.setText(description);
            mTemperature.setText(temperature);
        }
    }

    private void updateWeatherCard(Context context, final CurrentWeather info) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences weather = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

                weather.edit().putString("city name", info.getTitle())
                        .putString("city temp", info.getTemperature())
                        .putString("city conditions", info.getContent())
                        .commit();

                String name = intent.getStringExtra("city name");
                String description = intent.getStringExtra("description");
                String temperature = intent.getStringExtra("temperature");

                mCityName.setText(name);
                mTemperature.setText(temperature);
                mDescription.setText(description);
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("weather service"));
    }


    @Override
    public void setPermissionResponseListener(final int response) {
        BroadcastReceiver afterPermission = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                String city = intent.getStringExtra("city name");
                String desc = intent.getStringExtra("description");
                String temp = intent.getStringExtra("temperature");

                if(response == PackageManager.PERMISSION_GRANTED) {
                    CurrentWeather weather = new CurrentWeather(city, desc, temp);
                    updateWeatherCard(mCityName.getContext(), weather);
                } else {
                    showZipCodeEntry();
                    mSetZipCode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mZipCode.getText().length() != 5) {
                                mZipCode.setError("Enter Zip Code");
                            } else {
                                if(Pattern.matches("\\d", mZipCode.getText())) {
                                    SharedPreferences pref = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
                                    pref.edit().putString("zip", mZipCode.getText().toString()).apply();

                                    JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                    scheduleWeather(scheduler, context);
                                } else {
                                    mZipCode.setError("Invalid format");
                                }
                            }
                        }
                    });
                }
            }
        };
        LocalBroadcastManager.getInstance(mCityName.getContext()).registerReceiver(afterPermission,
                new IntentFilter("weather service"));
    }

    private void scheduleWeather(JobScheduler jobs, Context context) {
        JobInfo info = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID,
                new ComponentName(context, WeatherService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(TEN_MINUTE_REFRESH)
                .build();
        jobs.schedule(info);
    }

    private void showLocationButton() {
        mSetLocation.setVisibility(View.VISIBLE);
    }

    private void showZipCodeEntry() {
        mZipCode.setVisibility(View.VISIBLE);
        mSetZipCode.setVisibility(View.VISIBLE);
    }
}
