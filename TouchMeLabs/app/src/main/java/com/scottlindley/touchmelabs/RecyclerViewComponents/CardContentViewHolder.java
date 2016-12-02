package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * ViewHolder for parent {@link CardContent} class.
 * Extended by {@link CurrentWeatherViewHolder}, {@link NewsStoryViewHolder}, {@link TweetInfoViewHolder}
 */

public class CardContentViewHolder extends RecyclerView.ViewHolder {
    public CardContentViewHolder(View itemView) {
        super(itemView);
    }
}
