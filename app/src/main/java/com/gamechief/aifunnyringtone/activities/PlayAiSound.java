package com.gamechief.aifunnyringtone.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.AppOpenManager;
import com.gamechief.aifunnyringtone.adsCondition.DataASSavingPrefs;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityPlayAiSoundBinding;
import com.gamechief.aifunnyringtone.databinding.PremiumDialogBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PlayAiSound extends ParentActivity {
    private static int clickCount = 0;
    private static final String PREF_IS_RINGTONE_SET_FREE = "is_ringtone_set_free_v1";
    private static final String PREF_IS_NOTIFICATION_SET_FREE = "is_notification_set_free_v1";
    private static final String PREF_IS_ALARM_SET_FREE = "is_alarm_set_free_v1";

    private String audioUrl;
    /* access modifiers changed from: private */
    public ActivityPlayAiSoundBinding binding;
    private Handler handler = new Handler();
    private boolean isPlaying = false;
    /* access modifiers changed from: private */
    public MediaPlayer mediaPlayer;
    private Runnable seekBarRunnable;
    /* access modifiers changed from: private */
    public boolean wasPlayingBeforeAd = false;

    private BillingClient billingClient;
    private ProductDetails currentProductDetails;
    private ProductDetails.SubscriptionOfferDetails targetOfferDetails;
    private ProductDetails.SubscriptionOfferDetails baseOfferDetails;
    private Dialog premiumDialog;
    private PremiumDialogBinding dialogBinding;
    private RewardedAd rewardedAd;

    private DataASSavingPrefs dataPrefs;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityPlayAiSoundBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        
        this.dataPrefs = new DataASSavingPrefs();

        this.binding.backbtn.setOnClickListener(onClickListener -> {
            onBackPressed();
        });
        
        this.audioUrl = getIntent().getStringExtra("soundUrl");
        String prompt = getIntent().getStringExtra("prompt_text");
        this.binding.name.setText(prompt);
        
        clickCount++;
        Log.d("PlayAiSound", "Click count: " + clickCount);

        if (clickCount % 5 == 0 && !AdsController.isInAppPurchased) {
            showInterstitialWithMusicControl();
        } else {
            if (audioUrl != null) {
                handler.postDelayed(() -> playSound(audioUrl), 1000);
            }
        }

        setupControls();
        setupSaveButton(prompt);
        setupSeekBarListener();
        this.binding.trimSound.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlayAiSound.this.openTrimScreen();
            }
        });

        final PopupMenu popupMenu = new PopupMenu(this, this.binding.menubtn, 17);
        popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String prefKey = "";
                String optionName = "";
                if (item.getItemId() == R.id.set_ringtone) {
                    prefKey = PREF_IS_RINGTONE_SET_FREE;
                    optionName = "Ringtone";
                } else if (item.getItemId() == R.id.set_notification_sound) {
                    prefKey = PREF_IS_NOTIFICATION_SET_FREE;
                    optionName = "Notification";
                } else if (item.getItemId() == R.id.set_alarm_sound) {
                    prefKey = PREF_IS_ALARM_SET_FREE;
                    optionName = "Alarm";
                }

                if (prefKey.isEmpty()) return false;

                String finalPrefKey = prefKey;
                String finalOptionName = optionName;
                Runnable action = () -> {
                    if (audioUrl != null && !audioUrl.isEmpty()) {
                        if (item.getItemId() == R.id.set_ringtone) {
                            ringtoneMethodFromUrl(audioUrl);
                        } else if (item.getItemId() == R.id.set_notification_sound) {
                            notificationMethodFromUrl(audioUrl);
                        } else if (item.getItemId() == R.id.set_alarm_sound) {
                            alarmMethodFromUrl(audioUrl);
                        }
                    } else {
                        Toast.makeText(PlayAiSound.this, "Audio not ready", Toast.LENGTH_SHORT).show();
                    }
                };
                checkLimitAndPerform(finalPrefKey, finalOptionName, action);
                return true;
            }
        });
        this.binding.menubtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        initBillingClient();
        initPremiumDialog();
    }

    private void checkLimitAndPerform(String prefKey, String optionName, Runnable action) {
        boolean alreadyUsedFree = dataPrefs.getBooleanPrefValue(this, prefKey);
        if (!alreadyUsedFree || AdsController.isIsInAppPurchased()) {
            if (!alreadyUsedFree) {
                dataPrefs.setBooleanPrefValue(this, prefKey, true);
            }
            action.run();
        } else {
            showLimitDialog(optionName, action);
        }
    }

    private void showLimitDialog(String optionName, Runnable action) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_generation_limit);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView title = dialog.findViewById(R.id.dialogTitle);
        if (title != null) title.setText("Set " + optionName + " Limit");

        TextView message = dialog.findViewById(R.id.dialogMessage);
        if (message != null) {
            message.setText("You have already used your free turn for this option. Watch an ad to set another or go premium for unlimited access.");
        }

        dialog.findViewById(R.id.btnWatchAd).setOnClickListener(v -> {
            dialog.dismiss();
            loadAndShowRewardedAd(action);
        });

        dialog.findViewById(R.id.btnGoPremium).setOnClickListener(v -> {
            dialog.dismiss();
            showPremiumDialog();
        });

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadAndShowRewardedAd(Runnable action) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ad...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.rewardAd_high), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                progressDialog.dismiss();
                rewardedAd = ad;
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        action.run();
                    }
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }
                });

                rewardedAd.show(PlayAiSound.this, rewardItem -> {
                    action.run();
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                RewardedAd.load(PlayAiSound.this, getString(R.string.rewardAd_medium), adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        progressDialog.dismiss();
                        rewardedAd = ad;
                        rewardedAd.show(PlayAiSound.this, rewardItem -> action.run());
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        progressDialog.dismiss();
                        Toast.makeText(PlayAiSound.this, "Failed to load ad. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    }
                })
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {}
        });
    }

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId("my_plan")
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                currentProductDetails = productDetailsList.get(0);
                if (currentProductDetails.getSubscriptionOfferDetails() != null) {
                    for (ProductDetails.SubscriptionOfferDetails offer : currentProductDetails.getSubscriptionOfferDetails()) {
                        if ("offer1".equals(offer.getOfferId()) || "weekly".equals(offer.getBasePlanId())) {
                            targetOfferDetails = offer;
                        }
                        if ("weekly".equals(offer.getBasePlanId())) {
                            baseOfferDetails = offer;
                        }
                    }
                }
                updatePriceInDialog();
            }
        });
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(params, billingResult -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    AdsController.setIsInAppPurchased(true);
                    if (premiumDialog != null) premiumDialog.dismiss();
                    Toast.makeText(this, "Premium Activated!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initPremiumDialog() {
        this.dialogBinding = PremiumDialogBinding.inflate(getLayoutInflater());
        this.premiumDialog = new Dialog(this);
        this.premiumDialog.setContentView(this.dialogBinding.getRoot());
        this.premiumDialog.setCancelable(true);

        if (this.premiumDialog.getWindow() != null) {
            this.premiumDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.premiumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        this.dialogBinding.closeview.setOnClickListener(v -> premiumDialog.dismiss());
        this.dialogBinding.buyPremium.setOnClickListener(v -> {
            if (currentProductDetails != null && targetOfferDetails != null) {
                launchPurchaseFlow();
            } else {
                Toast.makeText(this, "Please wait, loading offer...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPremiumDialog() {
        if (premiumDialog != null && !premiumDialog.isShowing()) {
            premiumDialog.show();
        }
    }

    private void updatePriceInDialog() {
        if (dialogBinding == null) return;
        runOnUiThread(() -> {
            String discountedPriceStr = getString(R.string.loading);
            if (targetOfferDetails != null) {
                for (ProductDetails.PricingPhase phase : targetOfferDetails.getPricingPhases().getPricingPhaseList()) {
                    if (phase.getPriceAmountMicros() > 0) {
                        discountedPriceStr = phase.getFormattedPrice();
                        break;
                    }
                }
            }
            dialogBinding.freeTrial.setText(getString(R.string.start_3_days_free_trial_flat) + " " + getString(R.string.discount_on_1st_order_and_after_that) + " " + discountedPriceStr + " " + getString(R.string.per_week_will_be_charged));
            dialogBinding.justPrice.setText(getString(R.string.discount_on_1st_order_and_after_that) + " " + getString(R.string.just) + " " + discountedPriceStr + " " + getString(R.string.per_month));
        });
    }

    private void launchPurchaseFlow() {
        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(currentProductDetails)
                .setOfferToken(targetOfferDetails.getOfferToken())
                .build();
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(productDetailsParams))
                .build();
        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    public void ringtoneMethodFromUrl(String audioUrl2) {
        setToneFromUrl(audioUrl2, 1, "AI_Ringtone");
    }

    public void notificationMethodFromUrl(String audioUrl2) {
        setToneFromUrl(audioUrl2, 2, "AI_Notification");
    }

    public void alarmMethodFromUrl(String audioUrl2) {
        setToneFromUrl(audioUrl2, 4, "AI_Alarm");
    }

    private void setToneFromUrl(String audioUrl2, int type, String title) {
        if (audioUrl2 == null || audioUrl2.isEmpty()) return;

        if (audioUrl2.startsWith("http")) {
            downloadAndSetTone(audioUrl2, type, title);
            return;
        }

        File audioFile = new File(audioUrl2);
        if (!audioFile.exists()) {
             Toast.makeText(this, "Audio not found!", Toast.LENGTH_SHORT).show();
             return;
        }

        performSetTone(audioFile, type, title);
    }

    private void downloadAndSetTone(String urlString, int type, String title) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading audio...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(urlString);
                java.net.URLConnection connection = url.openConnection();
                connection.connect();

                File cacheFile = new File(getCacheDir(), "temp_tone_" + System.currentTimeMillis() + ".mp3");
                java.io.InputStream input = new java.io.BufferedInputStream(url.openStream());
                java.io.OutputStream output = new java.io.FileOutputStream(cacheFile);

                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    performSetTone(cacheFile, type, title);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to download audio", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void performSetTone(File audioFile, int type, String title) {
        if (!Settings.System.canWrite(this)) {
            showDialogPermissionStorage(this, "Please allow modify system setting !!", R.drawable.write_settings_icon, 0);
            return;
        }

        if (Build.VERSION.SDK_INT >= 29) {
            String relativePath = Environment.DIRECTORY_RINGTONES + "/MyAIRingtones";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, title + "_" + System.currentTimeMillis() + ".mp3");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);
            if (type == 1) contentValues.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, true);
            else if (type == 2) contentValues.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, true);
            else if (type == 4) contentValues.put(MediaStore.Audio.AudioColumns.IS_ALARM, true);

            Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri newUri = getContentResolver().insert(contentUri, contentValues);

            try (OutputStream os = getContentResolver().openOutputStream(newUri);
                 InputStream is = new FileInputStream(audioFile)) {
                byte[] buffer = new byte[4096];
                int byteRead;
                while ((byteRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteRead);
                }
                RingtoneManager.setActualDefaultRingtoneUri(this, type, newUri);
                Toast.makeText(this, title + " set successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to set tone", Toast.LENGTH_SHORT).show();
            }
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DATA, audioFile.getAbsolutePath());
            contentValues.put(MediaStore.MediaColumns.TITLE, title);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            if (type == 1) contentValues.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, true);
            else if (type == 2) contentValues.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, true);
            else if (type == 4) contentValues.put(MediaStore.Audio.AudioColumns.IS_ALARM, true);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(audioFile.getAbsolutePath());
            Uri newUri = getContentResolver().insert(uri, contentValues);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, type, newUri);
                Toast.makeText(this, title + " set successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to set tone", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setRingtone(SoundModel sound) {
        String str = sound.getName() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str + ".mp3");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_ringtone", true);
            contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyRingtones");
            Uri mParse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + sound.getSoundResId());
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try (AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(mParse, "r");
                 OutputStream os = getContentResolver().openOutputStream(insert);
                 InputStream is = afd.createInputStream()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                RingtoneManager.setActualDefaultRingtoneUri(this, 1, insert);
                Toast.makeText(this, "Ringtone set : " + sound.getName(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                showDialogPermissionStorage(this, "Please allow modify system setting !!", R.drawable.write_settings_icon, 0);
            }
        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
            File mfile = new File(Environment.getExternalStorageDirectory() + "/MyRingtones", str + ".mp3");
            if (mfile.getParentFile() != null && !mfile.getParentFile().exists()) {
                mfile.getParentFile().mkdirs();
            }
            try (InputStream is = getContentResolver().openAssetFileDescriptor(Uri.parse("android.resource://" + getPackageName() + "/raw/" + sound.getSoundResId()), "r").createInputStream();
                 FileOutputStream fos = new FileOutputStream(mfile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", sound.getName());
            contentValues2.put("mime_type", "audio/mp3");
            contentValues2.put("_size", mfile.length());
            contentValues2.put("artist", getString(R.string.app_name));
            contentValues2.put("is_ringtone", true);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 1, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                Toast.makeText(this, "Ringtone set successfully!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void ringtoneMethod(SoundModel sound) {
        NotificationManager notificationASManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == 0 || notificationASManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
            if (!notificationASManager.isNotificationPolicyAccessGranted()) {
                showDialogdialogdonotdistrub(this, getString(R.string.app_name) + " requires Do Not Disturb permission for setting ringtones, alarm, or notification sound.", R.drawable.do_not_distrub, 0);
            } else if (Settings.System.canWrite(this)) {
                setRingtone(sound);
            } else {
                showDialogPermissionStorage(this, "Please allow modify system setting !!", R.drawable.write_settings_icon, 0);
            }
        } else if (Settings.System.canWrite(this)) {
            setRingtone(sound);
        } else {
            setRingtone(sound);
        }
    }

    public void setNotification(SoundModel soundModel) {
        if (soundModel == null) return;
        String str = soundModel.getName() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str + ".mp3");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_notification", true);
            contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
            Uri mParse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId());
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try (AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(mParse, "r");
                 OutputStream os = getContentResolver().openOutputStream(insert);
                 InputStream is = afd.createInputStream()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                RingtoneManager.setActualDefaultRingtoneUri(this, 2, insert);
                Toast.makeText(this, "Notification set: " + soundModel.getName(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
            }
        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
            File mfile = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds", str + ".mp3");
            if (mfile.getParentFile() != null && !mfile.getParentFile().exists()) {
                mfile.getParentFile().mkdirs();
            }
            try (InputStream is = getContentResolver().openAssetFileDescriptor(Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId()), "r").createInputStream();
                 FileOutputStream fos = new FileOutputStream(mfile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", soundModel.getName());
            contentValues2.put("mime_type", "audio/mp3");
            contentValues2.put("_size", mfile.length());
            contentValues2.put("artist", getString(R.string.app_name));
            contentValues2.put("is_notification", true);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 2, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                Toast.makeText(this, "Notification set successfully: " + soundModel.getName(), Toast.LENGTH_LONG).show();
            } catch (Throwable th) {
                Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void notificationMethod(SoundModel soundModel) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == 0 || notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                showDialogdialogdonotdistrub(this, getString(R.string.app_name) + " requires do not disturb permission for setting notification sounds.", R.drawable.do_not_distrub, 0);
            } else if (Settings.System.canWrite(this)) {
                setNotification(soundModel);
            } else {
                showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
            }
        } else if (Settings.System.canWrite(this)) {
            setNotification(soundModel);
        } else {
            setNotification(soundModel);
        }
    }

    public void setAlarm(SoundModel soundModel) {
        if (soundModel == null) return;
        String str = soundModel.getName() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str + ".mp3");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_alarm", true);
            contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
            Uri mParse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId());
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try (AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(mParse, "r");
                 OutputStream os = getContentResolver().openOutputStream(insert);
                 InputStream is = afd.createInputStream()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                RingtoneManager.setActualDefaultRingtoneUri(this, 4, insert);
                Toast.makeText(this, "Alarm Tone set: " + soundModel.getName(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
            }
        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
            File mfile = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds", str + ".mp3");
            if (mfile.getParentFile() != null && !mfile.getParentFile().exists()) {
                mfile.getParentFile().mkdirs();
            }
            try (InputStream is = getContentResolver().openAssetFileDescriptor(Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId()), "r").createInputStream();
                 FileOutputStream fos = new FileOutputStream(mfile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", soundModel.getName());
            contentValues2.put("mime_type", "audio/mp3");
            contentValues2.put("_size", mfile.length());
            contentValues2.put("artist", getString(R.string.app_name));
            contentValues2.put("is_alarm", true);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 4, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                Toast.makeText(this, "Alarm Tone set successfully: " + soundModel.getName(), Toast.LENGTH_SHORT).show();
            } catch (Throwable th) {
                Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void alarmMethod(SoundModel soundModel) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == 0 || notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                showDialogdialogdonotdistrub(this, getString(R.string.app_name) + " requires Do Not Disturb permission for setting alarms.", R.drawable.do_not_distrub, 0);
            } else if (Settings.System.canWrite(this)) {
                setAlarm(soundModel);
            } else {
                showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
            }
        } else if (Settings.System.canWrite(this)) {
            setAlarm(soundModel);
        } else {
            setAlarm(soundModel);
        }
    }

    public void showDialogPermissionStorage(Activity context, String message, int imageRes, int position) {
        Dialog dialogsettings = new Dialog(context);
        dialogsettings.requestWindowFeature(1);
        dialogsettings.setContentView(R.layout.permissions_screen_dialog);
        dialogsettings.setCancelable(true);
        if (dialogsettings.getWindow() != null) {
            dialogsettings.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        ((TextView) dialogsettings.findViewById(R.id.textdataa)).setText(message);
        ((ImageView) dialogsettings.findViewById(R.id.pic_to_change)).setImageResource(imageRes);
        ((ImageView) dialogsettings.findViewById(R.id.cancel_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogsettings.dismiss();
            }
        });
        ((CardView) dialogsettings.findViewById(R.id.allow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppOpenManager.isAppOpenAllowed = false;
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                dialogsettings.dismiss();
            }
        });
        dialogsettings.show();
        if (dialogsettings.getWindow() != null) {
            dialogsettings.getWindow().setLayout(-1, -1);
        }
    }

    public void showDialogdialogdonotdistrub(final Activity context, String message, int imageRes, int position) {
        final Dialog dialogdonotdistrub = new Dialog(context);
        dialogdonotdistrub.requestWindowFeature(1);
        if (dialogdonotdistrub.getWindow() != null) {
            dialogdonotdistrub.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialogdonotdistrub.getWindow().setLayout(-1, -1);
        }
        dialogdonotdistrub.setContentView(R.layout.permissions_screen_dialog);
        ((TextView) dialogdonotdistrub.findViewById(R.id.textdataa)).setText(message);
        ((ImageView) dialogdonotdistrub.findViewById(R.id.pic_to_change)).setImageResource(imageRes);
        ((ImageView) dialogdonotdistrub.findViewById(R.id.cancel_btn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogdonotdistrub.dismiss();
            }
        });
        ((CardView) dialogdonotdistrub.findViewById(R.id.allow)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppOpenManager.isAppOpenAllowed = false;
                context.startActivity(new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"));
                dialogdonotdistrub.dismiss();
            }
        });
        dialogdonotdistrub.setCancelable(true);
        dialogdonotdistrub.show();
    }
    private void showInterstitialWithMusicControl() {
        AdsController.showInterstitialAdWithLoader(this, new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                if (audioUrl != null) playSound(audioUrl);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                if (audioUrl != null) playSound(audioUrl);
            }
        });
    }

    /* access modifiers changed from: private */
    public void openTrimScreen() {
        if (this.audioUrl == null) {
            Toast.makeText(this, "No audio to trim", Toast.LENGTH_SHORT).show();
            return;
        }
        String soundFilePath = this.audioUrl;
        String soundName = this.binding.name.getText().toString();
        Intent intent = new Intent(this, TrimSoundScreen.class);
        intent.putExtra("soundFilePath", soundFilePath);
        intent.putExtra("soundName", soundName);
        startActivity(intent);
    }

    private void setupControls() {
        this.binding.playPause.setOnClickListener(v -> {
            togglePlayPause();
        });
    }

    private void togglePlayPause() {
        if (this.mediaPlayer != null) {
            if (this.isPlaying) {
                this.mediaPlayer.pause();
                this.isPlaying = false;
                this.binding.playPause.setImageResource(R.drawable.play);
                this.binding.lottie.pauseAnimation();
                this.handler.removeCallbacks(this.seekBarRunnable);
            } else {
                this.mediaPlayer.start();
                this.isPlaying = true;
                this.binding.playPause.setImageResource(R.drawable.pause);
                this.binding.lottie.playAnimation();
                updateSeekBar();
            }
        }
    }

    private void setupSaveButton(String prompt) {
        this.binding.save.setOnClickListener(onClickListener -> {
             if (audioUrl != null) {
                 List<SoundModel> history = HistoryStorage.loadHistory(this);
                 if (history == null) history = new ArrayList<>();
                 
                 // Check if already exists
                 boolean alreadyExists = false;
                 for (SoundModel model : history) {
                     if (model.getFilePath() != null && model.getFilePath().equals(audioUrl)) {
                         alreadyExists = true;
                         break;
                     }
                 }
                 
                 if (!alreadyExists) {
                     SoundModel newSound = new SoundModel(prompt, audioUrl);
                     newSound.setFavorite(false);
                     history.add(0, newSound);
                     HistoryStorage.saveHistory(this, history);
                     Toast.makeText(this, "Sound saved to history", Toast.LENGTH_SHORT).show();
                 } else {
                     Toast.makeText(this, "Already saved", Toast.LENGTH_SHORT).show();
                 }
             }
        });
    }

    private void setupSeekBarListener() {
        this.binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && PlayAiSound.this.mediaPlayer != null) {
                    PlayAiSound.this.mediaPlayer.seekTo(progress);
                    PlayAiSound.this.binding.runningduration.setText(PlayAiSound.this.formatDuration(progress));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSound(String audioUrl2) {
        try {
            if (this.mediaPlayer == null) {
                this.mediaPlayer = new MediaPlayer();
                this.mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            } else {
                this.mediaPlayer.reset();
            }
            this.mediaPlayer.setDataSource(audioUrl2);
            this.mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.setLooping(false);
                mp.start();

                isPlaying = true;
                binding.playPause.setImageResource(R.drawable.pause);
                binding.lottie.playAnimation();

                binding.seekbar.setMax(mp.getDuration());
                binding.totalduration.setText(formatDuration(mp.getDuration()));

                updateSeekBar();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                this.isPlaying = false;
                this.binding.playPause.setImageResource(R.drawable.play);
                this.binding.lottie.pauseAnimation();
                this.binding.seekbar.setProgress(0);
                this.binding.runningduration.setText("00:00");
                this.handler.removeCallbacks(this.seekBarRunnable);
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to play sound", Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: private */
    public void updateSeekBar() {
        if (this.mediaPlayer != null && this.isPlaying) {
            this.binding.seekbar.setProgress(this.mediaPlayer.getCurrentPosition());
            this.binding.runningduration.setText(formatDuration(this.mediaPlayer.getCurrentPosition()));
            this.seekBarRunnable = this::updateSeekBar;
            this.handler.postDelayed(this.seekBarRunnable, 500);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.isPlaying = false;
            this.binding.playPause.setImageResource(R.drawable.play);
            this.binding.lottie.pauseAnimation();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacksAndMessages((Object) null);
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    /* access modifiers changed from: private */
    public String formatDuration(int millis) {
        return String.format("%02d:%02d", (millis / 1000) / 60, (millis / 1000) % 60);
    }
}
