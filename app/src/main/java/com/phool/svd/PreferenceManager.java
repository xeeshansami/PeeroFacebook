package com.phool.svd;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by paxees on 2/8/2019.
 */

public class PreferenceManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;
    Context context;
    private static final String FIRST_LAUNCH = "firstLaunch";
    int MODE = 0;
    private static final String PREFERENCE = "Javapapers";

    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE, MODE);
        spEditor = sharedPreferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        spEditor.putBoolean(FIRST_LAUNCH, isFirstTime);
        spEditor.commit();
    }

    public boolean FirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }
}