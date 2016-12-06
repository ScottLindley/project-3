package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scottlindley.touchmelabs.DetailView.DetailActivity;
import com.scottlindley.touchmelabs.MainActivity;
import com.scottlindley.touchmelabs.ModelObjects.CardContent;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeatherNoData;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeatherPermissionDenied;
import com.scottlindley.touchmelabs.ModelObjects.NewsStory;
import com.scottlindley.touchmelabs.ModelObjects.TweetInfo;
import com.scottlindley.touchmelabs.R;
import com.scottlindley.touchmelabs.Services.WeatherService;

import java.util.List;
import java.util.regex.Pattern;

import static com.scottlindley.touchmelabs.R.layout.news_card_light_layout;
import static com.scottlindley.touchmelabs.R.layout.twitter_card_light_layout;
import static com.scottlindley.touchmelabs.R.layout.weather_card_data_added;
import static com.scottlindley.touchmelabs.R.layout.weather_card_light_layout;
import static com.scottlindley.touchmelabs.R.layout.weather_card_no_data;
import static com.scottlindley.touchmelabs.R.layout.weather_card_no_permission;
import static com.scottlindley.touchmelabs.RecyclerViewComponents.CurrentWeatherViewHolder.PERMISSION_LOCATION_REQUEST_CODE;
import static com.scottlindley.touchmelabs.RecyclerViewComponents.CurrentWeatherViewHolder.WEATHER_JOB_SERVICE_ID;

/**
 * Created by jonlieblich on 12/1/16.
 */

public class CardRecyclerViewAdapter extends RecyclerView.Adapter{
    private List<CardContent> mCardList;
    private OnShareContentListener mListener;

    private static final int TWEET_VIEW_TYPE = twitter_card_light_layout;
    private static final int NEWS_VIEW_TYPE = news_card_light_layout;
    private static final int WEATHER_VIEW_TYPE = weather_card_data_added;
    private static final int WEATHER_VIEW_NO_DATA = weather_card_no_data;
    private static final int WEATHER_VIEW_PERMISSION_DENIED = weather_card_no_permission;

    public CardRecyclerViewAdapter(List<CardContent> list, OnShareContentListener listener) {
        mCardList = list;
        mListener = listener;
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
            case weather_card_no_data:
                return new CurrentWeatherViewHolder(inflater.inflate(weather_card_no_data, parent, false));
            case weather_card_no_permission:
                return new CurrentWeatherViewHolder(inflater.inflate(weather_card_no_permission, parent, false));
            case weather_card_data_added:
                return new CurrentWeatherViewHolder(inflater.inflate(weather_card_data_added, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        switch(type) {
            case twitter_card_light_layout:
                ((TweetInfoViewHolder)holder).bindDataToView((TweetInfo) mCardList.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardClick(holder);
                    }
                });
                ((TweetInfoViewHolder)holder).mReplyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.replyTweet(((TweetInfo) mCardList.get(position)).getUsername());
                    }
                });
                ((TweetInfoViewHolder)holder).mRetweetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.retweet(
                                Long.parseLong(((TweetInfo) mCardList.get(position)).getId()),
                                position);
                    }
                });
                break;
            case news_card_light_layout:
                //Card click listener to start new detail activity
                ((NewsStoryViewHolder)holder).bindDataToViews((NewsStory) mCardList.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardClick(holder);
                    }
                });
                //Share button click listener to share a news story
                ((NewsStoryViewHolder) holder).mShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.shareNews(mCardList.get(position).getTitle(),
                                ((NewsStory) mCardList.get(position)).getURL());
                    }
                });
                break;

            //Job service will update with given location info every 10 min
            case weather_card_data_added:
                SharedPreferences sp = holder.itemView.getContext()
                        .getSharedPreferences("weather", Context.MODE_PRIVATE);
                if(sp.contains("city name")) {
                    String name = sp.getString("city name", null);
                    String temp = sp.getString("temperature", null);
                    String desc = sp.getString("description", null);

                    ((CurrentWeatherViewHolder)holder).mCityName.setText(name);
                    ((CurrentWeatherViewHolder)holder).mTemperature.setText(temp);
                    ((CurrentWeatherViewHolder)holder).mDescription.setText(desc);
                }
                break;

            //Request location permission and launch weather service with lat/long
            case weather_card_no_data:
                ((CurrentWeatherNoDataViewHolder)holder).mSetLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions((MainActivity)((CurrentWeatherNoDataViewHolder)holder).mSetLocation
                        .getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_LOCATION_REQUEST_CODE);
                    }
                });
                break;

            //After user denies location permission, show this, then launch weather service with zip code
            case weather_card_no_permission:
                ((CurrentWeatherPermissionDeniedViewHolder)holder).mSetZipCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String zip = ((CurrentWeatherPermissionDeniedViewHolder)holder).mZipCode.getText().toString();
                        if(Pattern.matches("\\d", zip)&&zip.length()==5) {
                            JobScheduler scheduler = (JobScheduler)holder.itemView.getContext()
                                    .getSystemService(Context.JOB_SCHEDULER_SERVICE);
                            JobInfo weatherZip = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID,
                                    new ComponentName(holder.itemView.getContext(), WeatherService.class))
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                    .setPeriodic(600000)
                                    .build();
                            scheduler.schedule(weatherZip);
                        }
                    }
                });
                break;
            default:
                Toast.makeText(holder.itemView.getContext(), "Whoops!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    private void cardClick(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        switch(view.getId()) {
            case R.id.twitter_card_light_bg:
                Intent twitterIntent = new Intent(view.getContext(), DetailActivity.class);
                String id = ((TweetInfo)mCardList.get(holder.getAdapterPosition())).getId();
                twitterIntent.putExtra("card identifier", "tweet");
                twitterIntent.putExtra("id", id);
                view.getContext().startActivity(twitterIntent);
                break;
            case R.id.news_card_bg:
                Intent newsIntent = new Intent(view.getContext(), DetailActivity.class);
                String link = ((NewsStory)mCardList.get(holder.getAdapterPosition())).getURL();

                newsIntent.putExtra("card identifier", "story");
                newsIntent.putExtra("link", link);
                view.getContext().startActivity(newsIntent);
                break;
            default:
        }
    }

    public void replaceData(List<CardContent> newContent){
        mCardList.clear();
        mCardList.addAll(newContent);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mCardList.get(position) instanceof CurrentWeather){
            return WEATHER_VIEW_TYPE;
        } else if (mCardList.get(position) instanceof NewsStory){
            return NEWS_VIEW_TYPE;
        } else if (mCardList.get(position) instanceof TweetInfo){
            return TWEET_VIEW_TYPE;
        } else if (mCardList.get(position) instanceof CurrentWeatherNoData){
            return WEATHER_VIEW_NO_DATA;
        } else if (mCardList.get(position) instanceof CurrentWeatherPermissionDenied) {
            return WEATHER_VIEW_PERMISSION_DENIED;
        } else {
            return -1;
        }
    }

    public interface OnShareContentListener{
        void shareNews(String headline, String URL);
        void replyTweet(String handle);
        void retweet(long id, int position);
    }
}
