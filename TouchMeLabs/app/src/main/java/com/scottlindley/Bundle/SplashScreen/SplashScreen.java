package com.scottlindley.Bundle.SplashScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.scottlindley.Bundle.MainActivity;
import com.scottlindley.Bundle.R;

/**
 * Created by jay on 12/3/16.
 */

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

                // Running logo rotate animation
                iv.startAnimation(an2);

                finish();

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                /** TODO - trying to start the second animation to fade in the app name text
                tv.startAnimation(an3);

                finish();

                Intent fi = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(fi);*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
