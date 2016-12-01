package com.scottlindley.touchmelabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scottlindley.touchmelabs.Setup.DBAssetHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBAssetHelper dbSetup = new DBAssetHelper(MainActivity.this);
        dbSetup.getReadableDatabase();
    }
}
