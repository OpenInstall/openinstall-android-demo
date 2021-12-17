package com.fm.openinstalldemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Store {

    private static final String SHARED_NAME = "openinstall";
    public static final String KEY_PRIVACY = "_privacy_";
    public static final String KET_GET = "_get_";

    public static void sharedPreferencesPut(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String sharedPreferencesGet(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

}
