package com.gamechief.aifunnyringtone;

import static com.gamechief.aifunnyringtone.adsCondition.AdsController.getSplashBannerAdEnabled;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isInAppPurchased;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isShowIntroEveryTimeEnabled;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.showDoubleSplashAppOpenAd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gamechief.aifunnyringtone.StickerPack.StickerPack;
import com.gamechief.aifunnyringtone.StickerPack.StickerPackLoader;
import com.gamechief.aifunnyringtone.StickerPack.StickerPackValidator;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.PrefManager;
import com.gamechief.aifunnyringtone.databinding.ActivitySplashBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Activity activity = this;
    private PrefManager prefManager;
    int delay = 1000;
    int dlo = 1;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (dlo > 5) binding.textView.setText("Slow Internet Please take a while");
            dlo++;
            handler.postDelayed(this, delay);
        }
    };
    AppOpenAd loadedAppOpenAd, loadedAppOpenAd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prefManager = new PrefManager(activity);

        // Flow start
        if (!isInAppPurchased) {
            initGDPRDialog(activity);
        }
        else {
            binding.view.setVisibility(View.GONE);
            stopLoading();
            handler.postDelayed(this::moveToNextActivity, 2500);
        }
        binding.goNext.setOnClickListener(v -> {
            if (!isInAppPurchased) {
                if (loadedAppOpenAd != null) loadedAppOpenAd.show(activity);
                else moveToNextActivity();
                if (showDoubleSplashAppOpenAd() && loadedAppOpenAd2 != null) loadedAppOpenAd2.show(activity);
            } else {
                moveToNextActivity();
            }
        });

        // Start async task
        new LoadListAsyncTask(this).execute();
    }
    /*==============================================================================================*/
    public void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        Log.d("SplashActivity", "showStickerPack called. List is " +
                (stickerPackList == null ? "null" : stickerPackList.size() + " items"));

//        binding.Progress.setVisibility(View.GONE);
//        binding.textView.setVisibility(View.GONE);

        if (stickerPackList == null || stickerPackList.isEmpty()) {
            Log.w("SplashActivity", "Sticker pack list is empty!");
            showErrorMessage("No sticker packs found");
            return;
        }

        // ✅ Instead of starting another activity, just store the list or display it
        // You can add RecyclerView here if you want to show the sticker list directly
        Log.d("SplashActivity", "Sticker packs loaded successfully.");

//        // Example: show first sticker pack name
//        binding.textView.setVisibility(View.VISIBLE);
//        binding.textView.setText("Loaded " + stickerPackList.size() + " sticker packs. First: "
//                + stickerPackList.get(0).name);
    }
    public void showErrorMessage(String errorMessage) {
        Log.e("SplashActivity", "Error fetching sticker packs: " + errorMessage);
        binding.Progress.setVisibility(View.GONE);
        binding.textView.setVisibility(View.VISIBLE);
        binding.textView.setText(getString(R.string.error_message, errorMessage));
    }
    // AsyncTask
    static class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<SplashActivity> contextWeakReference;

        LoadListAsyncTask(SplashActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Pair<String, ArrayList<StickerPack>> doInBackground(Void... voids) {
            try {
                final Context context = contextWeakReference.get();
                if (context != null) {
                    ArrayList<StickerPack> stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList == null || stickerPackList.isEmpty()) {
                        return new Pair<>("No sticker packs found", null);
                    }
                    for (StickerPack stickerPack : stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
                    }
                    return new Pair<>(null, stickerPackList);
                } else {
                    return new Pair<>("Context is null", null);
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "Error fetching sticker packs", e);
                return new Pair<>(e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(Pair<String, ArrayList<StickerPack>> result) {
            SplashActivity entryActivity = contextWeakReference.get();
            if (entryActivity != null) {
                if (result.first != null) {
                    entryActivity.showErrorMessage(result.first);
                } else {
                    entryActivity.showStickerPack(result.second);
                }
            }
        }
    }
    /*==============================================================================================*/

    private void startLoading() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    private void stopLoading() {
        handler.removeCallbacks(runnable);
    }

    public void moveToNextActivity() {
        if (isShowIntroEveryTimeEnabled()) {
            startActivity(new Intent(activity, MyIntroScreensActivity.class));
        } else {
            if (!prefManager.isFirstTimeLaunch()) {
                startActivity(new Intent(activity, MainActivity.class));
            } else {
                startActivity(new Intent(activity, MyIntroScreensActivity.class));
            }
        }
        finish();
    }
    public void initGDPRDialog(Activity activity) {
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build();
        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(activity, params, () -> {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, error -> {
                if (error != null) Log.d("Consent", error.getMessage());
                if (consentInformation.isConsentFormAvailable()) loadForm(activity, consentInformation);
                if (consentInformation.canRequestAds()) {
                    if (!isInAppPurchased) {
                        if (getSplashBannerAdEnabled()) AdsController.loadSplashBannerAd(activity, binding.adView);
                        binding.view.setVisibility(View.VISIBLE);
                        startLoading();
                        loadingAppOpenAd();
                    } else {
                        binding.view.setVisibility(View.GONE);
                        stopLoading();
                        handler.postDelayed(this::moveToNextActivity, 2500);
                    }
                }
            });

        }, error -> Log.e("Consent", error.getMessage()));
    }

    public void loadForm(Activity activity, ConsentInformation consentInformation) {
        UserMessagingPlatform.loadConsentForm(activity, form -> {
            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                form.show(activity, formError -> loadForm(activity, consentInformation));
            }
        }, formError -> Log.e("Consent", formError.getMessage()));
    }
    // =======================
    // Ads
    // =======================
    private void loadingAppOpenAd() {
        if (showDoubleSplashAppOpenAd()) loadingAppOpenAd2(activity, new AdRequest.Builder().setHttpTimeoutMillis(5000).build());
        FullScreenContentCallback callback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                stopLoading();
                moveToNextActivity();
            }
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                stopLoading();
                moveToNextActivity();
            }
        };
        AdRequest request = new AdRequest.Builder().setHttpTimeoutMillis(5000).build();
        loadAdWithFallback(0, request, callback);
    }

    private void loadAdWithFallback(int step, AdRequest request, FullScreenContentCallback callback) {
        String adUnit;
        if (step == 0) adUnit = getString(R.string.admob_startup_hf);
        else if (step == 1) adUnit = getString(R.string.admob_startup_mf);
        else if (step == 2) adUnit = getString(R.string.admob_startup);
        else {
            stopLoading();
            Toast.makeText(this, "Failed to load Ads", Toast.LENGTH_SHORT).show();
            moveToNextActivity();
            return;
        }

        AppOpenAd.load(this, adUnit, request, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                loadedAppOpenAd = ad;
                ad.setFullScreenContentCallback(callback);
                if (getSplashBannerAdEnabled()) {
                    binding.view.setVisibility(View.GONE);
                    binding.goNext.setVisibility(View.VISIBLE);
                } else {
                    ad.show(activity);
                }
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError error) {
                loadAdWithFallback(step + 1, request, callback);
            }
        });
    }

    private void loadingAppOpenAd2(Activity activity, AdRequest request) {
        AppOpenAd.load(activity, getString(R.string.admob_startup_double), request, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                loadedAppOpenAd2 = ad;
                Log.d("ADS_FLOW", "DOUBLE loaded");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLoading();
    }
}