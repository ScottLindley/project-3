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
import com.scottlindley.touchmelabs.MainView.CardListFragment;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
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

    private static final int WEATHER_JOB_SERVICE_ID = 49;
    private static final int WEATHER_SERVICE_PERIOD_INTERVAL = 600000;

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
     *
     * Method is used to determine when to call {@link #updateWeatherCard(Context)} in
     * order to display new weather info and store it in {@link SharedPreferences}
     *
     * <li> {@link SharedPreferences} is checked for presence of most recent weather data
     * <li> If no data is found, the application checks for location permission access once the
     * {@link #mSetLocation} button appears and has been pressed
     * <li> If location permission is found, the user's current location will be used for access,
     * otherwise the user will be prompted to grant access to location
     * <li> If permission is denied, the user must enter current location info in
     * an {@link EditText} in order to proceed
     */
    public void bindDataToViews(CurrentWeather weather, final Context context) {
        SharedPreferences currentData = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

        //Check if most recent weather data exists
        if((!currentData.contains("city name"))||weather != null) {
            int cLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int fLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            JobScheduler weatherScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            //Check if app has location permissions available before scheduling new service
            if(cLocation == PackageManager.PERMISSION_GRANTED||fLocation == PackageManager.PERMISSION_GRANTED) {
                JobInfo info = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID,
                        new ComponentName(context, WeatherService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPeriodic(WEATHER_SERVICE_PERIOD_INTERVAL)
                        .build();
                weatherScheduler.schedule(info);
                updateWeatherCard(context);
             //No permission has been granted yet, so the app will prompt the user for it
            } else {
                mSetLocation = (Button) itemView.findViewById(R.id.find_location_btn);
                mSetLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        promptUserForLocationPermissionOrZip();
                    }
                });
            }

        //If data exists, retrieve it and display it. Service is periodic and does not need to be scheduled here
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

    /**
     * Called when {@link #bindDataToViews(CurrentWeather, Context)} has appropriate data
     * needed to obtain current weather info.
     */
    private void updateWeatherCard(Context context) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences weather = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

                String name = intent.getStringExtra("city name");
                String description = intent.getStringExtra("description");
                String temperature = intent.getStringExtra("temperature");

                mCityName.setText(name);
                mTemperature.setText(temperature);
                mDescription.setText(description);

                weather.edit().putString("city name", name)
                        .putString("city temp", temperature)
                        .putString("city conditions", description)
                        .apply();
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("weather service"));
    }

    //Requests permission
    private void promptUserForLocationPermissionOrZip() {
        ActivityCompat.requestPermissions(CardListFragment.newInstance().getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 512);
    }
}
