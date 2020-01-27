package com.glowingsoft.carplaterecognizer.api;

import android.app.Application;
import android.util.Log;

public class GlobleClass extends Application {
    private static final String TAG="Global class";
    public static final String BASE_URL = "https://api.platerecognizer.com/";
    public static GlobleClass singleton;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("response", "onCreate: url sent");
        singleton=this;
    }
    public static GlobleClass getInstance(){
        return singleton;
    }
}