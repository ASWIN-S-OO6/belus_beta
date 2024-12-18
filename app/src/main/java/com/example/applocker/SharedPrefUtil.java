package com.example.applocker;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefUtil {
    private static final String SHARED_APP_PREFERENCE_NAME = "SharedPref";
    private SharedPreferences pref;

    public SharedPrefUtil(Context context) {
        this.pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefUtil getInstance(Context context) {
        return new SharedPrefUtil(context);
    }

    public void putString(String key, String value) {
        pref.edit().putString(key, value).apply();
    }

    public void putInteger(String key, int value) {
        pref.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        pref.edit().putBoolean(key, value).apply();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public int getInteger(String key) {
        return pref.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public void putListString(String key, List<String> list) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key + "_size", list.size());
        for (int i = 0; i < list.size(); i++) {
            editor.putString(key + "_" + i, list.get(i));
        }
        editor.apply();
    }

    public List<String> getListString(String key) {
        int size = pref.getInt(key + "_size", 0);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(pref.getString(key + "_" + i, null));
        }
        return list;
    }
}
