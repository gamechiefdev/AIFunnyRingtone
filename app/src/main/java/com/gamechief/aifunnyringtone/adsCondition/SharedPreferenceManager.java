package com.gamechief.aifunnyringtone.adsCondition;

import android.content.Context;
import android.content.SharedPreferences;

import com.gamechief.aifunnyringtone.models.SavedRingtone;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceManager {

    private static final String KEY_SAVED_RINGTONE_LIST = "saved_ringtone_list";

    protected Context mContext;
    protected SharedPreferences mSettings;
    protected SharedPreferences.Editor mEditor;

    public SharedPreferenceManager(Context ctx, String prefFileName) {

        if (ctx == null) {
            throw new IllegalArgumentException("Context is null");
        }

        this.mContext = ctx;
        this.mSettings = mContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);

        // ✅ FIX: initialize editor AFTER mSettings
        this.mEditor = mSettings.edit();
    }

    public void setValue(String key, String value) {
        this.mEditor.putString(key, value);
        this.mEditor.commit();
    }

    public void setIntValue(String key, int value) {
        this.mEditor.putInt(key, value);
        this.mEditor.commit();
    }

    public void setDoubleValue(String key, double value) {
        setValue(key, Double.toString(value));
    }

    public void setLongValue(String key, long value) {
        this.mEditor.putLong(key, value);
        this.mEditor.commit();
    }

    public String getValue(String key, String defaultValue) {
        return this.mSettings.getString(key, defaultValue);
    }

    public int getIntValue(String key, int defaultValue) {
        return this.mSettings.getInt(key, defaultValue);
    }

    public long getLongValue(String key, long defaultValue) {
        return this.mSettings.getLong(key, defaultValue);
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return this.mSettings.getBoolean(key, defValue);
    }

    public void setBooleanValue(String key, boolean value) {
        this.mEditor.putBoolean(key, value);
        this.mEditor.commit();
    }

    public boolean clear() {
        try {
            this.mEditor.clear().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeValue(String key) {
        if (this.mEditor != null) {
            this.mEditor.remove(key).commit();
        }
    }

    public void saveRingtoneList(List<SavedRingtone> list) {
        this.mEditor.putString(KEY_SAVED_RINGTONE_LIST, new Gson().toJson((Object) list));
        this.mEditor.commit();
    }

    public List<SavedRingtone> getRingtoneList() {
        String json = this.mSettings.getString(KEY_SAVED_RINGTONE_LIST, (String) null);
        if (json == null) {
            return new ArrayList();
        }
        return (List) new Gson().fromJson(json, new TypeToken<List<SavedRingtone>>() {
        }.getType());
    }

    public void addRingtone(SavedRingtone ringtone) {
        List<SavedRingtone> list = getRingtoneList();
        list.add(ringtone);
        saveRingtoneList(list);
    }

    public void removeRingtone(int position) {
        List<SavedRingtone> list = getRingtoneList();
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            saveRingtoneList(list);
        }
    }
}
