package com.scottlindley.Bundle.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlindley.Bundle.ModelObjects.NewsStory;
import com.scottlindley.Bundle.R;

/**
 * ViewHolder for {@link NewsStory} card.
 */

public class NewsStoryViewHolder extends RecyclerView.ViewHolder{
    private TextView mTitle, mSummary;
    private RelativeLayout mNewsCard;
    public ImageView mShareButton;

    public NewsStoryViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView)itemView.findViewById(R.id.news_article_headline);
        mSummary = (TextView)itemView.findViewById(R.id.news_article_summary);
        mNewsCard = (RelativeLayout)itemView.findViewById(R.id.news_card_relative_layout);
        mShareButton = (ImageView)itemView.findViewById(R.id.news_share_button);
    }

    /**
     * Helper method to assign a given {@link NewsStory} object's contents to this view
     */
    public void bindDataToViews(NewsStory news) {
        mTitle.setText(news.getTitle());
        mSummary.setText(news.getContent());
    }

    /**
     * Views affected by this method are {@link #mTitle}, {@link #mSummary},
     * and {@link #mNewsCard}.
     * @param listener should be {@link this} (not make for use outside of {@link CardRecyclerViewAdapter}).
     */
    public void setClickListenerForAllViews(View.OnClickListener listener) {
        mTitle.setOnClickListener(listener);
        mSummary.setOnClickListener(listener);
        mNewsCard.setOnClickListener(listener);
    }
}
