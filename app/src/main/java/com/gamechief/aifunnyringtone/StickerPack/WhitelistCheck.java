package com.gamechief.aifunnyringtone.StickerPack;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import com.gamechief.aifunnyringtone.BuildConfig;

public class WhitelistCheck {
    private static final String AUTHORITY_QUERY_PARAM = "authority";
    public static String CONSUMER_WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    private static final String CONTENT_PROVIDER = ".provider.sticker_whitelist_check";
    private static final String IDENTIFIER_QUERY_PARAM = "identifier";
    private static final String QUERY_PATH = "is_whitelisted";
    private static final String QUERY_RESULT_COLUMN_NAME = "result";
    public static String SMB_WHATSAPP_PACKAGE_NAME = "com.whatsapp.w4b";

    public static boolean isWhitelisted(Context context, String identifier) {
        try {
            if (!isWhatsAppConsumerAppInstalled(context.getPackageManager()) && !isWhatsAppSmbAppInstalled(context.getPackageManager())) {
                return false;
            }
            boolean consumerResult = isStickerPackWhitelistedInWhatsAppConsumer(context, identifier);
            boolean smbResult = isStickerPackWhitelistedInWhatsAppSmb(context, identifier);
            return consumerResult && smbResult;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isWhitelistedFromProvider(Context context, String identifier, String whatsappPackageName) {
        PackageManager packageManager = context.getPackageManager();
        if (!isPackageInstalled(whatsappPackageName, packageManager)) {
            return true;
        }
        String whatsappProviderAuthority = whatsappPackageName + CONTENT_PROVIDER;
        if (packageManager.resolveContentProvider(whatsappProviderAuthority, 128) == null) {
            return false;
        }
        
        Uri queryUri = new Uri.Builder()
                .scheme("content")
                .authority(whatsappProviderAuthority)
                .appendPath(QUERY_PATH)
                .appendQueryParameter(AUTHORITY_QUERY_PARAM, BuildConfig.CONTENT_PROVIDER_AUTHORITY)
                .appendQueryParameter(IDENTIFIER_QUERY_PARAM, identifier)
                .build();

        try (Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(QUERY_RESULT_COLUMN_NAME)) == 1;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return applicationInfo != null && applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isWhatsAppConsumerAppInstalled(PackageManager packageManager) {
        return isPackageInstalled(CONSUMER_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    public static boolean isWhatsAppSmbAppInstalled(PackageManager packageManager) {
        return isPackageInstalled(SMB_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    public static boolean isStickerPackWhitelistedInWhatsAppConsumer(Context context, String identifier) {
        return isWhitelistedFromProvider(context, identifier, CONSUMER_WHATSAPP_PACKAGE_NAME);
    }

    public static boolean isStickerPackWhitelistedInWhatsAppSmb(Context context, String identifier) {
        return isWhitelistedFromProvider(context, identifier, SMB_WHATSAPP_PACKAGE_NAME);
    }
}
