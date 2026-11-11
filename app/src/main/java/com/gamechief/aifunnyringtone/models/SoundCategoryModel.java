package com.gamechief.aifunnyringtone.models;

import java.util.ArrayList;

public class SoundCategoryModel {
    public int icon;
    public ArrayList<Integer> imageRes;
    public ArrayList<String> soundNames;
    public ArrayList<Integer> soundRes;
    public String title;

    public SoundCategoryModel(String title2, int icon2, ArrayList<Integer> soundRes2, ArrayList<Integer> imageRes2, ArrayList<String> soundNames2) {
        this.title = title2;
        this.icon = icon2;
        this.soundRes = soundRes2;
        this.imageRes = imageRes2;
        this.soundNames = soundNames2;
    }
}
