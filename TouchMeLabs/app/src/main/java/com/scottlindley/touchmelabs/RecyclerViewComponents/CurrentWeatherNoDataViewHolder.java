package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.scottlindley.touchmelabs.R;

/**
 * Created by jonlieblich on 12/6/16.
 */

public class CurrentWeatherNoDataViewHolder extends RecyclerView.ViewHolder {
    public Button mSetLocation;

    public CurrentWeatherNoDataViewHolder(View itemView) {
        super(itemView);

        mSetLocation = (Button)itemView.findViewById(R.id.set_location_btn);
    }
}
