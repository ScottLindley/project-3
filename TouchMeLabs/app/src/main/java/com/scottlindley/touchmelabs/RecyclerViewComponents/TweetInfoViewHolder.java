package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlindley.touchmelabs.ModelObjects.TweetInfo;
import com.scottlindley.touchmelabs.R;

/**
 * ViewHolder for {@link TweetInfo} card.
 */

public class TweetInfoViewHolder extends RecyclerView.ViewHolder{
    private TextView mHandle, mTweetContent, mUsername, mTime;
    private RelativeLayout mTwitterCard;
    public ImageView mReplyButton, mRetweetButton;

    public TweetInfoViewHolder(View itemView) {
        super(itemView);
        mHandle = (TextView)itemView.findViewById(R.id.twitter_handle);
        mTweetContent = (TextView)itemView.findViewById(R.id.twitter_tweet_content);
        mUsername = (TextView)itemView.findViewById(R.id.twitter_username);
        mTime = (TextView)itemView.findViewById(R.id.twitter_time_stamp);
        mTwitterCard = (RelativeLayout)itemView.findViewById(R.id.twitter_card_relative_layout);
        mReplyButton = (ImageView)itemView.findViewById(R.id.tweet_reply_button);
        mRetweetButton = (ImageView)itemView.findViewById(R.id.tweet_retweet_button);
    }

    /**
     * Helper method to assign a given {@link TweetInfo} object's content.db to this view
     */
    public void bindDataToView(TweetInfo tweet) {
        //Adds a "@" before each handle name
        mHandle.setText( "@" + tweet.getTitle());
        mUsername.setText(tweet.getUsername());
        mTime.setText(tweet.getTime());
        //Trims any tweets that are longer than 140 characters
        if(tweet.getContent().length()>140){
            String trimmedTweet = tweet.getContent().substring(0, 140);
            mTweetContent.setText(trimmedTweet+"...");
        } else {
            mTweetContent.setText(tweet.getContent());
        }
    }

    /**
     * Views afftected by this method are {@link #mHandle}, {@link #mTweetContent}, {@link #mUsername},
     * {@link #mTime}, and {@link #mTwitterCard}.
     *
     * @param listener Should be {@link this} (not made for use outside of {@link CardRecyclerViewAdapter}).
     */
    public void setClickListenerForAllViews(View.OnClickListener listener) {
        mHandle.setOnClickListener(listener);
        mTweetContent.setOnClickListener(listener);
        mUsername.setOnClickListener(listener);
        mTime.setOnClickListener(listener);
        mTwitterCard.setOnClickListener(listener);
    }
}
