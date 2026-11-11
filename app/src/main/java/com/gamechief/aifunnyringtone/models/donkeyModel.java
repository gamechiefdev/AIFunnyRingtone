package com.gamechief.aifunnyringtone.models;

import java.io.Serializable;

public class donkeyModel implements Serializable {

    private String name;
    private String des;
    private int imagge;
    private int videoRes;

    public donkeyModel(String name, int imagge, String des, int videoRes) {
        this.name = name;
        this.imagge = imagge;
        this.des = des;
        this.videoRes = videoRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImagge() {
        return imagge;
    }

    public void setImagge(int imagge) {
        this.imagge = imagge;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getVideoRes() {
        return videoRes;
    }

    public void setVideoRes(int videoRes) {
        this.videoRes = videoRes;
    }
}
