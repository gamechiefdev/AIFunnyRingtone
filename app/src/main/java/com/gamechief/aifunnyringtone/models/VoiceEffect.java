package com.gamechief.aifunnyringtone.models;

public class VoiceEffect {
    private int icon;
    private String name;
    private float pitch;
    private float speed;

    public VoiceEffect(String name2, float pitch2, float speed2, int icon2) {
        this.name = name2;
        this.pitch = pitch2;
        this.speed = speed2;
        this.icon = icon2;
    }

    public String getName() {
        return this.name;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getIcon() {
        return this.icon;
    }

    public int getIconRes() {
        return this.icon;
    }
}
