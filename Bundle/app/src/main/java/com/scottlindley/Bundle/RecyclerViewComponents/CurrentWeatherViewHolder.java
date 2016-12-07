package com.scottlindley.Bundle.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlindley.Bundle.ModelObjects.CurrentWeather;
import com.scottlindley.Bundle.R;

/**
 * ViewHolder for {@link CurrentWeather} card.
 */

public class CurrentWeatherViewHolder extends RecyclerView.ViewHolder{
    public TextView mCityName, mTemperature, mDescription;
    public RelativeLayout mWeatherCard;

    public static final int WEATHER_JOB_SERVICE_ID = 49;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 235;

    public CurrentWeatherViewHolder(View itemView) {
        super(itemView);

        mCityName = (TextView)itemView.findViewById(R.id.weather_city);
        mTemperature = (TextView)itemView.findViewById(R.id.weather_temp);
        mDescription = (TextView)itemView.findViewById(R.id.weather_desc);
        mWeatherCard = (RelativeLayout)itemView.findViewById(R.id.weather_data_layout);
    }
}
