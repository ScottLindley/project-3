package com.scottlindley.touchmelabs.MainView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.scottlindley.touchmelabs.ContentDBHelper;
import com.scottlindley.touchmelabs.ModelObjects.CardContent;
import com.scottlindley.touchmelabs.ModelObjects.CurrentWeather;
import com.scottlindley.touchmelabs.NetworkConnectionDetector;
import com.scottlindley.touchmelabs.R;
import com.scottlindley.touchmelabs.RecyclerViewComponents.CardRecyclerViewAdapter;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;

/**
 * "Home screen" fragment. Main purpose is to display the RecyclerView of {@link CardContent} objects.
 */

public class CardListFragment extends Fragment {

    private TwitterLoginButton mLoginButton;
    private SwipeRefreshLayout mRefreshLayout;
    private NetworkConnectionDetector mNetworkDetector;
    private CurrentWeather mWeather;
    private CardRecyclerViewAdapter mAdapter;
    private List<CardContent> mCardList;

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
        mNetworkDetector = new NetworkConnectionDetector(getContext());
        if (!mNetworkDetector.isConnected()){
            Toast.makeText(getContext(), "No Network Detected", Toast.LENGTH_SHORT).show();
        }
        mRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        setRefreshListener();

        SharedPreferences preferences = getContext().getSharedPreferences("mWeather", Context.MODE_PRIVATE);
        String cityName = preferences.getString("city name", "error");
        String cityConditions = preferences.getString("city conditions", "error");
        String cityTemp = preferences.getString("city temp", "error");

        mWeather = new CurrentWeather(cityName, cityConditions, cityTemp);

        mCardList = ContentDBHelper.getInstance(getContext()).getCardList(mWeather);

        RecyclerView cardRecycler = (RecyclerView)getView().findViewById(R.id.fragment_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cardRecycler.setLayoutManager(manager);
        mAdapter = new CardRecyclerViewAdapter(mCardList);
        cardRecycler.setAdapter(mAdapter);

        setUpBroadcastReceiverForRecyclerView();
    }


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

    public void setRefreshListener(){
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mNetworkDetector.isConnected()) {
                    //This prevents a refresh request if the user is not logged into Twitter
                    if(Twitter.getSessionManager().getActiveSession() != null) {
                        ContentDBHelper.getInstance(getContext()).refreshDB();
                    } else {
                        checkForTwitterLogin();
                        mRefreshLayout.setRefreshing(false);
                    }
                } else {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

        if(mNetworkDetector.isConnected()) {
            checkForTwitterLogin();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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

}
