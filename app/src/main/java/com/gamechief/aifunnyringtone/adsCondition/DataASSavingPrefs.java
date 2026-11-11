package com.gamechief.aifunnyringtone.adsCondition;



import android.content.Context;
import android.content.SharedPreferences;

public class DataASSavingPrefs {

    // Shared preferences file name
    private String PREF_NAME = "mpPref";
    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;
    boolean defValue = false;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    public boolean setStringPrefValue(Context context, String key, String value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getStringPrefValue(Context context, String key) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(key, "");
    }
    public String getStringPrefValue(Context context, String key,String defaultValue) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(key, defaultValue);
    }


    public boolean setIntPrefValue(Context context, String key, int value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public long getLongPrefValue(Context context, String key) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        long defValue = 0;
        switch (key) {
        }
        return pref.getLong(key, defValue);
    }

    public boolean setLongPrefValue(Context context, String key, long value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public boolean setBooleanPrefValue(Context context, String key, boolean value) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }


    public boolean getBooleanPrefValue(Context context, String key) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getBoolean(key, defValue);
    }

}
