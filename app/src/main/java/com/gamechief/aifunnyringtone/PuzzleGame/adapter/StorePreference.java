package com.gamechief.aifunnyringtone.PuzzleGame.adapter;

import android.content.Context;
import android.content.SharedPreferences;

public class StorePreference {
    private Context mContext;
    private SharedPreferences.Editor mPreferenceEditor = this.mSharedPreferences.edit();
    private SharedPreferences mSharedPreferences = this.mContext.getSharedPreferences("JIGSAW", 0);

    public StorePreference(Context aContext) {
        this.mContext = aContext;
    }

    public StorePreference setString(String key, String value) {
        this.mPreferenceEditor.putString(key, value);
        this.mPreferenceEditor.commit();
        return null;
    }

    public void setInt(String key, int value) {
        this.mPreferenceEditor.putInt(key, value);
        this.mPreferenceEditor.commit();
    }

    public String getStringValue(String key) {
        String aName = this.mSharedPreferences.getString(key, (String) null);
        return aName == null ? "" : aName;
    }

    public int getIntValue(String key) {
        return this.mSharedPreferences.getInt(key, -1);
    }

    public void setBoolean(String key, boolean value) {
        this.mPreferenceEditor.putBoolean(key, value);
        this.mPreferenceEditor.commit();
    }

    public boolean getBooleanValue(String key) {
        return this.mSharedPreferences.getBoolean(key, false);
    }

    public void clearEditor(String name) {
        SharedPreferences.Editor editor = this.mContext.getSharedPreferences(name, 0).edit();
        editor.clear();
        editor.commit();
    }
}
