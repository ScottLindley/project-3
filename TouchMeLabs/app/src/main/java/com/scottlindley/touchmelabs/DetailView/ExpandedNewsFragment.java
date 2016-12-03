package com.scottlindley.touchmelabs.DetailView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.scottlindley.touchmelabs.NetworkConnectionDetector;
import com.scottlindley.touchmelabs.R;


public class ExpandedNewsFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content.db";
    private static final String ARG_URL = "url";

    private WebView mWebView;

    private String mTitle;
    private String mContent;
    private String mURL;

    private OnFragmentInteractionListener mListener;

    public ExpandedNewsFragment() {
        // Required empty public constructor
    }

    public static ExpandedNewsFragment newInstance(String title, String content, String link) {
        ExpandedNewsFragment fragment = new ExpandedNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_URL, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mContent = getArguments().getString(ARG_CONTENT);
            mURL = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_expanded_news, container, false);
        mWebView = (WebView)rootView.findViewById(R.id.webview);
        return rootView;
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

        NetworkConnectionDetector detector = new NetworkConnectionDetector(context);
        if(detector.isConnected()){
            mWebView.loadUrl(mURL);
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
