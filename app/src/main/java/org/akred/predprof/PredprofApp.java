package org.akred.predprof;

import android.app.Application;
import android.content.Intent;

import com.google.android.material.color.DynamicColors;

import org.akred.predprof.ui.activities.MainActivity;

public class PredprofApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);

        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startIntent);
    }
}
