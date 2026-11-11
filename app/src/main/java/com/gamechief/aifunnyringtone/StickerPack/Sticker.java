package com.gamechief.aifunnyringtone.StickerPack;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Sticker implements Parcelable {
    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };
    public List<String> emojis;
    public String imageFileName;
    public long size;

    public Sticker(String imageFileName2, List<String> emojis2) {
        this.imageFileName = imageFileName2;
        this.emojis = emojis2 != null ? emojis2 : new ArrayList<>();
    }

    protected Sticker(Parcel in) {
        this.imageFileName = in.readString();
        this.emojis = in.createStringArrayList();
        if (this.emojis == null) {
            this.emojis = new ArrayList();
        }
        this.size = in.readLong();
    }

    public void setSize(long size2) {
        this.size = size2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageFileName);
        dest.writeStringList(this.emojis);
        dest.writeLong(this.size);
    }

    public String getImageFileName() {
        return this.imageFileName;
    }

    public List<String> getEmojis() {
        return this.emojis;
    }

    public long getSize() {
        return this.size;
    }
}
