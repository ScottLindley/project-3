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

/**
 * ViewHolder for {@link CurrentWeather} card.
 */

public class CurrentWeatherViewHolder extends RecyclerView.ViewHolder{
    public TextView mCityName, mTemperature, mDescription;
    public RelativeLayout mWeatherCard;
    public Button mSetLocation;
    public EditText mZipCode;

    private OnLocationPermissionResponseListener mListener;

    private static final int WEATHER_JOB_SERVICE_ID = 49;

    public CurrentWeatherViewHolder(View itemView) {
        super(itemView);

        mCityName = (TextView)itemView.findViewById(R.id.city_name_light);
        mTemperature = (TextView)itemView.findViewById(R.id.temperature_light);
        mDescription = (TextView)itemView.findViewById(R.id.weather_conditions_light);
        mWeatherCard = (RelativeLayout)itemView.findViewById(R.id.weather_card_light);
        //TODO: make weather card button (A.K.A. THE BUTTON)
        //TODO: make weather card editText
//        mSetLocation = (Button)itemView.findViewById(R.id.find_location_btn);
//        mZipCode = (EditText)itemView.findViewById(R.id.zip_code_edit);
    }

    /**
     * Helper method to assign a given {@link CurrentWeather} object's contents to this view
     */
    public void bindDataToViews(CurrentWeather weather, final Context context) {
        SharedPreferences currentData = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
        if((!currentData.contains("city name"))||weather != null) {
            int cLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int fLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            JobScheduler weatherScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(cLocation == PackageManager.PERMISSION_GRANTED||fLocation == PackageManager.PERMISSION_GRANTED) {
                JobInfo info = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID,
                        new ComponentName(context, WeatherService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();
                weatherScheduler.schedule(info);
                updateWeatherCard(context);
            } else {

            }
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

                ((MainActivity) mCityName.getContext()).getParent()
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("weather service"));
    }
}
