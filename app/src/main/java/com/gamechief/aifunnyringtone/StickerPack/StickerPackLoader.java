package com.gamechief.aifunnyringtone.StickerPack;

import static com.gamechief.aifunnyringtone.StickerPack.StickerContentProvider.STICKER_FILE_EMOJI_IN_QUERY;
import static com.gamechief.aifunnyringtone.StickerPack.StickerContentProvider.STICKER_FILE_NAME_IN_QUERY;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.gamechief.aifunnyringtone.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;

public class StickerPackLoader {
    public static ArrayList<StickerPack> fetchStickerPacks(Context context) throws IllegalStateException {
        Cursor cursor = context.getContentResolver().query(StickerContentProvider.AUTHORITY_URI, (String[]) null, (String) null, (String[]) null, (String) null);
        if (cursor != null) {
            HashSet<String> identifierSet = new HashSet<>();
            ArrayList<StickerPack> stickerPackList = fetchFromContentProvider(cursor);
            cursor.close();
            
            Iterator<StickerPack> it = stickerPackList.iterator();
            while (it.hasNext()) {
                StickerPack stickerPack = it.next();
                if (!identifierSet.contains(stickerPack.identifier)) {
                    identifierSet.add(stickerPack.identifier);
                } else {
                    throw new IllegalStateException("sticker pack identifiers should be unique, there are more than one pack with identifier:" + stickerPack.identifier);
                }
            }
            if (!stickerPackList.isEmpty()) {
                for (StickerPack stickerPack2 : stickerPackList) {
                    stickerPack2.setStickers(getStickersForPack(context, stickerPack2));
                    StickerPackValidator.verifyStickerPackValidity(context, stickerPack2);
                }
                return stickerPackList;
            }
            throw new IllegalStateException("There should be at least one sticker pack in the app");
        }
        throw new IllegalStateException("could not fetch from content provider");
    }

    private static List<Sticker> getStickersForPack(Context context, StickerPack stickerPack) {
        List<Sticker> stickers = fetchFromContentProviderForStickers(stickerPack.identifier, context.getContentResolver());
        for (Sticker sticker : stickers) {
            try {
                long size = getStickerAssetSize(stickerPack.identifier, sticker.imageFileName, context.getContentResolver());
                if (size > 0) {
                    sticker.setSize(size);
                } else {
                    throw new IllegalStateException("Asset file is empty, pack: " + stickerPack.name + ", sticker: " + sticker.imageFileName);
                }
            } catch (IOException | IllegalArgumentException e) {
                throw new IllegalStateException("Asset file doesn't exist. pack: " + stickerPack.name + ", sticker: " + sticker.imageFileName, e);
            }
        }
        return stickers;
    }

    private static long getStickerAssetSize(String identifier, String name, ContentResolver contentResolver) throws IOException {
        try (AssetFileDescriptor afd = contentResolver.openAssetFileDescriptor(getStickerAssetUri(identifier, name), "r")) {
            return afd != null ? afd.getLength() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private static ArrayList<StickerPack> fetchFromContentProvider(Cursor cursor) {
        ArrayList<StickerPack> stickerPackList = new ArrayList<>();
        if (cursor == null || !cursor.moveToFirst()) return stickerPackList;
        
        do {
            String identifier = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.STICKER_PACK_IDENTIFIER_IN_QUERY));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("sticker_pack_name"));
            String publisher = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.STICKER_PACK_PUBLISHER_IN_QUERY));
            String trayImage = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.STICKER_PACK_ICON_IN_QUERY));
            String androidPlayStoreLink = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.ANDROID_APP_DOWNLOAD_LINK_IN_QUERY));
            String iosAppLink = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.IOS_APP_DOWNLOAD_LINK_IN_QUERY));
            
            StickerPack stickerPack = new StickerPack(
                identifier, 
                name, 
                publisher, 
                trayImage, 
                cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.PUBLISHER_EMAIL)), 
                cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.PUBLISHER_WEBSITE)), 
                cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.PRIVACY_POLICY_WEBSITE)), 
                cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.LICENSE_AGREENMENT_WEBSITE))
            );
            stickerPack.setAndroidPlayStoreLink(androidPlayStoreLink);
            stickerPack.setIosAppStoreLink(iosAppLink);
            stickerPackList.add(stickerPack);
        } while (cursor.moveToNext());
        return stickerPackList;
    }

    @NonNull
    private static List<Sticker> fetchFromContentProviderForStickers(String identifier, ContentResolver contentResolver) {
        Uri uri = getStickerListUri(identifier);
        final String[] projection = {STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY};
        List<Sticker> stickers = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final String name = cursor.getString(cursor.getColumnIndexOrThrow(STICKER_FILE_NAME_IN_QUERY));
                    final String emojisConcatenated = cursor.getString(cursor.getColumnIndexOrThrow(STICKER_FILE_EMOJI_IN_QUERY));
                    stickers.add(new Sticker(name, Arrays.asList(emojisConcatenated.split(","))));
                } while (cursor.moveToNext());
            }
        }
        return stickers;
    }

    public static byte[] fetchStickerAsset(String identifier, String name, ContentResolver contentResolver) throws IOException {
        try (InputStream inputStream = contentResolver.openInputStream(getStickerAssetUri(identifier, name))) {
            if (inputStream == null) {
                throw new IOException("cannot read sticker asset:" + identifier + InternalZipConstants.ZIP_FILE_SEPARATOR + name);
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[16384];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    private static Uri getStickerListUri(String identifier) {
        return new Uri.Builder().scheme("content").authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath("stickers").appendPath(identifier).build();
    }

    public static Uri getStickerAssetUri(String identifier, String stickerName) {
        return new Uri.Builder().scheme("content").authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath("stickers_asset").appendPath(identifier).appendPath(stickerName).build();
    }

    public static StickerPack fetchStickerPackByIdentifier(Context context, String identifier) {
        for (StickerPack pack : fetchStickerPacks(context)) {
            if (pack.identifier.equals(identifier)) {
                return pack;
            }
        }
        throw new IllegalArgumentException("Sticker pack with identifier " + identifier + " not found");
    }
}
