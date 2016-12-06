package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
import com.scottlindley.touchmelabs.R;

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

        mCityName = (TextView)itemView.findViewById(R.id.city_name_light);
        mTemperature = (TextView)itemView.findViewById(R.id.temperature_light);
        mDescription = (TextView)itemView.findViewById(R.id.weather_conditions_light);
        mWeatherCard = (RelativeLayout)itemView.findViewById(R.id.weather_card_light);
    }
}
