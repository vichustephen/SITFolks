package com.seven.zion.sitfolks;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Stephen on 08-Mar-17.
 */

public class SITFolks extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
