package com.scottlindley.touchmelabs.DetailView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.scottlindley.touchmelabs.NetworkConnectionDetector;
import com.scottlindley.touchmelabs.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;


public class ExpandedTweetFragment extends Fragment {
    private static final String ARG_ID = "id";

    private FrameLayout mTweetContainer;

    private long mID;

    private OnFragmentInteractionListener mListener;

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
            long longID = getArguments().getLong(ARG_ID);
            if(longID != -1){
                mID = longID;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_expanded_tweet, container, false);
        mTweetContainer = (FrameLayout)rootView.findViewById(R.id.tweet_container);
        return rootView;
    }


    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        NetworkConnectionDetector detector = new NetworkConnectionDetector(context);
        if(detector.isConnected()){
            TweetUtils.loadTweet(mID, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    Tweet tweet = result.data;
                    mTweetContainer.addView(new TweetView(context, tweet));
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
