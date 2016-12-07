package com.scottlindley.Bundle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class NetworkConnectionDetector {
    private boolean mIsConnected;

    public NetworkConnectionDetector(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.getState() == NetworkInfo.State.CONNECTED){
            mIsConnected = true;
        } else {
            mIsConnected = false;
        }
    }

    public boolean isConnected(){
        return mIsConnected;
    }
}
