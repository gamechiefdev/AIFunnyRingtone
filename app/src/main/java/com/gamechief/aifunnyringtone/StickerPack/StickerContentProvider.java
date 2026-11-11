package com.gamechief.aifunnyringtone.StickerPack;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.load.Key;
import com.gamechief.aifunnyringtone.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.lingala.zip4j.util.InternalZipConstants;

public class StickerContentProvider extends ContentProvider {
    public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
    public static Uri AUTHORITY_URI = new Uri.Builder().scheme("content").authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath("metadata").build();
    public static final String CONTENT_FILE_NAME = "contents.json";
    public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
    public static final String LICENSE_AGREENMENT_WEBSITE = "sticker_pack_license_agreement_website";
    private static final UriMatcher MATCHER = new UriMatcher(-1);
    static final String METADATA = "metadata";
    private static final int METADATA_CODE = 1;
    private static final int METADATA_CODE_FOR_SINGLE_PACK = 2;
    public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
    public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
    public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
    static final String STICKERS = "stickers";
    static final String STICKERS_ASSET = "stickers_asset";
    private static final int STICKERS_ASSET_CODE = 4;
    private static final int STICKERS_CODE = 3;
    public static final String STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji";
    public static final String STICKER_FILE_NAME_IN_QUERY = "sticker_file_name";
    public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
    public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
    public static final String STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name";
    public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
    private static final int STICKER_PACK_TRAY_ICON_CODE = 5;
    private List<StickerPack> stickerPackList;

    public boolean onCreate() {
        if (BuildConfig.CONTENT_PROVIDER_AUTHORITY.startsWith(((Context) Objects.requireNonNull(getContext())).getPackageName())) {
            MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, METADATA, 1);
            MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "metadata/*", 2);
            MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "stickers/*", 3);
            try {
                for (StickerPack stickerPack : getStickerPackList()) {
                    MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "stickers_asset/" + URLEncoder.encode(stickerPack.identifier, Key.STRING_CHARSET_NAME) + InternalZipConstants.ZIP_FILE_SEPARATOR + URLEncoder.encode(stickerPack.trayImageFile, Key.STRING_CHARSET_NAME), 5);
                    for (Sticker sticker : stickerPack.getStickers()) {
                        MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "stickers_asset/" + URLEncoder.encode(stickerPack.identifier, Key.STRING_CHARSET_NAME) + InternalZipConstants.ZIP_FILE_SEPARATOR + URLEncoder.encode(sticker.imageFileName, Key.STRING_CHARSET_NAME), 4);
                    }
                }
            } catch (Exception e) {
                Log.e(getContext().getPackageName(), "Error encoding URIs", e);
            }
            return true;
        }
        throw new IllegalStateException("Authority must start with package name: " + getContext().getPackageName());
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = MATCHER.match(uri);
        if (code == 1) {
            return getPackForAllStickerPacks(uri);
        }
        if (code == 2) {
            return getCursorForSingleStickerPack(uri);
        }
        if (code == 3) {
            return getStickersForAStickerPack(uri);
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String mode) {
        int matchCode = MATCHER.match(uri);
        if (matchCode == 4 || matchCode == 5) {
            return getImageAsset(uri);
        }
        return null;
    }

    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case 1:
                return "vnd.android.cursor.dir/vnd." + BuildConfig.CONTENT_PROVIDER_AUTHORITY + ".metadata";
            case 2:
                return "vnd.android.cursor.item/vnd." + BuildConfig.CONTENT_PROVIDER_AUTHORITY + ".metadata";
            case 3:
                return "vnd.android.cursor.dir/vnd." + BuildConfig.CONTENT_PROVIDER_AUTHORITY + ".stickers";
            case 4:
                return "image/webp";
            case 5:
                return "image/png";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private synchronized void readContentFile(Context context) {
        InputStream contentsInputStream;
        try {
            contentsInputStream = context.getAssets().open(CONTENT_FILE_NAME);
            this.stickerPackList = ContentFileParser.parseStickerPacks(contentsInputStream);
            if (contentsInputStream != null) {
                contentsInputStream.close();
            }
        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("contents.json file has some issues: " + e.getMessage(), e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        return;
    }

    public List<StickerPack> getStickerPackList() {
        if (this.stickerPackList == null) {
            readContentFile((Context) Objects.requireNonNull(getContext()));
        }
        return this.stickerPackList;
    }

    private Cursor getPackForAllStickerPacks(Uri uri) {
        return getStickerPackInfo(uri, getStickerPackList());
    }

    private Cursor getCursorForSingleStickerPack(Uri uri) {
        String identifier = uri.getLastPathSegment();
        for (StickerPack stickerPack : getStickerPackList()) {
            if (identifier.equals(stickerPack.identifier)) {
                return getStickerPackInfo(uri, Collections.singletonList(stickerPack));
            }
        }
        return getStickerPackInfo(uri, new ArrayList());
    }

    private Cursor getStickerPackInfo(Uri uri, List<StickerPack> stickerPackList2) {
        MatrixCursor cursor = new MatrixCursor(new String[]{STICKER_PACK_IDENTIFIER_IN_QUERY, "sticker_pack_name", STICKER_PACK_PUBLISHER_IN_QUERY, STICKER_PACK_ICON_IN_QUERY, ANDROID_APP_DOWNLOAD_LINK_IN_QUERY, IOS_APP_DOWNLOAD_LINK_IN_QUERY, PUBLISHER_EMAIL, PUBLISHER_WEBSITE, PRIVACY_POLICY_WEBSITE, LICENSE_AGREENMENT_WEBSITE});
        for (StickerPack stickerPack : stickerPackList2) {
            MatrixCursor.RowBuilder builder = cursor.newRow();
            builder.add(stickerPack.identifier);
            builder.add(stickerPack.name);
            builder.add(stickerPack.publisher);
            builder.add(stickerPack.trayImageFile);
            builder.add(stickerPack.androidPlayStoreLink);
            builder.add(stickerPack.iosAppStoreLink);
            builder.add(stickerPack.publisherEmail);
            builder.add(stickerPack.publisherWebsite);
            builder.add(stickerPack.privacyPolicyWebsite);
            builder.add(stickerPack.licenseAgreementWebsite);
        }
        cursor.setNotificationUri(((Context) Objects.requireNonNull(getContext())).getContentResolver(), uri);
        return cursor;
    }

    private Cursor getStickersForAStickerPack(Uri uri) {
        String identifier = uri.getLastPathSegment();
        MatrixCursor cursor = new MatrixCursor(new String[]{STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY});
        for (StickerPack stickerPack : getStickerPackList()) {
            if (identifier.equals(stickerPack.identifier)) {
                for (Sticker sticker : stickerPack.getStickers()) {
                    cursor.addRow(new Object[]{sticker.imageFileName, TextUtils.join(",", sticker.emojis)});
                }
            }
        }
        cursor.setNotificationUri(((Context) Objects.requireNonNull(getContext())).getContentResolver(), uri);
        return cursor;
    }

    private AssetFileDescriptor getImageAsset(Uri uri) {
        AssetManager am = ((Context) Objects.requireNonNull(getContext())).getAssets();
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 3) {
            try {
                String fileName = URLDecoder.decode(pathSegments.get(pathSegments.size() - 1), Key.STRING_CHARSET_NAME);
                String identifier = URLDecoder.decode(pathSegments.get(pathSegments.size() - 2), Key.STRING_CHARSET_NAME);
                if (TextUtils.isEmpty(identifier) || TextUtils.isEmpty(fileName)) {
                    throw new IllegalArgumentException("identifier or filename empty, uri: " + uri);
                }
                for (StickerPack stickerPack : getStickerPackList()) {
                    if (identifier.equals(stickerPack.identifier)) {
                        if (fileName.equals(stickerPack.trayImageFile)) {
                            return fetchFile(uri, am, fileName, identifier);
                        }
                        for (Sticker sticker : stickerPack.getStickers()) {
                            if (fileName.equals(sticker.imageFileName)) {
                                return fetchFile(uri, am, fileName, identifier);
                            }
                        }
                        continue;
                    }
                }
                return null;
            } catch (Exception e) {
                Log.e(getContext().getPackageName(), "Error decoding URI: " + uri, e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("path segments should be 3, uri is: " + uri);
        }
    }

    private AssetFileDescriptor fetchFile(Uri uri, AssetManager am, String fileName, String identifier) {
        try {
            return am.openFd(identifier + InternalZipConstants.ZIP_FILE_SEPARATOR + fileName);
        } catch (IOException e) {
            Log.e(((Context) Objects.requireNonNull(getContext())).getPackageName(), "IOException when getting asset file, uri:" + uri, e);
            return null;
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not supported");
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }
}
