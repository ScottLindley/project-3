package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.scottlindley.touchmelabs.R;

/**
 * ViewHolder for {@link CurrentWeather} card.
 */

public class CurrentWeatherViewHolder extends RecyclerView.ViewHolder{
    private TextView mCityName, mTemperature, mDescription;
    private RelativeLayout mWeatherCard;
    private Button mSetLocation;
    private EditText mZipCode;

    private static final int WEATHER_JOB_SERVICE_ID = 49;

    public CurrentWeatherViewHolder(View itemView) {
        super(itemView);

        mCityName = (TextView)itemView.findViewById(R.id.city_name);
        mTemperature = (TextView)itemView.findViewById(R.id.tempertature);
        mDescription = (TextView)itemView.findViewById(R.id.weather_conditions);
        mWeatherCard = (RelativeLayout)itemView.findViewById(R.id.weather_card);
        mSetLocation = (Button)itemView.findViewById(R.id.find_location_btn);
        mZipCode = (EditText)itemView.findViewById(R.id.zip_code_edit);
    }

    /**
     * Helper method to assign a given {@link CurrentWeather} object's contents to this view
     */
    public void bindDataToViews(CurrentWeather weather, final Context context) {
        SharedPreferences currentData = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
        if(!currentData.contains("city name")) {
            int cLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int fLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            JobScheduler weatherScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(cLocation == PackageManager.PERMISSION_GRANTED||fLocation == PackageManager.PERMISSION_GRANTED) {
                JobInfo info = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID, new ComponentName(context, WeatherService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();
                weatherScheduler.schedule(info);
                updateWeatherCard(context);
            } else {

            }
        } else {
            SharedPreferences weatherContext = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

        }
    }

    private void updateWeatherCard(Context context) {
        SharedPreferences weather = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

        weather.edit().putString("city name", "retrieve from api call")
                    .putString("city temp", "retrieve temp from api")
                    .putString("city conditions", "retrieve from api")
                    .commit();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
}
