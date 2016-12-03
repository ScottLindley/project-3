package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scottlindley.touchmelabs.DetailView.DetailActivity;
import com.scottlindley.touchmelabs.ModelObjects.CardContent;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
import com.scottlindley.touchmelabs.ModelObjects.NewsStory;
import com.scottlindley.touchmelabs.ModelObjects.TweetInfo;
import com.scottlindley.touchmelabs.R;

import java.util.List;

import static com.scottlindley.touchmelabs.R.layout.news_card_light_layout;
import static com.scottlindley.touchmelabs.R.layout.twitter_card_light_layout;
import static com.scottlindley.touchmelabs.R.layout.weather_card_light_layout;

/**
 * Created by jonlieblich on 12/1/16.
 */

public class CardRecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<CardContent> mCardList;
    private int positionForWeather;

    private static final int TWEET_VIEW_TYPE = twitter_card_light_layout;
    private static final int NEWS_VIEW_TYPE = news_card_light_layout;
    private static final int WEATHER_VIEW_TYPE = weather_card_light_layout;

    public CardRecyclerViewAdapter(List<CardContent> list) {
        mCardList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case twitter_card_light_layout:
                return new TweetInfoViewHolder(inflater.inflate(twitter_card_light_layout, parent, false));
            case news_card_light_layout:
                return new NewsStoryViewHolder(inflater.inflate(news_card_light_layout, parent, false));
            case weather_card_light_layout:
                return new CurrentWeatherViewHolder(inflater.inflate(weather_card_light_layout, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        positionForWeather = position;
        switch(type) {
            case twitter_card_light_layout:
                ((TweetInfoViewHolder)holder).bindDataToView((TweetInfo) mCardList.get(position));
                ((TweetInfoViewHolder) holder).setClickListenerForAllViews(this);
                break;
            case news_card_light_layout:
                ((NewsStoryViewHolder)holder).bindDataToViews((NewsStory) mCardList.get(position));
                ((NewsStoryViewHolder)holder).setClickListenerForAllViews(this);
                break;
            case weather_card_light_layout:
                SharedPreferences sp = holder.itemView.getContext()
                        .getSharedPreferences("weather", Context.MODE_PRIVATE);
                if(sp.contains("city name")) {
                    String name = sp.getString("city name", null);
                    String temp = sp.getString("temperature", null);
                    String desc = sp.getString("description", null);

                    ((CurrentWeatherViewHolder)holder).mCityName.setText(name);
                    ((CurrentWeatherViewHolder)holder).mTemperature.setText(temp);
                    ((CurrentWeatherViewHolder)holder).mDescription.setText(desc);
                } else {
                    ((CurrentWeatherViewHolder) holder).bindDataToViews((CurrentWeather) mCardList.get(position),
                            holder.itemView.getContext());
                }
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
            case R.id.twitter_card_light_bg:
                Intent twitterIntent = new Intent(view.getContext(), DetailActivity.class);
                String id = ((TweetInfo)mCardList.get(positionForWeather)).getId();

                twitterIntent.putExtra("id", id);
                view.getContext().startActivity(twitterIntent);
                break;
            case R.id.news_card_bg:
                Intent newsIntent = new Intent(view.getContext(), DetailActivity.class);
                String title = mCardList.get(positionForWeather).getTitle();
                String content = mCardList.get(positionForWeather).getContent();
                String link = ((NewsStory)mCardList.get(positionForWeather)).getURL();

                newsIntent.putExtra("name", title);
                newsIntent.putExtra("content.db", content);
                newsIntent.putExtra("link", link);
                view.getContext().startActivity(newsIntent);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mCardList.get(position) instanceof CurrentWeather){
            return WEATHER_VIEW_TYPE;
        } else if (mCardList.get(position) instanceof NewsStory){
            return NEWS_VIEW_TYPE;
        } else if (mCardList.get(position) instanceof TweetInfo){
            return TWEET_VIEW_TYPE;
        } else {
            return -1;
        }
    }
}
