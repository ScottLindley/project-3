package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scottlindley.touchmelabs.ModelObjects.NewsStory;
import com.scottlindley.touchmelabs.R;

/**
 * ViewHolder for {@link NewsStory} card.
 */

public class NewsStoryViewHolder extends RecyclerView.ViewHolder{
    private TextView mTitle, mSummary, mUrl;
    private RelativeLayout mNewsCard;

    public NewsStoryViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView)itemView.findViewById(R.id.news_article_headline);
        mSummary = (TextView)itemView.findViewById(R.id.news_article_summary);
        mUrl = (TextView)itemView.findViewById(R.id.news_article_source);
        mNewsCard = (RelativeLayout)itemView.findViewById(R.id.news_card_bg);
    }

    /**
     * Helper method to assign a given {@link NewsStory} object's contents to this view
     */
    public void bindDataToViews(NewsStory news) {
        mTitle.setText(news.getTitle());
        mSummary.setText(news.getContent());
        mUrl.setText(news.getURL());
    }

    /**
     * Views affected by this method are {@link #mTitle}, {@link #mSummary}, {@link #mUrl},
     * and {@link #mNewsCard}.
     * @param listener should be {@link this} (not make for use outside of {@link CardRecyclerViewAdapter}).
     */
    public void setClickListenerForAllViews(View.OnClickListener listener) {
        mTitle.setOnClickListener(listener);
        mSummary.setOnClickListener(listener);
        mUrl.setOnClickListener(listener);
        mNewsCard.setOnClickListener(listener);
    }
}
