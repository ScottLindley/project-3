package com.scottlindley.touchmelabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.List;

/**
 * "Home screen" fragment. Main purpose is to display the RecyclerView of {@link CardContent} objects.
 */

public class CardListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private CardRecyclerViewAdapter mAdapter;
    private List<CardContent> mCardList;
    public static final int CARD_LIST_LENGTH = 27;

    public CardListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CardListFragment newInstance(String param1) {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
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

                //Get weather object data to display
                String weatherCity = intent.getStringExtra("main weatherCity");
                String weatherDesc = intent.getStringExtra("main weatherContent");
                String weatherTemp = intent.getStringExtra("main weatherTemperature");

                //Index counter for tweet and news arrays

                for(int i=1;i<CARD_LIST_LENGTH;i++) {
                    if(i % 3 == 0) {

                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter("card list"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView cardRecycler = (RecyclerView)getView().findViewById(R.id.whatever_the_rv_is);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
