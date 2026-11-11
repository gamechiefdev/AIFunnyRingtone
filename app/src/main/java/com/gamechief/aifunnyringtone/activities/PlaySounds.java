package com.gamechief.aifunnyringtone.activities;

import android.annotation.SuppressLint;
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
import com.gamechief.aifunnyringtone.databinding.ActivityPlaySoundsBinding;
import com.gamechief.aifunnyringtone.databinding.PremiumDialogBinding;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.lingala.zip4j.util.InternalZipConstants;

public class PlaySounds extends ParentActivity {
    private static int clickCount = 0;
    private static final String PREF_IS_RINGTONE_SET_FREE = "is_ringtone_set_free_v1";
    private static final String PREF_IS_NOTIFICATION_SET_FREE = "is_notification_set_free_v1";
    private static final String PREF_IS_ALARM_SET_FREE = "is_alarm_set_free_v1";

    /* access modifiers changed from: private */
    public AudioManager audioManager;
    private String audioUrl = null;
    /* access modifiers changed from: private */
    public ActivityPlaySoundsBinding binding;
    private int currentPosition = 0;
    /* access modifiers changed from: private */
    public Handler handler = new Handler();
    private boolean isLooping = false;
    private boolean isStreaming = false;
    /* access modifiers changed from: private */
    public MediaPlayer mediaPlayer;
    /* access modifiers changed from: private */
    public Runnable seekBarRunnable;
    private List<SoundModel> soundList;
    /* access modifiers changed from: private */
    public boolean wasPlayingBeforeAd = false;
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
        this.binding = ActivityPlaySoundsBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        this.dataPrefs = new DataASSavingPrefs();
        Log.d("PlaySounds", "Click count before ad check: " + clickCount);
        clickCount++;
        Log.d("PlaySounds", "Click count after increment: " + clickCount);
        if (clickCount % 5 == 0) {
            this.wasPlayingBeforeAd = this.mediaPlayer != null && this.mediaPlayer.isPlaying();
            Log.d("PlaySounds", "5th click! wasPlayingBeforeAd: " + this.wasPlayingBeforeAd);
            if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
                Log.d("PlaySounds", "Pausing mediaPlayer before showing ad");
                this.mediaPlayer.pause();
                this.binding.playerplaybtn.setImageResource(R.drawable.play);
                this.binding.premiBtn.pauseAnimation();
            }
            Log.d("PlaySounds", "Showing interstitial ad");
            showInterstitialWithMusicControl();
        }
        this.binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        this.soundList = getIntent().getParcelableArrayListExtra("soundList");
        this.audioUrl = getIntent().getStringExtra("soundUrl");
        Log.d("PlaySounds", "soundList size: " + (this.soundList != null ? Integer.valueOf(this.soundList.size()) : null) + ", audioUrl: " + this.audioUrl);
        if (this.soundList != null && !this.soundList.isEmpty()) {
            this.currentPosition = getIntent().getIntExtra("position", 0);
            Log.d("PlaySounds", "Scheduling local music start after 1 sec at position: " + this.currentPosition);
            updateUI();
            new Handler().postDelayed(() -> startMusicLocal(this.soundList.get(this.currentPosition)),1200);
        } else if (this.audioUrl == null || this.audioUrl.isEmpty()) {
            Log.d("PlaySounds", "No sound data found, finishing activity");
            Toast.makeText(this, "No sound data found!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            this.isStreaming = true;
            Log.d("PlaySounds", "Scheduling streaming music start after 1 sec from URL: " + this.audioUrl);
            new Handler().postDelayed(() -> startMusicFromUrl(audioUrl), 1200);
        }
        this.binding.playerplaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        this.binding.previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStreaming) {
                    playPrevious();
                } else {
                    Toast.makeText(PlaySounds.this, "Not available for AI sounds", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStreaming) {
                    playNext();
                } else {
                    Toast.makeText(PlaySounds.this, "Not available for AI sounds", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.binding.loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLoop();
            }
        });
        this.binding.trimSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrimScreen();
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
                    if (!isStreaming && soundList != null && !soundList.isEmpty()) {
                        SoundModel currentSound = soundList.get(currentPosition);
                        if (item.getItemId() == R.id.set_ringtone) {
                            ringtoneMethod(currentSound);
                        } else if (item.getItemId() == R.id.set_notification_sound) {
                            notificationMethod(currentSound);
                        } else if (item.getItemId() == R.id.set_alarm_sound) {
                            alarmMethod(currentSound);
                        }
                    } else if (isStreaming && audioUrl != null && !audioUrl.isEmpty()) {
                        if (item.getItemId() == R.id.set_ringtone) {
                            ringtoneMethodFromUrl(audioUrl);
                        } else if (item.getItemId() == R.id.set_notification_sound) {
                            notificationMethodFromUrl(audioUrl);
                        } else if (item.getItemId() == R.id.set_alarm_sound) {
                            alarmMethodFromUrl(audioUrl);
                        }
                    } else {
                        Toast.makeText(PlaySounds.this, "Audio not ready", Toast.LENGTH_SHORT).show();
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

                rewardedAd.show(PlaySounds.this, rewardItem -> {
                    action.run();
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                RewardedAd.load(PlaySounds.this, getString(R.string.rewardAd_medium), adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        progressDialog.dismiss();
                        rewardedAd = ad;
                        rewardedAd.show(PlaySounds.this, rewardItem -> action.run());
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        progressDialog.dismiss();
                        Toast.makeText(PlaySounds.this, "Failed to load ad. Please try again later.", Toast.LENGTH_SHORT).show();
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
            double discountedMicros = 0;
            double originalMicros = 0;
            if (targetOfferDetails != null) {
                for (ProductDetails.PricingPhase phase : targetOfferDetails.getPricingPhases().getPricingPhaseList()) {
                    if (phase.getPriceAmountMicros() > 0) {
                        discountedPriceStr = phase.getFormattedPrice();
                        discountedMicros = phase.getPriceAmountMicros();
                        break;
                    }
                }
            }
            if (baseOfferDetails != null) {
                for (ProductDetails.PricingPhase phase : baseOfferDetails.getPricingPhases().getPricingPhaseList()) {
                    if (phase.getPriceAmountMicros() > 0) {
                        originalMicros = phase.getPriceAmountMicros();
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

    private void openTrimScreen() {
        Intent intent = new Intent(this, TrimSoundScreen.class);
        String soundFilePath = null;
        String soundName = null;
        if (!this.isStreaming && this.soundList != null && !this.soundList.isEmpty()) {
            SoundModel currentSound = this.soundList.get(this.currentPosition);
            soundName = currentSound.getName();
            soundFilePath = currentSound.getFilePath();
            if (soundFilePath == null || soundFilePath.isEmpty()) {
                soundFilePath = copyRawToTempFile(currentSound.getSoundResId(), soundName);
            }
        } else if (this.isStreaming && this.audioUrl != null && !this.audioUrl.isEmpty()) {
            soundFilePath = this.audioUrl;
            soundName = "StreamingSound";
        }
        if (soundFilePath == null) {
            Toast.makeText(this, "No sound available to trim", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("soundFilePath", soundFilePath);
        intent.putExtra("soundName", soundName);
        startActivity(intent);
    }

    private String copyRawToTempFile(int resId, String fileName) {
        try {
            File tempFile = new File(getCacheDir(), fileName + "_" + System.currentTimeMillis() + ".mp3");
            if (!tempFile.exists()) {
                InputStream inputStream = getResources().openRawResource(resId);
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = inputStream.read(buffer);
                    int read2 = read;
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, read2);
                }
                outputStream.close();
                inputStream.close();
            }
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void ringtoneMethodFromUrl(String audioUrl2) {
        if (audioUrl2 != null && !audioUrl2.isEmpty()) {
            File audioFile = new File(audioUrl2);
            if (!audioFile.exists()) {
                Toast.makeText(this, "Audio not found!", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", audioFile.getAbsolutePath());
            contentValues.put("title", "AI_Ringtone");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_ringtone", true);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 1, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(audioFile.getAbsolutePath()), contentValues));
                Toast.makeText(this, "Ringtone set successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to set ringtone", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void notificationMethodFromUrl(String audioUrl2) {
        if (audioUrl2 != null && !audioUrl2.isEmpty()) {
            File audioFile = new File(audioUrl2);
            if (!audioFile.exists()) {
                Toast.makeText(this, "Audio not found!", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", audioFile.getAbsolutePath());
            contentValues.put("title", "AI_Notification");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_notification", true);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 2, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(audioFile.getAbsolutePath()), contentValues));
                Toast.makeText(this, "Notification set successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to set notification", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void alarmMethodFromUrl(String audioUrl2) {
        if (audioUrl2 != null && !audioUrl2.isEmpty()) {
            File audioFile = new File(audioUrl2);
            if (!audioFile.exists()) {
                Toast.makeText(this, "Audio not found!", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", audioFile.getAbsolutePath());
            contentValues.put("title", "AI_Alarm");
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("is_alarm", true);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 4, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(audioFile.getAbsolutePath()), contentValues));
                Toast.makeText(this, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to set alarm", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playNext() {
        stopMusic();
        this.currentPosition++;
        if (this.currentPosition >= this.soundList.size()) {
            this.currentPosition = 0;
        }
        updateUI();
        startMusicLocal(this.soundList.get(this.currentPosition));
    }

    private void playPrevious() {
        stopMusic();
        this.currentPosition--;
        if (this.currentPosition < 0) {
            this.currentPosition = this.soundList.size() - 1;
        }
        updateUI();
        startMusicLocal(this.soundList.get(this.currentPosition));
    }

    private void updateUI() {
        if (this.soundList != null && !this.soundList.isEmpty()) {
            SoundModel model = this.soundList.get(this.currentPosition);
            this.binding.name.setText(model.getName());
            this.binding.roundedImageView.setImageResource(model.getImageResId());
        }
    }

    private void startMusicLocal(SoundModel sound) {
        stopMusic();
        try {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            if (sound.getFilePath() != null && !sound.getFilePath().isEmpty()) {
                this.mediaPlayer.setDataSource(sound.getFilePath());
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();
            } else if (sound.getSoundResId() > 0) {
                AssetFileDescriptor afd = getResources().openRawResourceFd(sound.getSoundResId());
                this.mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();
            } else {
                Toast.makeText(this, "No audio available!", Toast.LENGTH_SHORT).show();
                return;
            }
            this.binding.playerplaybtn.setImageResource(R.drawable.pause);
            this.binding.totalduration.setText(formatTime(this.mediaPlayer.getDuration()));
            animateVisualizer();
            setupSeekBar();
            this.mediaPlayer.setLooping(this.isLooping);
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (isLooping) {
                        mp.start();
                    } else if (!isStreaming && currentPosition < soundList.size() - 1) {
                        playNext();
                    } else {
                        binding.playerplaybtn.setImageResource(R.drawable.play);
                        binding.premiBtn.pauseAnimation();
                        binding.premiBtn.animate().alpha(0.3f).setDuration(300).start();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }


    private void startMusicFromUrl(String pathOrUrl) {
        stopMusic();
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        try {
            if (pathOrUrl.startsWith(InternalZipConstants.ZIP_FILE_SEPARATOR)) {
                this.mediaPlayer.setDataSource(pathOrUrl);
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();
                this.binding.playerplaybtn.setImageResource(R.drawable.pause);
                this.binding.totalduration.setText(formatTime(this.mediaPlayer.getDuration()));
                animateVisualizer();
                setupSeekBar();
            } else {
                this.mediaPlayer.setDataSource(pathOrUrl);
                this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        binding.playerplaybtn.setImageResource(R.drawable.pause);
                        binding.totalduration.setText(formatTime(mp.getDuration()));
                        animateVisualizer();
                        setupSeekBar();
                    }
                });
                this.mediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing sound", Toast.LENGTH_SHORT).show();
        }
        this.mediaPlayer.setLooping(this.isLooping);
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isLooping) {
                    mp.start();
                } else {
                    binding.playerplaybtn.setImageResource(R.drawable.play);
                    binding.premiBtn.pauseAnimation();
                    binding.premiBtn.animate().alpha(0.3f).setDuration(300).start();
                }
            }
        });
    }





    private void togglePlayPause() {
        if (this.mediaPlayer != null) {
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.pause();
                this.binding.playerplaybtn.setImageResource(R.drawable.play);
                this.binding.premiBtn.pauseAnimation();
                return;
            }
            this.mediaPlayer.start();
            this.binding.playerplaybtn.setImageResource(R.drawable.pause);
            this.binding.premiBtn.resumeAnimation();
            
            // Restart seekbar update loop if it stopped
            if (this.seekBarRunnable != null) {
                this.handler.post(this.seekBarRunnable);
            } else {
                setupSeekBar();
            }
        }
    }

    private void toggleLoop() {
        this.isLooping = !this.isLooping;
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setLooping(this.isLooping);
        }
        this.binding.loop.setAlpha(this.isLooping ? 1.0f : 0.4f);
        Toast.makeText(this, this.isLooping ? "Loop On" : "Loop Off", Toast.LENGTH_SHORT).show();
    }

    private void toggleVolumeSeekbar() {
        if (this.binding.seekbarVolume.getVisibility() == View.VISIBLE) {
            this.binding.seekbarVolume.setVisibility(View.INVISIBLE);
            return;
        }
        setupVolumeControl();
        this.binding.seekbarVolume.setVisibility(View.VISIBLE);
    }

    private void setupVolumeControl() {
        this.binding.seekbarVolume.setMax(this.audioManager.getStreamMaxVolume(3));
        this.binding.seekbarVolume.setProgress(this.audioManager.getStreamVolume(3));
        this.binding.seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    PlaySounds.this.audioManager.setStreamVolume(3, progress, 0);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /* access modifiers changed from: private */
    public void setupSeekBar() {
        if (this.mediaPlayer != null) {
            // Remove existing callbacks to avoid multiple update loops
            this.handler.removeCallbacks(this.seekBarRunnable);
            
            this.binding.seekbar.setMax(this.mediaPlayer.getDuration());
            this.seekBarRunnable = new Runnable() {
                public void run() {
                    if (PlaySounds.this.mediaPlayer != null && PlaySounds.this.mediaPlayer.isPlaying()) {
                        PlaySounds.this.binding.seekbar.setProgress(PlaySounds.this.mediaPlayer.getCurrentPosition());
                        PlaySounds.this.binding.runningduration.setText(PlaySounds.this.formatTime(PlaySounds.this.mediaPlayer.getCurrentPosition()));
                        // Use 100ms for smooth movement
                        PlaySounds.this.handler.postDelayed(this, 100);
                    }
                }
            };
            this.handler.postDelayed(this.seekBarRunnable, 100);
            this.binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && PlaySounds.this.mediaPlayer != null) {
                        PlaySounds.this.mediaPlayer.seekTo(progress);
                        PlaySounds.this.binding.runningduration.setText(PlaySounds.this.formatTime(progress));
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private void animateVisualizer() {
        this.binding.premiBtn.setAlpha(1.0f);
        this.binding.premiBtn.playAnimation();
    }

    /* access modifiers changed from: private */
    public String formatTime(int ms) {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) ms)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) ms) % 60)});
    }

    private void stopMusic() {
        if (this.seekBarRunnable != null) {
            this.handler.removeCallbacks(this.seekBarRunnable);
            this.seekBarRunnable = null;
        }
        if (this.mediaPlayer != null) {
            try {
                this.mediaPlayer.stop();
            } catch (Exception e) {
            }
            try {
                this.mediaPlayer.reset();
                this.mediaPlayer.release();
            } catch (Exception e2) {
            }
            this.mediaPlayer = null;
        }
        this.binding.playerplaybtn.setImageResource(R.drawable.play);
        this.binding.premiBtn.cancelAnimation();
        this.binding.premiBtn.setAlpha(0.3f);
        this.binding.seekbar.setProgress(0);
        this.binding.runningduration.setText("00:00");
    }

    private void pauseMusicSafely() {
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.binding.playerplaybtn.setImageResource(R.drawable.play);
            if (this.seekBarRunnable != null) {
                this.handler.removeCallbacks(this.seekBarRunnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void resumeMusicSafely() {
        if (this.mediaPlayer != null && !this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.start();
            this.binding.playerplaybtn.setImageResource(R.drawable.pause);
            this.binding.premiBtn.resumeAnimation();
            if (this.seekBarRunnable != null) {
                this.handler.postDelayed(this.seekBarRunnable, 100);
            } else {
                setupSeekBar();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        pauseMusicSafely();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.seekBarRunnable != null) {
            this.handler.removeCallbacks(this.seekBarRunnable);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint("GestureBackNavigation")
    public void onDestroy() {
        stopMusic();
        if (billingClient != null) {
            billingClient.endConnection();
        }
        super.onDestroy();
    }

    @SuppressLint("GestureBackNavigation")
    public void onBackPressed() {
        stopMusic();
        super.onBackPressed();
    }

    private void showInterstitialWithMusicControl() {
        if (!AdsController.isInAppPurchased) {
            if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.setVolume(0.0f, 0.0f);
                this.mediaPlayer.pause();
            }
            if (this.seekBarRunnable != null) {
                this.handler.removeCallbacks(this.seekBarRunnable);
            }
            AdsController.showInterstitialAdWithLoader(this, new FullScreenContentCallback() {
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    if (PlaySounds.this.mediaPlayer != null) {
                        PlaySounds.this.mediaPlayer.setVolume(1.0f, 1.0f);
                        PlaySounds.this.mediaPlayer.start();
                        PlaySounds.this.binding.playerplaybtn.setImageResource(R.drawable.pause);
                        PlaySounds.this.binding.premiBtn.resumeAnimation();
                        if (PlaySounds.this.seekBarRunnable != null) {
                            PlaySounds.this.handler.postDelayed(PlaySounds.this.seekBarRunnable, 100);
                        } else {
                            PlaySounds.this.setupSeekBar();
                        }
                        if (PlaySounds.this.wasPlayingBeforeAd) {
                            PlaySounds.this.resumeMusicSafely();
                        }
                    }
                }

                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    if (PlaySounds.this.mediaPlayer != null) {
                        PlaySounds.this.mediaPlayer.setVolume(1.0f, 1.0f);
                        if (PlaySounds.this.wasPlayingBeforeAd) {
                            PlaySounds.this.resumeMusicSafely();
                        }
                    }
                }
            });
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
            new File(mParse.getPath());
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            Log.v("ContentValues", "URI" + insert);
            try {
                AssetFileDescriptor assetFileDescriptor2 = getContentResolver().openAssetFileDescriptor(mParse, "r");
                OutputStream openASOutputStream = getContentResolver().openOutputStream(insert);
                InputStream createInputStream = assetFileDescriptor2.createInputStream();
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = createInputStream.read(buffer);
                    int read2 = read;
                    if (read == -1) {
                        break;
                    }
                    openASOutputStream.write(buffer, 0, read2);
                }
                openASOutputStream.flush();
                openASOutputStream.close();
            } catch (Exception e) {
                Log.v("ContentValues", "Exception: " + e.getMessage());
            }
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, 1, insert);
                Toast.makeText(this, "Ringtone set : " + sound.getName(), Toast.LENGTH_LONG).show();
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
                InputStream createInputStream2 = getContentResolver().openAssetFileDescriptor(Uri.parse("android.resource://" + getPackageName() + "/raw/" + sound.getSoundResId()), "r").createInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(mfile);
                byte[] buffer2 = new byte[1024];
                while (true) {
                    int read3 = createInputStream2.read(buffer2);
                    int read4 = read3;
                    if (read3 == -1) {
                        break;
                    }
                    fileOutputStream.write(buffer2, 0, read4);
                }
                fileOutputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("_data", mfile.getAbsolutePath());
            contentValues2.put("title", sound.getName());
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
        AssetFileDescriptor assetFileDescriptor2;
        if (soundModel != null) {
            String str = soundModel.getName() + "_" + System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str + ".mp3");
                contentValues.put("mime_type", "audio/mp3");
                contentValues.put("is_notification", true);
                contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
                Uri mParse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId());
                Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                Log.v("ContentValues", "URI" + insert);
                try {
                    assetFileDescriptor2 = getContentResolver().openAssetFileDescriptor(mParse, "r");
                } catch (FileNotFoundException e) {
                    Log.v("ContentValues", "open Asset File Descriptor " + e.getMessage());
                    assetFileDescriptor2 = null;
                }
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(insert);
                    InputStream inputStream = assetFileDescriptor2.createInputStream();
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int read = inputStream.read(buffer);
                        int read2 = read;
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(buffer, 0, read2);
                    }
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e2) {
                    Log.v("ContentValues", "Exception: " + e2.getMessage());
                }
                try {
                    RingtoneManager.setActualDefaultRingtoneUri(this, 2, insert);
                    Toast.makeText(this, "Notification set: " + soundModel.getName(), Toast.LENGTH_LONG).show();
                } catch (Exception e3) {
                    showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
                }
            } else {
                ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
                File mfile = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds", str + ".mp3");
                if (!mfile.getParentFile().exists()) {
                    mfile.getParentFile().mkdirs();
                }
                if (!mfile.exists()) {
                    try {
                        mfile.createNewFile();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                try {
                    InputStream inputStream2 = getContentResolver().openAssetFileDescriptor(Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId()), "r").createInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(mfile);
                    byte[] buffer2 = new byte[1024];
                    while (true) {
                        int read3 = inputStream2.read(buffer2);
                        int read4 = read3;
                        if (read3 == -1) {
                            break;
                        }
                        fileOutputStream.write(buffer2, 0, read4);
                    }
                    fileOutputStream.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("_data", mfile.getAbsolutePath());
                contentValues2.put("title", soundModel.getName());
                contentValues2.put("mime_type", "audio/mp3");
                contentValues2.put("_size", Long.valueOf(mfile.length()));
                contentValues2.put("artist", getString(R.string.app_name));
                contentValues2.put("is_ringtone", false);
                contentValues2.put("is_notification", true);
                contentValues2.put("is_alarm", false);
                contentValues2.put("is_music", false);
                try {
                    RingtoneManager.setActualDefaultRingtoneUri(this, 2, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                    Toast.makeText(this, "Notification set successfully: " + soundModel.getName(), Toast.LENGTH_LONG).show();
                } catch (Throwable th) {
                    Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
                }
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
        AssetFileDescriptor assetFileDescriptor2;
        if (soundModel != null) {
            String str = soundModel.getName() + "_" + System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str + ".mp3");
                contentValues.put("mime_type", "audio/mp3");
                contentValues.put("is_ringtone", true);
                contentValues.put("relative_path", Environment.DIRECTORY_RINGTONES + "/MyAppSounds");
                Uri mParse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId());
                Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                Log.v("ContentValues", "URI" + insert);
                try {
                    assetFileDescriptor2 = getContentResolver().openAssetFileDescriptor(mParse, "r");
                } catch (FileNotFoundException e) {
                    Log.v("ContentValues", "open Asset File Descriptor " + e.getMessage());
                    assetFileDescriptor2 = null;
                }
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(insert);
                    InputStream inputStream = assetFileDescriptor2.createInputStream();
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int read = inputStream.read(buffer);
                        int read2 = read;
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(buffer, 0, read2);
                    }
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e2) {
                    Log.v("ContentValues", "Exception: " + e2.getMessage());
                }
                try {
                    RingtoneManager.setActualDefaultRingtoneUri(this, 4, insert);
                    Toast.makeText(this, "Alarm Tone set: " + soundModel.getName(), Toast.LENGTH_LONG).show();
                } catch (Exception e3) {
                    showDialogPermissionStorage(this, "Please allow modify system settings !!", R.drawable.write_settings_icon, 0);
                }
            } else {
                ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(2);
                File mfile = new File(Environment.getExternalStorageDirectory() + "/MyAppSounds", str + ".mp3");
                if (!mfile.getParentFile().exists()) {
                    mfile.getParentFile().mkdirs();
                }
                if (!mfile.exists()) {
                    try {
                        mfile.createNewFile();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                try {
                    InputStream inputStream2 = getContentResolver().openAssetFileDescriptor(Uri.parse("android.resource://" + getPackageName() + "/raw/" + soundModel.getSoundResId()), "r").createInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(mfile);
                    byte[] buffer2 = new byte[1024];
                    while (true) {
                        int read3 = inputStream2.read(buffer2);
                        int read4 = read3;
                        if (read3 == -1) {
                            break;
                        }
                        fileOutputStream.write(buffer2, 0, read4);
                    }
                    fileOutputStream.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("_data", mfile.getAbsolutePath());
                contentValues2.put("title", soundModel.getName());
                contentValues2.put("mime_type", "audio/mp3");
                contentValues2.put("_size", Long.valueOf(mfile.length()));
                contentValues2.put("artist", getString(R.string.app_name));
                contentValues2.put("is_ringtone", false);
                contentValues2.put("is_notification", false);
                contentValues2.put("is_alarm", true);
                contentValues2.put("is_music", false);
                try {
                    RingtoneManager.setActualDefaultRingtoneUri(this, 4, getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mfile.getAbsolutePath()), contentValues2));
                    Toast.makeText(this, "Alarm Tone set successfully: " + soundModel.getName(), Toast.LENGTH_LONG).show();
                } catch (Throwable th) {
                    Toast.makeText(this, "Feature is not working", Toast.LENGTH_LONG).show();
                }
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
}
