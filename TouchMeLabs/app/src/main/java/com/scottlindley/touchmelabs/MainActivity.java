package com.scottlindley.touchmelabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.scottlindley.touchmelabs.DetailView.AboutUsFragment;
import com.scottlindley.touchmelabs.DetailView.DetailActivity;
import com.scottlindley.touchmelabs.DetailView.ExpandedTweetFragment;
import com.scottlindley.touchmelabs.DetailView.SettingsFragment;
import com.scottlindley.touchmelabs.MainView.CardListFragment;
import com.scottlindley.touchmelabs.Services.TwitterAppInfo;
import com.scottlindley.touchmelabs.Setup.DBAssetHelper;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements CardListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener,
        ExpandedTweetFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
        AboutUsFragment.OnFragmentInteractionListener {

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterAppInfo.CONSUMER_KEY, TwitterAppInfo.CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetUi());

        setContentView(R.layout.activity_main);

        DBAssetHelper dbSetup = new DBAssetHelper(MainActivity.this);
        dbSetup.getReadableDatabase();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        CardListFragment cardFragment = CardListFragment.newInstance();

        transaction.add(R.id.main_fragment_container, cardFragment);
        transaction.commit();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        /** What I'm trying to do here is to get the fragments to stay in the backStack, so
         * when the back button is pressed, we can return to the previous fragment...
         * it's not working at the moment though. */

        android.app.FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() >= 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();

        } else {

            Log.i("MainActivity", "nothing on backstack, calling super");
        }

        /**
         * ^^ simply a footnote comment-out to denote the segment of code that seems problematic/
         * needs work
         * */

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Adding fragment navigation onItemSelected - click each item to navigate to the
        // respective fragment
        if (id == R.id.nav_home) {
            CardListFragment cardListFragment = new CardListFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, cardListFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {
            SettingsFragment settingsFragment = new SettingsFragment(); // TODO - create this fragment ~ Jay... created and working on it
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, settingsFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_about_us) {
            AboutUsFragment aboutUsFragment = new AboutUsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, aboutUsFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.findFragmentById(R.id.main_fragment_container)
                    .onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}