package com.scottlindley.touchmelabs;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * "Home screen" fragment. Main purpose is to display the RecyclerView of {@link CardContent} objects.
 */

public class CardListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private CardRecyclerViewAdapter mAdapter;
    private List<CardContent> mCardList;
    public static final int CARD_LIST_LENGTH = 27;

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
        SharedPreferences preferences = context.getSharedPreferences("weather", Context.MODE_PRIVATE);

        String cityName = preferences.getString("city name", "error");
        String cityTemp = preferences.getString("city temp", "error");
        String cityConditions = preferences.getString("city conditions", "error");



        RecyclerView cardRecycler = (RecyclerView)getView().findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cardRecycler.setLayoutManager(manager);
        mAdapter = new CardRecyclerViewAdapter(mCardList);
        cardRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUpBroadcastReceiverForRecyclerView() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Get twitter data to display
                String[] tweetNames = intent.getStringArrayExtra("main tweetNames");
                String[] tweetContents = intent.getStringArrayExtra("main tweetContents");
                String[] tweetUsernames = intent.getStringArrayExtra("main tweetUsernames");
                String[] tweetTimes = intent.getStringArrayExtra("main tweetTimes");
                String[] tweetIds = intent.getStringArrayExtra("main tweetIds");

                //Get news data to display
                String[] newsNames = intent.getStringArrayExtra("main newsNames");
                String[] newsContents = intent.getStringArrayExtra("main newsContents");
                String[] newsLinks = intent.getStringArrayExtra("main newsLinks");

                //Convert into Model Java Objects
                List<TweetInfo> tweets = convertToTwitterInfo(
                        tweetNames,
                        tweetContents,
                        tweetUsernames,
                        tweetTimes,
                        tweetIds
                );

                List<NewsStories> stories = convertToNewsStory(
                        newsNames,
                        newsContents,
                        newsLinks
                );

                //Index counter for tweet and news arrays
                int tweetIndexCounter = 0;
                int storyIndexCounter = 0;

                for(int i=1;i<CARD_LIST_LENGTH;i++) {
                    if(i % 3 == 0) {
                        mCardList.add(stories.get(storyIndexCounter));
                        storyIndexCounter++;
                    } else {
                        mCardList.add(tweets.get(tweetIndexCounter));
                        tweetIndexCounter++;
                    }
                }

                mAdapter.notifyDataSetChanged();
                
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("card list"));
    }

    public void requestDataRefresh(Context context){
        mCardList.clear();
        mCardList.addAll(ContentDBHelper.getInstance(context).getUpdatedCardList());
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public List<TweetInfo> convertToTwitterInfo(String[] names, String[] contents, String[] usernames, String[] times, String[] ids){
        List<TweetInfo> tweets = new ArrayList<>();
        for(int i=0; i<names.length; i++){
            tweets.add(new TweetInfo(
                names[i], contents[i], usernames[i], times[i], ids[i]
            ));
        }
        return tweets;
    }

    public List<NewsStory> convertToNewsStory(String[] names, String[] contents, String[] links){
        List<NewsStory> stories = new ArrayList<>();
        for(int i=0; i<names.length; i++){
            stories.add(new NewsStory(
                    names[i], contents[i], links[i]
            ));
        }
        return stories;
    }

}
