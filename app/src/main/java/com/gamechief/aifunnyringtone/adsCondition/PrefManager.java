package com.gamechief.aifunnyringtone.adsCondition;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PrefManager {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared  preferences file name
    private static final String PREF_NAME = "my-intro-slider";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_GUIDED_VIEW_SHOWED = "is_guided_view_showed";
    private static final String PREF_NAMEFAV = "fav_prefs";
    private static final String KEY_FAVORITES = "favorites";



    public PrefManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTime);
        editor.commit();
    }

    public void setGuidedShown(boolean value){
        editor.putBoolean(IS_GUIDED_VIEW_SHOWED,value);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }
    public boolean isGuidedShown(){
        return sharedPreferences.getBoolean(IS_GUIDED_VIEW_SHOWED,false);
    }

    // Save or remove favorite
    public void setFavorite(int imageResId, boolean isFav) {
        Set<String> favorites = new HashSet<>(sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>()));
        String resIdStr = String.valueOf(imageResId);

        if (isFav) {
            favorites.add(resIdStr);
        } else {
            favorites.remove(resIdStr);
        }

        sharedPreferences.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    // Check if an int imageResId is a favorite
    public boolean isFavorite(int imageResId) {
        Set<String> favorites = sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>());
        return favorites.contains(String.valueOf(imageResId));
    }

    // Get all favorites as List<Integer>
    public ArrayList<Integer> getFavoriteList() {
        Set<String> stringSet = sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>());
        ArrayList<Integer> intList = new ArrayList<>();
        for (String s : stringSet) {
            try {
                intList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return intList;
    }
}


