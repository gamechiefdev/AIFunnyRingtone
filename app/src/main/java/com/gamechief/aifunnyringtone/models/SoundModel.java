package com.gamechief.aifunnyringtone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SoundModel implements Parcelable {
    public static final Creator<SoundModel> CREATOR = new Creator<SoundModel>() {
        public SoundModel createFromParcel(Parcel in) {
            return new SoundModel(in);
        }

        public SoundModel[] newArray(int size) {
            return new SoundModel[size];
        }
    };
    private String duration;
    private boolean favorite;
    private String filePath;
    private int imageResId;
    private String name;
    private String number;
    private int soundResId;

    public SoundModel(String number2, int imageResId2, String name2, String duration2, boolean favorite2, int soundResId2) {
        this.number = number2;
        this.imageResId = imageResId2;
        this.name = name2;
        this.duration = duration2;
        this.favorite = favorite2;
        this.soundResId = soundResId2;
    }

    public SoundModel(String name2, String filePath2) {
        this.number = "";
        this.imageResId = 0;
        this.name = name2;
        this.duration = "0:00";
        this.favorite = false;
        this.soundResId = 0;
        this.filePath = filePath2;
    }

    protected SoundModel(Parcel in) {
        this.number = in.readString();
        this.imageResId = in.readInt();
        this.name = in.readString();
        this.duration = in.readString();
        this.favorite = in.readByte() != 0;
        this.soundResId = in.readInt();
        this.filePath = in.readString();
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getImageResId() {
        return this.imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public int getSoundResId() {
        return this.soundResId;
    }

    public void setSoundResId(int soundResId) {
        this.soundResId = soundResId;
    }

    public void setFavorite(boolean favorite2) {
        this.favorite = favorite2;
    }

    public int describeContents() {
        return 0;
    }

    public void setFilePath(String filePath2) {
        this.filePath = filePath2;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.number);
        parcel.writeInt(this.imageResId);
        parcel.writeString(this.name);
        parcel.writeString(this.duration);
        parcel.writeByte(this.favorite ? (byte) 1 : 0);
        parcel.writeInt(this.soundResId);
        parcel.writeString(this.filePath);
    }
}
