package com.scottlindley.touchmelabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.scottlindley.touchmelabs.Services.TwitterAppInfo;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;

public class DetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterAppInfo.CONSUMER_KEY,TwitterAppInfo.CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig),new TweetUi());

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        Intent receivedIntent = getIntent();
        String intentID = receivedIntent.getStringExtra("card identifier");
        switch (intentID) {
            case "NewsStory":
                String title = receivedIntent.getStringExtra("name");
                String content = receivedIntent.getStringExtra("content");
                String link = receivedIntent.getStringExtra("link");
                ExpandedNewsFragment newsFragment = ExpandedNewsFragment.newInstance(title, content, link);
                fragmentTransaction.replace(R.id.detail_fragment_container, newsFragment);
                fragmentTransaction.commit();
                break;
            case "TweetInfo":
                //This check to verify that the user is already logged into Twitter
                if (Twitter.getSessionManager().getActiveSession() != null) {
                    String id = receivedIntent.getStringExtra("id");
                    //Convert string id to a long
                    long longID = -1;
                    try {
                        longID = Long.parseLong(id);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    ExpandedTweetFragment tweetFragment = ExpandedTweetFragment.newInstance(longID);
                    fragmentTransaction.replace(R.id.detail_fragment_container, tweetFragment);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(this, "You're not logged in to Twitter!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
