package com.gamechief.aifunnyringtone;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.initLoadNativeAd;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isBigNativeIntroScreenEnabled;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isDebugging;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isInAppPurchased;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.noOfNativeBetweenIntroSlider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gamechief.aifunnyringtone.adsCondition.AdUnifiedListening;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.adsCondition.PrefManager;
import com.gamechief.aifunnyringtone.databinding.ActivityMyIntroScreensBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.ArrayList;
import java.util.List;
public class MyIntroScreensActivity extends ParentActivity {
    private ActivityMyIntroScreensBinding binding;
    private PrefManager prefManager;
    private static final int TOTAL_INTRO_SCREENS = 3;
    private final List<IntroItem> introItems = new ArrayList<>();
    private final List<NativeAd> loadedNativeAds = new ArrayList<>();
    private int nativeLoadedCount = 0;
    private boolean isPagerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyIntroScreensBinding.inflate(getLayoutInflater());
        prefManager = new PrefManager(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });

        // Setup status bar
        UltimateBarX.statusBarOnly(this)
                .fitWindow(true)
                .transparent()
                .colorRes(android.R.color.transparent)
                .light(true)
                .apply();
        // Initialize small native ad

        if (isBigNativeIntroScreenEnabled() && !isInAppPurchased && noOfNativeBetweenIntroSlider() > 0) {
            showLoading();
            preloadBigNativeAds();
            // 🔥 Timeout fallback (max wait 4 seconds)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isPagerStarted) {
                    buildFinalPagerList();
                    startPager();
                }
            }, 6000);

        } else {
            buildFinalPagerList();
            startPager();
        }

        binding.btnNext.setOnClickListener(v -> nextPage());
        binding.closeBtn.setOnClickListener(v -> nextPage());
    }
    // =========================
    // DATA MODEL
    // =========================
    static class IntroItem {
        int layoutRes;
        NativeAd ad;
        IntroItem(int layoutRes) {this.layoutRes = layoutRes;}
        IntroItem(int layoutRes, NativeAd ad) {
            this.layoutRes = layoutRes;
            this.ad = ad;
        }
        boolean isAd() {
            return ad != null;
        }
    }
    // =========================
    // ADS LOADING
    // =========================
    private void preloadBigNativeAds() {
        for (int i = 0; i < noOfNativeBetweenIntroSlider(); i++) {
            loadNativeAd(new NativeCallback() {
                @Override
                public void onLoaded(NativeAd ad) {
                    loadedNativeAds.add(ad);
                    nativeLoadedCount++;
                    checkReady();
                }
                @Override
                public void onFail() {
                    nativeLoadedCount++;
                    checkReady();
                }
            });
        }
    }

    private void loadNativeAd(NativeCallback callback) {
        AdRequest request = new AdRequest.Builder().setHttpTimeoutMillis(6000).build();
        AdsController.createUnifiedAds(this, R.string.navtive_ad_intro_big, request, new AdUnifiedListening() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                callback.onLoaded(nativeAd);
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError error) {
                callback.onFail();
            }
        });
    }

    private void checkReady() {
        if (nativeLoadedCount >= noOfNativeBetweenIntroSlider() && !isPagerStarted) {
            runOnUiThread(() -> {
                buildFinalPagerList();
                startPager();
            });
        }
    }
    // =========================
    // BUILD FINAL PAGER
    // =========================
    private void buildFinalPagerList() {
        introItems.clear();
        int adIndex = 0;
        for (int i = 0; i < TOTAL_INTRO_SCREENS; i++) {
            int layout = getResources().getIdentifier("layout_intro_slider" + (i + 1), "layout", getPackageName());
            introItems.add(new IntroItem(layout));
            // Insert ad ONLY if available
            if (isBigNativeIntroScreenEnabled() && !isInAppPurchased && adIndex < loadedNativeAds.size()) {
                introItems.add(new IntroItem(R.layout.layout_intro_native_ad, loadedNativeAds.get(adIndex)));
                adIndex++;
            }
        }
    }

    // =========================
    // VIEW PAGER
    // =========================
    boolean isSmallNativeAdLoadedOrNot = false;
    private void startPager() {
        isPagerStarted = true;
        hideLoading();
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.lottieView.setVisibility(View.VISIBLE);
        binding.viewPager.setAdapter(new IntroPagerAdapter());
        binding.viewPager.addOnPageChangeListener(pageListener);
        updateDots(0);
//        initNativeAdSingleSmallIntroScreen(this, binding.nativeAdViewContainer, loadStatus -> isSmallNativeAdLoadedOrNot = loadStatus);

        initLoadNativeAd(this, binding.nativeAdViewContainer, loadStatus -> {
            isSmallNativeAdLoadedOrNot = loadStatus;
            // 🔥 CRITICAL FIX: re-evaluate current page UI
            runOnUiThread(() -> {
                int currentPos = binding.viewPager.getCurrentItem();
                boolean isAd = isBigAdPosition(currentPos);
                binding.nativeAdViewContainer.setVisibility((isSmallNativeAdLoadedOrNot && !isAd) ? View.VISIBLE : View.GONE
                );
            });
        });

    }

    private void nextPage() {
        int next = binding.viewPager.getCurrentItem() + 1;
        if (next < introItems.size()) {
            binding.viewPager.setCurrentItem(next);
        } else {
            openHome();
        }
    }

    private class IntroPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return introItems.size();
        }
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            IntroItem item = introItems.get(position);
            View view = LayoutInflater.from(MyIntroScreensActivity.this).inflate(item.layoutRes, container, false);
            if (item.isAd()) {
                TemplateView template = view.findViewById(R.id.templateView_1);
                if (template != null) showBigNative(template, item.ad);
            }
            container.addView(view);
            return view;
        }
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    // =========================
    // UI HELPERS
    // =========================
    private boolean isBigAdPosition(int position) {
        return introItems.get(position).isAd();
    }
    private final Handler closeBtnHandler = new Handler(Looper.getMainLooper());
    private Runnable closeBtnRunnable;

    private final ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            boolean isAd = isBigAdPosition(position);
            if (isSmallNativeAdLoadedOrNot) binding.nativeAdViewContainer.setVisibility(isAd ? View.GONE : View.VISIBLE);
            handleCloseButton(isAd);
            binding.lottieView.setVisibility(position < 2 && !isAd ? View.VISIBLE : View.GONE);

            binding.layoutDots.setVisibility(isAd ? View.GONE : View.VISIBLE);
            binding.btnNext.setVisibility(isAd ? View.GONE : View.VISIBLE);
            updateDots(position);
            binding.btnNext.setText(position == introItems.size() - 1 ? "Get Started" : "Next");
        }

        public void onPageScrolled(int p, float o, int px) {
        }
        public void onPageScrollStateChanged(int s) {}
    };

    private void handleCloseButton(boolean isAd) {
        // Always cancel previous callbacks (VERY IMPORTANT)
        if (closeBtnRunnable != null) closeBtnHandler.removeCallbacks(closeBtnRunnable);
        if (!isAd) {
            binding.closeBtn.setVisibility(View.GONE);
            binding.closeBtn.setAlpha(0f);
            return;
        }
        // Initially hidden
        binding.closeBtn.setVisibility(View.INVISIBLE);
        binding.closeBtn.setAlpha(0f);
        closeBtnRunnable = () -> {
            // Safety check (activity might be finishing)
            if (isFinishing() || isDestroyed()) return;
            binding.closeBtn.setVisibility(View.VISIBLE);
            binding.closeBtn.animate().alpha(1f).setDuration(300).start();
        };
        closeBtnHandler.postDelayed(closeBtnRunnable, 2000);
    }


    private void updateDots(int currentPosition) {
        binding.layoutDots.removeAllViews();
        int activeColor = getColor(com.google.android.ads.nativetemplates.R.color.gnt_primary);
        int inactiveColor = getColor(com.google.android.ads.nativetemplates.R.color.gnt_gray);
        int introIndex = 0;
        for (int i = 0; i < introItems.size(); i++) {
            if (!introItems.get(i).isAd()) {
                TextView dot = new TextView(this);
                dot.setText("•");
                dot.setTextSize(35);
                dot.setTextColor(i == currentPosition ? activeColor : inactiveColor);

                binding.layoutDots.addView(dot);
                introIndex++;
            }
        }
    }

    private void showBigNative(TemplateView template, NativeAd nativeAd) {
        NativeTemplateStyle style = new NativeTemplateStyle.Builder().build();
        template.setStyles(style);
        template.setNativeAd(nativeAd);
        template.setVisibility(View.VISIBLE);
        logAdImpression(nativeAd);
    }

    // =========================
    // LOADING / NAVIGATION
    // =========================
    private void showLoading() {
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.viewPager.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.loadingView.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.VISIBLE);
    }

    private void openHome() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // =========================
    // CLEANUP
    // =========================
    @Override
    protected void onDestroy() {
        if (closeBtnRunnable != null) closeBtnHandler.removeCallbacks(closeBtnRunnable);
        for (NativeAd ad : loadedNativeAds) if (ad != null) ad.destroy();
        loadedNativeAds.clear();
        super.onDestroy();
    }

    // =========================
    // CALLBACK
    // =========================
    private interface NativeCallback {
        void onLoaded(NativeAd ad);
        void onFail();
    }

    private void logAdImpression(NativeAd ad) {
        if (isDebugging) {
            Log.d("AdAnalytics", "Intro Big Native impression | headline = " + ad.getHeadline());
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("ad_type", "big_native_intro");
            bundle.putString("placement", "intro_screen");
            FirebaseAnalytics.getInstance(this).logEvent("ad_impression", bundle);
        }
    }
}
