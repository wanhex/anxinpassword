package com.wanhex.anxinpassword.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

    private static final String APP_SETTINGS_FILE_NAME = "settings";

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(APP_SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(APP_SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

}
