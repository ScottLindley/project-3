package com.scottlindley.touchmelabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ExpandedTweetFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_HANDLE = "handle";
    private static final String ARG_TWEET = "tweet";
    private static final String ARG_TIME = "time";

    private String mName, mHandle, mTweet, mTime;

    private OnFragmentInteractionListener mListener;

    public ExpandedTweetFragment() {
        // Required empty public constructor
    }

    public static ExpandedTweetFragment newInstance(String name, String handle, String tweet, String time) {
        ExpandedTweetFragment fragment = new ExpandedTweetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_HANDLE, handle);
        args.putString(ARG_TWEET, tweet);
        args.putString(ARG_TIME, time);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mHandle = getArguments().getString(ARG_HANDLE);
            mTweet = getArguments().getString(ARG_TWEET);
            mTime = getArguments().getString(ARG_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expanded_tweet, container, false);
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
