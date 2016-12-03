package com.scottlindley.touchmelabs.Setup;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by jonlieblich on 12/1/16.
 */

public class DBAssetHelper extends SQLiteAssetHelper{
    private static final String DB_NAME = "content.db";
    private static final int DB_VERSION = 1;

    public DBAssetHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
}
