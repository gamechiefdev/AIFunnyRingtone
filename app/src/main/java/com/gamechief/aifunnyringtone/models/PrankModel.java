package com.gamechief.aifunnyringtone.models;

public class PrankModel {
    private int image;
    private String name;
    private int position;

    public PrankModel(int image2, String name2, int position2) {
        this.image = image2;
        this.name = name2;
        this.position = position2;
    }

    public int getImage() {
        return this.image;
    }

    public String getName() {
        return this.name;
    }

    public int getPosition() {
        return this.position;
    }
}
