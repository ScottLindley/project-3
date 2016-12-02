package com.scottlindley.touchmelabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpandedTweetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpandedTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpandedTweetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "name";
    private static final String ARG_HANDLE = "handle";
    private static final String ARG_TWEET = "tweet";
    private static final String ARG_TIME = "time";

    // TODO: Rename and change types of parameters
    private String mName, mHandle, mTweet, mTime;

    private OnFragmentInteractionListener mListener;

    public ExpandedTweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExpandedTweetFragment.
     */
    // TODO: Rename and change types and number of parameters
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
