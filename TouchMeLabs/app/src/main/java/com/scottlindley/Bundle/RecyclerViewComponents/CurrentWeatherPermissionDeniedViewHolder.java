package com.scottlindley.Bundle.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scottlindley.Bundle.R;

/**
 * Created by jonlieblich on 12/6/16.
 */

public class CurrentWeatherPermissionDeniedViewHolder extends RecyclerView.ViewHolder {
    public EditText mZipCode;
    public Button mSetZipCode;

    public CurrentWeatherPermissionDeniedViewHolder(View itemView) {
        super(itemView);

        mZipCode = (EditText)itemView.findViewById(R.id.zip_code_entry);
        mSetZipCode = (Button)itemView.findViewById(R.id.find_zip_code_btn);
    }
}
