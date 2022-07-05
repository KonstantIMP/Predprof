package org.akred.predprof;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class PredprofApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
