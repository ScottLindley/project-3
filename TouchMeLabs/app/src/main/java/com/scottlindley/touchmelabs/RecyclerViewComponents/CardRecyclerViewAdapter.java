package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scottlindley.touchmelabs.R;

/**
 * Created by jonlieblich on 12/1/16.
 */

public class CardRecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<CardContent> mCardList;
    private int positionForWeather;

    public CardRecyclerViewAdapter(List<CardContent> list) {
        mCardList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case R.layout.twitter_card:
                return inflater.inflate(R.layout.twitter_card, parent, false);
            case R.layout.news_card:
                return inflater.inflate(R.layout.news_card, parent, false);
            case R.layout.weather_card:
                return inflater.inflate(R.layout.weather_card, parent, false);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        positionForWeather = position;
        switch(type) {
            case R.layout.twitter_card:
                ((TweetInfoViewHolder)holder).bindDataToView(mCardList.get(position));
                ((TweetInfoViewHolder) holder).setClickListenerForAllViews(this);
                break;
            case R.layout.news_card:
                ((NewsStoryViewHolder)holder).bindDataToViews(mCardList.get(position));
                ((NewsStoryViewHolder)holder).setClickListenerForAllViews(this);
                break;
            case R.layout.weather_card:
                ((CurrentWeatherViewHolder)holder).bindDataToViews(mCardList.get(position),
                        holder.itemView.getContext());
                break;
            default:
                Toast.makeText(holder.itemView.getContext(), "Whoops!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    @Override
    public void onClick(View view) {
        //TODO: Set to open new detail activity and/or fragment
        switch(view.getId()) {
            case R.id.twitter_card:
                Intent twitterIntent = new Intent(view.getContext(), DetailActivity.class);
                String id = mCardList.get(positionForWeather).getId();

                twitterIntent.putExtra("id", id);
                view.getContext().startActivity(twitterIntent);
                break;
            case R.id.news_card:
                Intent newsIntent = new Intent(view.getContext(), DetailActivity.class);
                String title = mCardList.get(positionForWeather).getTitle();
                String content = mCardList.get(positionForWeather).getContent();
                String link = mCardList.get(positionForWeather).getUrl();

                newsIntent.putExtra("name", title);
                newsIntent.putExtra("content", content);
                newsIntent.putExtra("linke", link);
                view.getContext().startActivity(newsIntent);
                break;
        }
    }
}
