package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * ViewHolder for parent {@link CardContent} class.
 * Extended by {@link CurrentWeatherViewHolder}, {@link NewsStoryViewHolder}, {@link TweetInfoViewHolder}
 */

public class CardContentViewHolder extends RecyclerView.ViewHolder {
    //Twitter info
    public TextView mHandle, mTweetContent, mUsername, mTime;
    //News info
    public TextView mTitle, mSummary, mUrl;
    //Weather info
    public TextView mCityNmae, mTemperature, mDescription;

    public CardContentViewHolder(View itemView) {
        super(itemView);

    }
}
