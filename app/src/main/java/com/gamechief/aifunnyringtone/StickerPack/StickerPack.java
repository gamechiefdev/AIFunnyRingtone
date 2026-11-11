package com.gamechief.aifunnyringtone.StickerPack;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class StickerPack implements Parcelable {
    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        public StickerPack createFromParcel(Parcel in) {
            return new StickerPack(in);
        }

        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };
    String androidPlayStoreLink;
    public String identifier;
    String iosAppStoreLink;
    private boolean isWhitelisted;
    final String licenseAgreementWebsite;
    public String name;
    final String privacyPolicyWebsite;
    public String publisher;
    final String publisherEmail;
    final String publisherWebsite;
    private List<Sticker> stickers;
    private long totalSize;
    public String trayImageFile;

    public StickerPack(String identifier2, String name2, String publisher2, String trayImageFile2, String publisherEmail2, String publisherWebsite2, String privacyPolicyWebsite2, String licenseAgreementWebsite2) {
        this.identifier = identifier2;
        this.name = name2;
        this.publisher = publisher2;
        this.trayImageFile = trayImageFile2;
        this.publisherEmail = publisherEmail2;
        this.publisherWebsite = publisherWebsite2;
        this.privacyPolicyWebsite = privacyPolicyWebsite2;
        this.licenseAgreementWebsite = licenseAgreementWebsite2;
        this.stickers = new ArrayList();
    }

    public void setIsWhitelisted(boolean isWhitelisted2) {
        this.isWhitelisted = isWhitelisted2;
    }

    public boolean getIsWhitelisted() {
        return this.isWhitelisted;
    }

    protected StickerPack(Parcel in) {
        this.identifier = in.readString();
        this.name = in.readString();
        this.publisher = in.readString();
        this.trayImageFile = in.readString();
        this.publisherEmail = in.readString();
        this.publisherWebsite = in.readString();
        this.privacyPolicyWebsite = in.readString();
        this.licenseAgreementWebsite = in.readString();
        this.iosAppStoreLink = in.readString();
        this.stickers = in.createTypedArrayList(Sticker.CREATOR);
        if (this.stickers == null) {
            this.stickers = new ArrayList();
        }
        this.totalSize = in.readLong();
        this.androidPlayStoreLink = in.readString();
        this.isWhitelisted = in.readByte() != 0;
    }

    public void setStickers(List<Sticker> stickers2) {
        this.stickers = stickers2 != null ? stickers2 : new ArrayList<>();
        this.totalSize = 0;
        for (Sticker sticker : this.stickers) {
            this.totalSize += sticker.size;
        }
    }

    public void setAndroidPlayStoreLink(String androidPlayStoreLink2) {
        this.androidPlayStoreLink = androidPlayStoreLink2;
    }

    public void setIosAppStoreLink(String iosAppStoreLink2) {
        this.iosAppStoreLink = iosAppStoreLink2;
    }

    public List<Sticker> getStickers() {
        return this.stickers;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeString(this.name);
        dest.writeString(this.publisher);
        dest.writeString(this.trayImageFile);
        dest.writeString(this.publisherEmail);
        dest.writeString(this.publisherWebsite);
        dest.writeString(this.privacyPolicyWebsite);
        dest.writeString(this.licenseAgreementWebsite);
        dest.writeString(this.iosAppStoreLink);
        dest.writeTypedList(this.stickers);
        dest.writeLong(this.totalSize);
        dest.writeString(this.androidPlayStoreLink);
        dest.writeByte(this.isWhitelisted ? (byte) 1 : 0);
    }
}
