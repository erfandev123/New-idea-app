package com.cutechat.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getString(Constants.PREF_USER_ID) != null;
    }

    public String getCurrentUserId() {
        return getString(Constants.PREF_USER_ID);
    }

    public void saveUserData(String userId, String username, String email, String profileImage) {
        putString(Constants.PREF_USER_ID, userId);
        putString(Constants.PREF_USERNAME, username);
        putString(Constants.PREF_EMAIL, email);
        putString(Constants.PREF_PROFILE_IMAGE, profileImage);
    }
}
