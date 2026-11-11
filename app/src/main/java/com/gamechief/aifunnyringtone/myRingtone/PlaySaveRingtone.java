package com.gamechief.aifunnyringtone.myRingtone;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.gamechief.aifunnyringtone.databinding.ActivityPlaySaveRingtoneBinding;
import com.gamechief.aifunnyringtone.databinding.PremiumDialogBinding;
import com.gamechief.aifunnyringtone.models.SavedRingtone;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaySaveRingtone extends ParentActivity {
    private static final String PREF_IS_RINGTONE_SET_FREE = "is_ringtone_save_free_v1";
    private static final String PREF_IS_NOTIFICATION_SET_FREE = "is_notification_save_free_v1";
    private static final String PREF_IS_ALARM_SET_FREE = "is_alarm_save_free_v1";

    /* access modifiers changed from: private */
    public ActivityPlaySaveRingtoneBinding binding;
    /* access modifiers changed from: private */
    public int elapsedMs = 0;
    /* access modifiers changed from: private */
    public int estimatedTotalMs = 1;
    /* access modifiers changed from: private */
    public Handler handler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public boolean isPlaying = false;
    /* access modifiers changed from: private */
    public final Runnable progressRunnable = new Runnable() {
        public void run() {
            if (PlaySaveRingtone.this.isPlaying) {
                PlaySaveRingtone playSaveRingtone = PlaySaveRingtone.this;
                playSaveRingtone.elapsedMs = playSaveRingtone.elapsedMs + 100;
                int seconds = PlaySaveRingtone.this.elapsedMs / 1000;
                PlaySaveRingtone.this.binding.count.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(seconds / 60), Integer.valueOf(seconds % 60)}));
                PlaySaveRingtone.this.binding.circularProgress.setProgress(Math.min((int) ((((float) PlaySaveRingtone.this.elapsedMs) * 100.0f) / ((float) PlaySaveRingtone.this.estimatedTotalMs)), 100));
                PlaySaveRingtone.this.handler.postDelayed(this, 100);
            }
        }
    };
    private TextToSpeech tts;
    private DataASSavingPrefs dataPrefs;

    private BillingClient billingClient;
    private ProductDetails currentProductDetails;
    private ProductDetails.SubscriptionOfferDetails targetOfferDetails;
    private ProductDetails.SubscriptionOfferDetails baseOfferDetails;
    private Dialog premiumDialog;
    private PremiumDialogBinding dialogBinding;
    private RewardedAd rewardedAd;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityPlaySaveRingtoneBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.dataPrefs = new DataASSavingPrefs();
        this.binding.imageView7.setOnClickListener(v -> onBackPressed());
        String text = getIntent().getStringExtra("ringtone");
        this.binding.getPromptTxt.setText(text);
        initTTS();
        final SavedRingtone ttsRingtone = new SavedRingtone(text);

        this.binding.setAlarm.setOnClickListener(view -> checkLimitAndPerform(PREF_IS_ALARM_SET_FREE, "Alarm", () -> alarmMethod(ttsRingtone)));
        this.binding.setRintone.setOnClickListener(view -> checkLimitAndPerform(PREF_IS_RINGTONE_SET_FREE, "Ringtone", () -> ringtoneMethod(ttsRingtone)));
        this.binding.setNotify.setOnClickListener(view -> checkLimitAndPerform(PREF_IS_NOTIFICATION_SET_FREE, "Notification", () -> notificationMethod(ttsRingtone)));

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

                rewardedAd.show(PlaySaveRingtone.this, rewardItem -> {
                    action.run();
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                RewardedAd.load(PlaySaveRingtone.this, getString(R.string.rewardAd_medium), adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        progressDialog.dismiss();
                        rewardedAd = ad;
                        rewardedAd.show(PlaySaveRingtone.this, rewardItem -> action.run());
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        progressDialog.dismiss();
                        Toast.makeText(PlaySaveRingtone.this, "Failed to load ad. Please try again later.", Toast.LENGTH_SHORT).show();
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


    private void initTTS() {
        this.tts = new TextToSpeech(this, v -> {
            if (v == 0) {
                this.tts.setLanguage(Locale.US);
                this.tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    public void onStart(String utteranceId) {
                        PlaySaveRingtone.this.runOnUiThread(() -> {
                            PlaySaveRingtone.this.isPlaying = true;
                            PlaySaveRingtone.this.elapsedMs = 0;
                            PlaySaveRingtone.this.binding.circularProgress.setProgress(0);
                            PlaySaveRingtone.this.binding.count.setText("0:00");
                            PlaySaveRingtone.this.handler.post(PlaySaveRingtone.this.progressRunnable);
                        });
                    }

                    public void onDone(String utteranceId) {
                        PlaySaveRingtone.this.runOnUiThread(() ->{
                            PlaySaveRingtone.this.isPlaying = false;
                            PlaySaveRingtone.this.handler.removeCallbacks(PlaySaveRingtone.this.progressRunnable);
                            PlaySaveRingtone.this.binding.circularProgress.setProgress(100);
                            int totalSeconds = PlaySaveRingtone.this.estimatedTotalMs / 1000;
                            PlaySaveRingtone.this.binding.count.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(totalSeconds / 60),
                                    Integer.valueOf(totalSeconds % 60)}));
                        });
                    }

                    public void onError(String utteranceId) {
                        PlaySaveRingtone.this.runOnUiThread(() ->{
                            PlaySaveRingtone.this.isPlaying = false;
                            PlaySaveRingtone.this.handler.removeCallbacks(PlaySaveRingtone.this.progressRunnable);
                        });
                    }
                });
                playRingtone();
            }
        });
    }

    private void playRingtone() {
        String text = getIntent().getStringExtra("ringtone");
        if (text != null && !text.isEmpty()) {
            this.estimatedTotalMs = Math.max(1000, text.trim().split("\\s+").length * 450);
            Bundle params = new Bundle();
            params.putString("utteranceId", "ringtone_id");
            this.tts.speak(text, 0, params, "ringtone_id");
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacks(this.progressRunnable);
        if (billingClient != null) {
            billingClient.endConnection();
        }
        if (this.tts != null) {
            this.tts.stop();
            this.tts.shutdown();
        }
    }

    public void setRingtone(SavedRingtone sound) {
        int read;
        String str = sound.getText() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str + ".mp3");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_ringtone", true);
            contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyRingtones");
            Uri mParse = generateTTSFile(sound);
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                AssetFileDescriptor assetFileDescriptor2 = getContentResolver().openAssetFileDescriptor(mParse, "r");
                OutputStream openASOutputStream = getContentResolver().openOutputStream(insert);
                InputStream createInputStream = assetFileDescriptor2.createInputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int read2 = createInputStream.read(buffer);
                    if (read2 == -1) break;
                    openASOutputStream.write(buffer, 0, read2);
                }
                openASOutputStream.flush();
                openASOutputStream.close();
            } catch (Exception e) {
                Log.v("ContentValues", "Exception: " + e.getMessage());
            }
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 1, insert);
                Toast.makeText(this, "Ringtone set : " + sound.getText(), Toast.LENGTH_LONG).show();
            } catch (Exception e2) {
                showDialogPermissionStorage(this, "Please allow modify system setting !!", R.drawable.write_settings_icon, 0);
            }
        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
            File mfile = new File(Environment.getExternalStorageDirectory() + "/MyRingtones", str + ".mp3");
            if (!mfile.getParentFile().exists()) {
                mfile.getParentFile().mkdirs();
            }
            if (!mfile.exists()) {
                try {
                    mfile.createNewFile();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            try {
                InputStream createInputStream2 = getContentResolver().openAssetFileDescriptor(generateTTSFile(sound), "r").createInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(mfile);
                byte[] buffer2 = new byte[1024];
                while (true) {
                    int read4 = createInputStream2.read(buffer2);
                    if (read4 == -1) break;
                    fileOutputStream.write(buffer2, 0, read4);
                }
                fileOutputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", sound.getText());
            contentValues2.put("mime_type", "audio/mp3");
            contentValues2.put("_size", Long.valueOf(mfile.length()));
            contentValues2.put("artist", getString(R.string.app_name));
            contentValues2.put("is_ringtone", true);
            contentValues2.put("is_notification", false);
            contentValues2.put("is_alarm", false);
            contentValues2.put("is_music", false);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 1, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                Toast.makeText(this, "Ringtone set successfully!", Toast.LENGTH_LONG).show();
            } catch (Exception e5) {
                Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void ringtoneMethod(SavedRingtone sound) {
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

    public void setNotification(SavedRingtone soundModel) {
        String str = soundModel.getText() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str + ".mp3");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_notification", true);
            contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
            Uri mParse = generateTTSFile(soundModel);
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                AssetFileDescriptor assetFileDescriptor2 = getContentResolver().openAssetFileDescriptor(mParse, "r");
                OutputStream outputStream = getContentResolver().openOutputStream(insert);
                InputStream inputStream = assetFileDescriptor2.createInputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = inputStream.read(buffer);
                    if (read == -1) break;
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
            } catch (Exception e2) {
                Log.v("ContentValues", "Exception: " + e2.getMessage());
            }
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 2, insert);
                Toast.makeText(this, "Notification set: " + soundModel.getText(), Toast.LENGTH_LONG).show();
            } catch (Exception e3) {
                showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
            }
        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
            File mfile = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds", str + ".mp3");
            if (!mfile.getParentFile().exists()) mfile.getParentFile().mkdirs();
            if (!mfile.exists()) {
                try {
                    mfile.createNewFile();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            try {
                InputStream inputStream2 = getContentResolver().openAssetFileDescriptor(generateTTSFile(soundModel), "r").createInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(mfile);
                byte[] buffer2 = new byte[1024];
                while (true) {
                    int read4 = inputStream2.read(buffer2);
                    if (read4 == -1) break;
                    fileOutputStream.write(buffer2, 0, read4);
                }
                fileOutputStream.close();
            } catch (IOException e5) {
                e5.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", soundModel.getText());
            contentValues2.put("mime_type", "audio/mp3");
            contentValues2.put("_size", Long.valueOf(mfile.length()));
            contentValues2.put("artist", getString(R.string.app_name));
            contentValues2.put("is_ringtone", false);
            contentValues2.put("is_notification", true);
            contentValues2.put("is_alarm", false);
            contentValues2.put("is_music", false);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 2, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                Toast.makeText(this, "Notification set successfully: " + soundModel.getText(), Toast.LENGTH_LONG).show();
            } catch (Throwable th) {
                Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void notificationMethod(SavedRingtone soundModel) {
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

    public void setAlarm(SavedRingtone soundModel) {
        String str = soundModel.getText() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str + ".mp3");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_ringtone", true);
            contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
            Uri mParse = generateTTSFile(soundModel);
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                AssetFileDescriptor assetFileDescriptor2 = getContentResolver().openAssetFileDescriptor(mParse, "r");
                OutputStream outputStream = getContentResolver().openOutputStream(insert);
                InputStream inputStream = assetFileDescriptor2.createInputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = inputStream.read(buffer);
                    if (read == -1) break;
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
            } catch (Exception e2) {
                Log.v("ContentValues", "Exception: " + e2.getMessage());
            }
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 4, insert);
                Toast.makeText(this, "Alarm Tone set: " + soundModel.getText(), Toast.LENGTH_LONG).show();
            } catch (Exception e3) {
                showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
            }
        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
            File mfile = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds", str + ".mp3");
            if (!mfile.getParentFile().exists()) mfile.getParentFile().mkdirs();
            if (!mfile.exists()) {
                try {
                    mfile.createNewFile();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            try {
                InputStream inputStream2 = getContentResolver().openAssetFileDescriptor(generateTTSFile(soundModel), "r").createInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(mfile);
                byte[] buffer2 = new byte[1024];
                while (true) {
                    int read4 = inputStream2.read(buffer2);
                    if (read4 == -1) break;
                    fileOutputStream.write(buffer2, 0, read4);
                }
                fileOutputStream.close();
            } catch (IOException e5) {
                e5.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", soundModel.getText());
            contentValues2.put("mime_type", "audio/mp3");
            contentValues2.put("_size", Long.valueOf(mfile.length()));
            contentValues2.put("artist", getString(R.string.app_name));
            contentValues2.put("is_ringtone", false);
            contentValues2.put("is_notification", false);
            contentValues2.put("is_alarm", true);
            contentValues2.put("is_music", false);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 4, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                Toast.makeText(this, "Alarm Tone set successfully: " + soundModel.getText(), Toast.LENGTH_LONG).show();
            } catch (Throwable th) {
                Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void alarmMethod(SavedRingtone soundModel) {
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

    private Uri generateTTSFile(SavedRingtone sound) {
        if (this.tts == null) {
            return null;
        }
        String fileName = sound.getText() + "_" + System.currentTimeMillis() + ".mp3";
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", fileName);
                contentValues.put("mime_type", "audio/mp3");
                contentValues.put("is_ringtone", true);
                contentValues.put("is_notification", true);
                contentValues.put("is_alarm", true);
                contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
                Uri fileUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (Build.VERSION.SDK_INT >= 30) {
                    this.tts.synthesizeToFile(sound.getText(), (Bundle) null, getContentResolver().openFileDescriptor(fileUri, "w"), sound.getText() + "_id");
                }
                return fileUri;
            }
            File dir = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            this.tts.synthesizeToFile(sound.getText(), (Bundle) null, file, sound.getText() + "_id");
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
        ((ImageView) dialogsettings.findViewById(R.id.cancel_btn)).setOnClickListener(v -> dialogsettings.dismiss());
        ((CardView) dialogsettings.findViewById(R.id.allow)).setOnClickListener(v -> {
            AppOpenManager.isAppOpenAllowed = false;
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            dialogsettings.dismiss();
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
        ((ImageView) dialogdonotdistrub.findViewById(R.id.cancel_btn)).setOnClickListener(v -> dialogdonotdistrub.dismiss());
        ((CardView) dialogdonotdistrub.findViewById(R.id.allow)).setOnClickListener(v -> {
            AppOpenManager.isAppOpenAllowed = false;
            context.startActivity(new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"));
            dialogdonotdistrub.dismiss();
        });
        dialogdonotdistrub.setCancelable(true);
        dialogdonotdistrub.show();
    }
}
