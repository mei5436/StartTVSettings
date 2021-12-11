package com.bestv;

import android.app.Application;
import android.content.Context;

public class TvSystemApp extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getInstance() {
        return sContext;
    }
}
