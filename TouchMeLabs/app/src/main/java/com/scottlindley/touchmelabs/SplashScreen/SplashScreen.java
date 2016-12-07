package com.scottlindley.touchmelabs.SplashScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.scottlindley.touchmelabs.MainActivity;
import com.scottlindley.touchmelabs.R;

/**
 * Created by jay on 12/3/16.
 */

// This activity is used to define how the splash screen animation will behave
// The animation is set (in Manifest) to LAUNCHER, so it runs prior to the CardListFragment being loaded
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        final ImageView iv = (ImageView)findViewById(R.id.app_logo);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out);

        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // Running logo rotation animation
                iv.startAnimation(an2);

                finish();

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
