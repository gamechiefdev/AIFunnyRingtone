package com.gamechief.aifunnyringtone.models;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryStorage {
    private static final String KEY_FAVORITE = "favorite_list";
    private static final String KEY_HISTORY = "history_list";
    private static final String KEY_RECENT = "recent_sound";
    private static final String KEY_TRIMMED = "trimmed_list";
    private static final String PREF_NAME_FAVORITE = "sound_favorites";
    private static final String PREF_NAME_HISTORY = "sound_history";
    private static final String PREF_NAME_RECENT = "sound_recent";
    private static final String PREF_NAME_TRIMMED = "trimmed_history";
    private static final Gson gson = new Gson();

    public static void saveHistory(Context context, List<SoundModel> list) {
        context.getSharedPreferences(PREF_NAME_HISTORY, 0).edit().putString(KEY_HISTORY, gson.toJson((Object) list)).apply();
    }

    public static List<SoundModel> loadHistory(Context context) {
        String json = context.getSharedPreferences(PREF_NAME_HISTORY, 0).getString(KEY_HISTORY, (String) null);
        if (json == null) {
            return new ArrayList();
        }
        return (List) gson.fromJson(json, new TypeToken<List<SoundModel>>() {
        }.getType());
    }

    public static void saveTrimmedSound(Context context, SoundModel soundModel) {
        List<SoundModel> trimmedList = loadTrimmedHistory(context);
        trimmedList.add(0, soundModel);
        saveFullTrimmedList(context, trimmedList);
    }
    
    public static void saveFullTrimmedList(Context context, List<SoundModel> list) {
        context.getSharedPreferences(PREF_NAME_TRIMMED, 0).edit().putString(KEY_TRIMMED, gson.toJson((Object) list)).apply();
    }

    public static List<SoundModel> loadTrimmedHistory(Context context) {
        String json = context.getSharedPreferences(PREF_NAME_TRIMMED, 0).getString(KEY_TRIMMED, (String) null);
        if (json == null) {
            return new ArrayList();
        }
        return (List) gson.fromJson(json, new TypeToken<List<SoundModel>>() {
        }.getType());
    }

    public static void clearTrimmedHistory(Context context) {
        context.getSharedPreferences(PREF_NAME_TRIMMED, 0).edit().remove(KEY_TRIMMED).apply();
    }

    public static void saveFavorite(Context context, SoundModel soundModel) {
        List<SoundModel> favorites = loadFavorites(context);
        boolean found = false;
        int i = 0;
        while (true) {
            if (i >= favorites.size()) {
                break;
            } else if (favorites.get(i).getSoundResId() == soundModel.getSoundResId()) {
                favorites.set(i, soundModel);
                found = true;
                break;
            } else {
                i++;
            }
        }
        if (!found) {
            favorites.add(soundModel);
        }
        context.getSharedPreferences(PREF_NAME_FAVORITE, 0).edit().putString(KEY_FAVORITE, gson.toJson((Object) favorites)).apply();
    }

    public static List<SoundModel> loadFavorites(Context context) {
        String json = context.getSharedPreferences(PREF_NAME_FAVORITE, 0).getString(KEY_FAVORITE, (String) null);
        if (json == null) {
            return new ArrayList();
        }
        Type type = new TypeToken<List<SoundModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void removeFavorite(Context context, SoundModel soundModel) {
        List<SoundModel> favorites = loadFavorites(context);
        favorites.removeIf(s -> s.getSoundResId() == soundModel.getSoundResId());
        context.getSharedPreferences(PREF_NAME_FAVORITE, 0).edit().putString(KEY_FAVORITE, gson.toJson((Object) favorites)).apply();
    }

    public static void saveRecent(Context ctx, SoundModel item) {
        ctx.getSharedPreferences(PREF_NAME_HISTORY, 0).edit().putString(KEY_RECENT, gson.toJson((Object) item)).apply();
    }

    public static SoundModel loadRecent(Context ctx) {
        String json = ctx.getSharedPreferences(PREF_NAME_HISTORY, 0).getString(KEY_RECENT, (String) null);
        if (json == null) {
            return null;
        }
        return (SoundModel) gson.fromJson(json, SoundModel.class);
    }

    public static void clearRecent(Context ctx) {
        ctx.getSharedPreferences(PREF_NAME_HISTORY, 0).edit().remove(KEY_RECENT).apply();
    }
}
