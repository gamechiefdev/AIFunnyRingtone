package com.gamechief.aifunnyringtone.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gamechief.aifunnyringtone.BuildConfig;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.StickerPack.Sticker;
import com.gamechief.aifunnyringtone.StickerPack.StickerPack;
import com.gamechief.aifunnyringtone.StickerPack.StickerPackLoader;
import com.gamechief.aifunnyringtone.StickerPack.WhitelistCheck;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;


public abstract class AddStickerPackActivity extends ParentActivity {
    public static final int ADD_PACK = 200;
    public static final String TAG = "StickerPackValidator";

    /* access modifiers changed from: protected */
    public void addStickerPackToWhatsApp(String identifier, String stickerPackName) {
        StickerPack stickerPack = StickerPackLoader.fetchStickerPackByIdentifier(getApplicationContext(), identifier);
        Log.d(TAG, "Attempting to add sticker pack: " + stickerPack.name + " (ID=" + stickerPack.identifier + ")");
        Log.d(TAG, "Tray image file: " + stickerPack.trayImageFile);
        for (Sticker sticker : stickerPack.getStickers()) {
            Log.d(TAG, "Sticker: " + sticker.imageFileName + " emojis=" + TextUtils.join(",", sticker.emojis));
        }
        try {
            if (WhitelistCheck.isWhatsAppConsumerAppInstalled(getPackageManager()) || WhitelistCheck.isWhatsAppSmbAppInstalled(getPackageManager())) {
                boolean stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, identifier);
                boolean stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, identifier);
                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToChooser(identifier, stickerPackName);
                } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                    launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
                } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
                } else {
                    Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "error adding sticker pack to WhatsApp", e);
            Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
        }
    }

    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
        Log.d(TAG, "Launching intent for specific package: " + whatsappPackageName);
        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        intent.setPackage(whatsappPackageName);
        try {
            startActivityForResult(intent, 200);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "WhatsApp package not found: " + whatsappPackageName, e);
            Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
        }
    }

    private void launchIntentToAddPackToChooser(String identifier, String stickerPackName) {
        Log.d(TAG, "Launching chooser to select WhatsApp app.");
        try {
            startActivityForResult(Intent.createChooser(createIntentToAddStickerPack(identifier, stickerPackName), getString(R.string.add_to_whatsapp)), 200);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "No app found to handle sticker pack intent.", e);
            Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
        }
    }

    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(ShowPack.EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(ShowPack.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra("sticker_pack_name", stickerPackName);
        Log.d(TAG, "Created intent for sticker pack: " + stickerPackName + " (ID=" + identifier + ")");
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult called. requestCode=" + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 200) {
            return;
        }
        if (resultCode != 0) {
            Log.d(TAG, "Sticker pack added successfully or resultCode=" + resultCode);
        } else if (data != null) {
            String validationError = data.getStringExtra("validation_error");
            if (validationError != null) {
                Log.e(TAG, "Sticker pack validation failed: " + validationError);
                boolean z = BuildConfig.DEBUG;
            }
        } else {
            Log.e(TAG, "Sticker pack add canceled with no validation error info.");
        }
    }
}
