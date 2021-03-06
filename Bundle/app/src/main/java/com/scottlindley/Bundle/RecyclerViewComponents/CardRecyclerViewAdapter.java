package com.scottlindley.Bundle.RecyclerViewComponents;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scottlindley.Bundle.DetailView.DetailActivity;
import com.scottlindley.Bundle.MainActivity;
import com.scottlindley.Bundle.ModelObjects.CardContent;
import com.scottlindley.Bundle.ModelObjects.CurrentWeather;
import com.scottlindley.Bundle.ModelObjects.CurrentWeatherNoData;
import com.scottlindley.Bundle.ModelObjects.CurrentWeatherPermissionDenied;
import com.scottlindley.Bundle.ModelObjects.NewsStory;
import com.scottlindley.Bundle.ModelObjects.TweetInfo;
import com.scottlindley.Bundle.R;

import java.util.List;

import static com.scottlindley.Bundle.R.layout.news_card_light_layout;
import static com.scottlindley.Bundle.R.layout.twitter_card_light_layout;
import static com.scottlindley.Bundle.R.layout.weather_card_data_added;
import static com.scottlindley.Bundle.R.layout.weather_card_no_data;
import static com.scottlindley.Bundle.R.layout.weather_card_no_permission;
import static com.scottlindley.Bundle.RecyclerViewComponents.CurrentWeatherViewHolder.PERMISSION_LOCATION_REQUEST_CODE;

/**
 * Created by jonlieblich on 12/1/16.
 */

public class CardRecyclerViewAdapter extends RecyclerView.Adapter{
    private List<CardContent> mCardList;
    private CommunicateWithFragmentListener mListener;

    private static final int TWEET_VIEW_TYPE = twitter_card_light_layout;
    private static final int NEWS_VIEW_TYPE = news_card_light_layout;
    private static final int WEATHER_VIEW_TYPE = weather_card_data_added;
    private static final int WEATHER_VIEW_NO_DATA = weather_card_no_data;
    private static final int WEATHER_VIEW_PERMISSION_DENIED = weather_card_no_permission;

    public CardRecyclerViewAdapter(List<CardContent> list, CommunicateWithFragmentListener listener) {
        mCardList = list;
        mListener = listener;
    }

    /**
     * Creates the appropriate ViewHolders Given a list of CardContent objects
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case twitter_card_light_layout:
                return new TweetInfoViewHolder(inflater.inflate(twitter_card_light_layout, parent, false));
            case news_card_light_layout:
                return new NewsStoryViewHolder(inflater.inflate(news_card_light_layout, parent, false));
            case weather_card_no_data:
                return new CurrentWeatherNoDataViewHolder(inflater.inflate(weather_card_no_data, parent, false));
            case weather_card_no_permission:
                return new CurrentWeatherPermissionDeniedViewHolder(inflater.inflate(weather_card_no_permission, parent, false));
            case weather_card_data_added:
                return new CurrentWeatherViewHolder(inflater.inflate(weather_card_data_added, parent, false));
            default:
                return null;
        }
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        //Check wich viewholder is found at this position and then set up its views and click listeners
        switch(type) {
            case twitter_card_light_layout:
                ((TweetInfoViewHolder)holder).bindDataToView((TweetInfo) mCardList.get(position));
                //Card click listener to start new detail activity
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardClick(holder);
                    }
                });
                //Share button click listener to reply to a tweet
                ((TweetInfoViewHolder)holder).mReplyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.replyTweet(((TweetInfo) mCardList.get(position)).getUsername());
                    }
                });
                //Share button click listener to retweet a tweet
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
                ((NewsStoryViewHolder)holder).bindDataToViews((NewsStory) mCardList.get(position));
                //Card click listener to start new detail activity
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
                    ((CurrentWeatherViewHolder)holder).mTemperature.setText(temp+"\u2109");
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
                        if(zip.length()==5) {
                            mListener.requestUpdatedWeatherZip(zip);
                        }}
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

    /**
     * Starts a DetailActivity. What data is sent in the intend depends upon which card was clicked.
     * @param holder
     */
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

    /**
     * replaces all data in this adapter
     * @param newContent
     */
    public void replaceData(List<CardContent> newContent){
        mCardList.clear();
        mCardList.addAll(newContent);
        notifyDataSetChanged();
    }

    /**
     * Overrided method. Returns unique values determined by the type of object that lies in the
     * given position.
     * @param position
     * @return
     */
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

    /**
     * Interface that communicates info back to the CardListFragment
     */
    public interface CommunicateWithFragmentListener {
        void shareNews(String headline, String URL);
        void replyTweet(String handle);
        void retweet(long id, int position);
        void requestUpdatedWeatherZip(String zip);
    }
}
