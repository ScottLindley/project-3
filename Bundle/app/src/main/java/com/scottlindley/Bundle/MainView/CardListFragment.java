package com.scottlindley.Bundle.MainView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scottlindley.Bundle.ContentDBHelper;
import com.scottlindley.Bundle.ModelObjects.CardContent;
import com.scottlindley.Bundle.ModelObjects.CurrentWeather;
import com.scottlindley.Bundle.ModelObjects.CurrentWeatherNoData;
import com.scottlindley.Bundle.ModelObjects.CurrentWeatherPermissionDenied;
import com.scottlindley.Bundle.NetworkConnectionDetector;
import com.scottlindley.Bundle.R;
import com.scottlindley.Bundle.RecyclerViewComponents.CardRecyclerViewAdapter;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import java.util.List;

import retrofit2.Call;

/**
 * "Home screen" fragment. Main purpose is to display the RecyclerView of {@link CardContent} objects.
 */

public class CardListFragment extends Fragment implements CardRecyclerViewAdapter.CommunicateWithFragmentListener {

    private TwitterLoginButton mLoginButton;
    private SwipeRefreshLayout mRefreshLayout;
    private NetworkConnectionDetector mNetworkDetector;
    private CardContent mWeather;
    private CardRecyclerViewAdapter mAdapter;
    private List<CardContent> mCardList;
    private LoggedInListener mLogInListener;
    private WeatherUpdateListener mListener;

    //Required empty public constructor
    public CardListFragment() {}

    public static CardListFragment newInstance() {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Check for network connection
        mNetworkDetector = new NetworkConnectionDetector(getContext());
        if (!mNetworkDetector.isConnected()){
            Toast.makeText(getContext(), "No Network Detected", Toast.LENGTH_SHORT).show();
        }
        //set up swipe down to refresh
        mRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        setRefreshListener();

        //Check to see if the user is logged into twitter
        checkForTwitterLogin();

        //Grab weather data that may have been stored locally
        SharedPreferences preferences = getContext().getSharedPreferences("weather", Context.MODE_PRIVATE);
        String cityName = preferences.getString("city name", "error");
        String cityConditions = preferences.getString("city conditions", "error");
        String cityTemp = preferences.getString("city temp", "error");
        String denied = preferences.getString("isDenied", "error");

        //Create weather objects bases on what is or isn't stored in shared preferences
        if(cityName.equals("error") && denied.equals("error")){
            mWeather = new CurrentWeatherNoData();
        }
        else if(denied.equals("error")){
            mWeather = new CurrentWeather(cityName, cityConditions, cityTemp);
        } else {
            mWeather = new CurrentWeatherPermissionDenied();
        }

        //Pull the cards that are stored locally in the database
        mCardList = ContentDBHelper.getInstance(getContext()).getCardList(mWeather);

        //Set up the recycler view and its adapter
        RecyclerView cardRecycler = (RecyclerView)getView().findViewById(R.id.fragment_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cardRecycler.setLayoutManager(manager);
        mAdapter = new CardRecyclerViewAdapter(mCardList, this);
        cardRecycler.setAdapter(mAdapter);

        setUpBroadcastReceiverForRecyclerView();
        setUpBroadCastReceiverForShareButtons();
        setUpBroadcastReceiverForWeatherData();
    }

    /**
     * Makes a broadcast receiver that receives a broadcast once the WeatherService has
     * completed. Data is pulled from the received intent and a new CurrentWeather card takes
     * the place of the previous weather card on the list. The weather data is then added to
     * shared preferences. Finally, the fragment is recreated.
     */
    public void setUpBroadcastReceiverForWeatherData(){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String cityName = intent.getStringExtra("city name");
                String description = intent.getStringExtra("description");
                String temp = intent.getStringExtra("temperature");

                mCardList.set(0, new CurrentWeather(cityName, description, temp));

                SharedPreferences preferences = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("city name", cityName)
                        .putString("description", description)
                        .putString("temperature", temp)
                        .putString("isDenied", "error")
                        .apply();

//                mListener.redrawFragment();
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("weather service"));
    }

    /**
     * Sets up a broadcast receiver that is notified only once BOTH the NewsService and the
     * TwitterService are completed. The data in the adapter is replaced with the new data that
     * is found in the database.
     */
    public void setUpBroadcastReceiverForRecyclerView() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mAdapter.replaceData(ContentDBHelper.getInstance(getContext()).getCardList(mWeather));
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("card list"));
    }

    /**
     * Sets up a broadcast receiver that is notified when a user attempts to post a tweet.
     * The result (success or failure) is displayed to the user in a Toast.
     */
    public void setUpBroadCastReceiverForShareButtons() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
                    // success
                    Toast.makeText(context, "Tweet posted", Toast.LENGTH_SHORT).show();
                } else {
                    // failure
                    Toast.makeText(context, "Error posting tweet", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS");
        filter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }

    //Requests the database to refresh its data
    public void requestDataRefresh(Context context){
        ContentDBHelper.getInstance(context).refreshDB();
    }

    /**
     * First checks to see if the user is already logged into Twitter.
     * If not, a dialog is launched with a login button.
     * Once logged in, the ContentDBHelper is requested to refresh its content.
     */
    public void checkForTwitterLogin(){
        if (Twitter.getSessionManager().getActiveSession() == null) {
            LayoutInflater inflater = getLayoutInflater(null);
            View dialogView = inflater.inflate(R.layout.twitter_dialog, null);

            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .create();
            mLoginButton = (TwitterLoginButton)dialogView.findViewById(R.id.twitter_login_button);
            mLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    requestDataRefresh(getContext());
                    dialog.dismiss();
                    mLogInListener.assignNavBarValues();
                }

                @Override
                public void failure(TwitterException exception) {
                    exception.printStackTrace();
                    Toast.makeText(getContext(), "Error logging into Twitter", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * Handles what data needs to be refreshed and how. Conditional statments determine which version
     * of the WeatherService to call (Have location permissions or not). This list will not refresh if
     * either the user is not logged into Twitter, or if there is no network connection.
     */
    public void setRefreshListener(){
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mNetworkDetector.isConnected()) {
                    //This prevents a refresh request if the user is not logged into Twitter
                    if(Twitter.getSessionManager().getActiveSession() != null) {
                        //Get permission status
                        SharedPreferences preferences = getContext().getSharedPreferences("weather", Context.MODE_PRIVATE);
                        String permission = preferences.getString("permission", "error");
                        if(permission.equals("denied")){
                            //If we don't have location permissions, the user may have already entered
                            //in a zip code
                            String zip = preferences.getString("zipcode", "error");
                            if(zip!=null) {
                                mListener.getUpdatedWeatherZip(zip);
                            }
                        }else if(permission.equals("granted")){
                            //If we have location permissions, refresh the weather data with the users
                            //current location
                            mListener.getUpdatedWeatherLongLat();
                        }
                        //Request a refresh for the news and tweets
                        ContentDBHelper.getInstance(getContext()).refreshDB();
                    } else {
                        //If the user isn't logged into twitter launch the login dialog
                        checkForTwitterLogin();
                        mRefreshLayout.setRefreshing(false);
                    }
                } else {
                    //if no network is detected
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * Called if the user denies location permissions. This saves the permission status into shared
     * preferences and then recreates the fragment. Upon being recreated the fragment will create
     * a CurrentWeatherPermissionDenied object to prompt the user to enter his/her zip code instead.
     */
    public void handlePermissionDenied(){
        SharedPreferences preferences = getContext().getSharedPreferences("weather", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isDenied", "yes")
                .putString("permission", "denied");
        editor.commit();
        mListener.redrawFragment();
    }


    /**
     * Opens a tweet composition screen with the article's headline and URL already included in
     * the tweet. The user can edit the contents of the tweet and then attempt to post the tweet.
     * @param headline
     * @param URL
     */
    @Override
    public void shareNews(String headline, String URL) {
        TweetComposer.Builder tweetBuilder = new TweetComposer.Builder(getContext())
                .text(headline + "\n" + URL);
        tweetBuilder.show();
    }

    /**
     * Opens a tweet composition screen with the user's handle already included in the tweet.
     * The user can edit the contents of the tweet and then attempt to post the tweet.
     * @param handle
     */
    @Override
    public void replyTweet(String handle) {
        TweetComposer.Builder replyBuilder = new TweetComposer.Builder(getContext())
                .text("@"+handle);
        replyBuilder.show();
    }

    /**
     * Retweets the selected tweet. Because retweets are not displayed in a user's timeline, the
     * success or failure of the retweet attempt is displayed through a Toast to the user.
     * @param id
     * @param position
     */
    @Override
    public void retweet(long id, final int position) {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        Call<Tweet> retweetCall = Twitter.getApiClient(session).getStatusesService().retweet(id, false);
        retweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(getContext(), "Retweet Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
                Toast.makeText(getContext(), "Error, post may have already been retweeted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getContext() instanceof WeatherUpdateListener) {
            mListener = (WeatherUpdateListener) getContext();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WeatherUpdateListener methods");
        } if (getContext() instanceof CardListFragment.LoggedInListener) {
            mLogInListener = (CardListFragment.LoggedInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoggedInListener methods");
        }
    }


    /**
     * Necessary override to notify the login button a successful login occurred.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestUpdatedWeatherZip(String zip) {
        mListener.getUpdatedWeatherZip(zip);
    }

    public interface WeatherUpdateListener{
        void redrawFragment();
        void getUpdatedWeatherLongLat();
        void getUpdatedWeatherZip(String zip);
    }

    /**
     * Notifies the MainActivity when the user has successfully logged into Twitter.
     * The MainActivity then uses the user's info to populate the navbar's header views.
     */
    public interface LoggedInListener{
        void assignNavBarValues();
    }
}
