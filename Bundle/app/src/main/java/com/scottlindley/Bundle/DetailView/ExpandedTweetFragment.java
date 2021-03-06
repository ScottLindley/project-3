package com.scottlindley.Bundle.DetailView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.scottlindley.Bundle.NetworkConnectionDetector;
import com.scottlindley.Bundle.R;
import com.scottlindley.Bundle.Services.TwitterAppInfo;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.List;

import retrofit2.Call;


public class ExpandedTweetFragment extends Fragment {

    private static final String ARG_ID = "id";

    private FrameLayout mTweetContainer;

    private long mID;

    public ExpandedTweetFragment() {
        // Required empty public constructor
    }

    public static ExpandedTweetFragment newInstance(long id) {
        ExpandedTweetFragment fragment = new ExpandedTweetFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Check that we've received a valid tweet id
            long longID = getArguments().getLong(ARG_ID);
            if(longID != -1){
                mID = longID;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expanded_tweet, container, false);
        mTweetContainer = (FrameLayout)rootView.findViewById(R.id.tweet_container);
        return rootView;
    }

    //
    @Override
    public void onAttach(final Context context) {
        super.onAttach(getContext());

        //Check for network connection
        NetworkConnectionDetector detector = new NetworkConnectionDetector(context);
        if(detector.isConnected()){
            TwitterSession session = Twitter.getSessionManager().getActiveSession();
            //Make an api call to grab the users timeline
            Call<List<Tweet>> timelineCall = Twitter.getApiClient(session).getStatusesService().homeTimeline(
                    18, null, null, null, null, null, null);
            timelineCall.enqueue(new Callback<List<Tweet>>(){
                @Override
                public void success(Result<List<Tweet>> result) {
                    Tweet selectedTweet = null;
                    //Loop through the timeline and find the tweet with the matching ID
                    for(Tweet tweet : result.data){
                        if (tweet.id == mID){
                            selectedTweet = tweet;
                        }
                    }
                    //Display the tweet in a new TweetView
                    mTweetContainer.addView(new TweetView(getActivity(), selectedTweet));
                }

                @Override
                public void failure(TwitterException exception) {
                    exception.printStackTrace();
                }
            });
        }else{
            Toast.makeText(context, "No Network Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
