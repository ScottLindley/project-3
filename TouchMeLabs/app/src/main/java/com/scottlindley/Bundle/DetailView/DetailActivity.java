package com.scottlindley.Bundle.DetailView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.scottlindley.Bundle.R;
import com.twitter.sdk.android.Twitter;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
}
