package com.scottlindley.touchmelabs.DetailView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.scottlindley.touchmelabs.R;
import com.twitter.sdk.android.Twitter;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private long mLongID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLongID = -1;
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getIntentInfo();
    }

    private void getIntentInfo(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Intent receivedIntent = getIntent();
        String intentID = receivedIntent.getStringExtra("card identifier");
        switch (intentID) {
            case "story":
                String link = receivedIntent.getStringExtra("link");
                ExpandedNewsFragment newsFragment = ExpandedNewsFragment.newInstance(link);
                fragmentTransaction.replace(R.id.detail_fragment_container, newsFragment);
                fragmentTransaction.commit();
                break;
            case "tweet":
                //This check to verify that the user is already logged into Twitter
                if (Twitter.getSessionManager().getActiveSession() != null) {
                    if(mLongID==-1) {
                        String id = receivedIntent.getStringExtra("id");
                        //Convert string id to a long
                        try {
                            mLongID = Long.parseLong(id);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        ExpandedTweetFragment tweetFragment = ExpandedTweetFragment.newInstance(mLongID);
                        fragmentTransaction.replace(R.id.detail_fragment_container, tweetFragment);
                        fragmentTransaction.commit();
                    }else{
                        Log.d(TAG, "getIntentInfo: "+mLongID);
                        ExpandedTweetFragment tweetFragment = ExpandedTweetFragment.newInstance(mLongID);
                        fragmentTransaction.replace(R.id.detail_fragment_container, tweetFragment);
                        fragmentTransaction.commit();
                    }
                } else {
                    Toast.makeText(this, "You're not logged in to Twitter!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("tweetID", mLongID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Long id = savedInstanceState.getLong("tweetID");
        if(id!=null){
            mLongID = id;
        }
    }
}
