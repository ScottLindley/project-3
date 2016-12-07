package com.scottlindley.Bundle;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scottlindley.Bundle.DetailView.AboutUsFragment;
import com.scottlindley.Bundle.DetailView.SettingsFragment;
import com.scottlindley.Bundle.MainView.CardListFragment;
import com.scottlindley.Bundle.Services.TwitterAppInfo;
import com.scottlindley.Bundle.Services.WeatherService;
import com.scottlindley.Bundle.Setup.DBAssetHelper;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

import static com.scottlindley.Bundle.RecyclerViewComponents.CurrentWeatherViewHolder.PERMISSION_LOCATION_REQUEST_CODE;
import static com.scottlindley.Bundle.RecyclerViewComponents.CurrentWeatherViewHolder.WEATHER_JOB_SERVICE_ID;

public class MainActivity extends AppCompatActivity implements CardListFragment.WeatherUpdateListener,
        NavigationView.OnNavigationItemSelectedListener,  CardListFragment.LoggedInListener{


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up Fabric for all Twitter API calls
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TwitterAppInfo.CONSUMER_KEY,TwitterAppInfo.CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig),new TweetUi(), new TweetComposer());

        setContentView(R.layout.activity_main);

        //Set up the local database file
        DBAssetHelper dbSetup = new DBAssetHelper(MainActivity.this);
        dbSetup.getReadableDatabase();

        //Create shared preferences "weather"
        SharedPreferences.Editor prefEditor = getSharedPreferences("weather", MODE_PRIVATE).edit();
        prefEditor.apply();

        //Create a cardlist fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        CardListFragment cardFragment = CardListFragment.newInstance();

        transaction.replace(R.id.main_fragment_container, cardFragment);
        transaction.commit();

        //Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up the drawer
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
        setUserNavInfo(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        }

        //Closes nav drawer if it is open
        @Override
        public void onBackPressed () {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        android.app.FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() >= 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();

        } else {

            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
    }

    //Loads navdrawer fragments when nav buttons are clicked
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Adding fragment navigation onItemSelected - click each item to navigate to the
        // respective fragment
        if (id == R.id.nav_home) {
            CardListFragment cardListFragment = CardListFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, cardListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {
            SettingsFragment settingsFragment = SettingsFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, settingsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_about_us) {
            AboutUsFragment aboutUsFragment = AboutUsFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment_container, aboutUsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //Sets the navdrawer header views to reflect User's Twitter profile
    private void setUserNavInfo(NavigationView navigationView){
        View headerView = navigationView.getHeaderView(0);
        final ImageView userPhoto = (ImageView) headerView.findViewById(R.id.twitter_profile_picture_drawer);
        final TextView userName =(TextView) headerView.findViewById(R.id.twitter_username_drawer);
        final TextView handleName =(TextView) headerView.findViewById(R.id.twitter_handle_drawer);

        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if(session!=null) {
            Call<User> userCall = Twitter.getApiClient(session).getAccountService().verifyCredentials(false, false);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    User user = result.data;
                    Picasso.with(MainActivity.this).load(user.profileImageUrl).into(userPhoto);
                    userName.setText(user.name);
                    handleName.setText("@" + user.screenName);
                }

                @Override
                public void failure(TwitterException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    //Listener method to trigger setUserNavInfo() when the user has finished logging in
    @Override
    public void assignNavBarValues() {
        setUserNavInfo((NavigationView) findViewById(R.id.nav_view));
    }

    /**
     * Requests a weather data refresh using WeatherService. It is used only when the user
     * has denied location permissions.
     * @param zip
     */
    @Override
    public void getUpdatedWeatherZip(String zip){
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("zip", zip);

        JobScheduler scheduler = (JobScheduler)this
                .getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo weatherZip = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID,
                new ComponentName(this, WeatherService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(600000)
                .setExtras(bundle)
                .build();
        scheduler.schedule(weatherZip);

        SharedPreferences preferences = getSharedPreferences("weather", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit()
                .putString("permission", "denied")
                .putString("zipcode", zip);
        editor.commit();

        redrawFragment();
    }


    /**
     * Request a weather data refresh using the WeatherService. It is used only when the
     * user has granted location permissions
     */
    @Override
    public void getUpdatedWeatherLongLat(){
        PersistableBundle pb = new PersistableBundle();
        pb.putString("long lat", "lat long");

        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo locationInfo = new JobInfo.Builder(WEATHER_JOB_SERVICE_ID,
                new ComponentName(this, WeatherService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(600000)
                .setExtras(pb)
                .build();
        scheduler.schedule(locationInfo);

        SharedPreferences preferences = getSharedPreferences("weather", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit()
                .putString("permission", "granted");
        editor.commit();

        redrawFragment();
    }

    /**
     * Necessary override to notify the login button a successful login occurred.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.findFragmentById(R.id.main_fragment_container)
                    .onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Is called immediately after the user responds to the location permissions request.
     * First a weather data refresh is requested and then the permissions status is saved
     * into shared preferences.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull final int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUpdatedWeatherLongLat();
                    SharedPreferences preferences = getSharedPreferences("weather", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit()
                            .putString("permission", "granted");
                    editor.commit();
                } else {
                    CardListFragment fragment = (CardListFragment)
                            getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
                    fragment.handlePermissionDenied();
                }
                break;
            default:
                Toast.makeText(this, "Only location permission needed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Replaces the current fragment with a new instance of the same fragment
     */
    @Override
    public void redrawFragment() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                CardListFragment cardFragment = CardListFragment.newInstance();

                transaction.replace(R.id.main_fragment_container, cardFragment);
                transaction.commit();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

}
