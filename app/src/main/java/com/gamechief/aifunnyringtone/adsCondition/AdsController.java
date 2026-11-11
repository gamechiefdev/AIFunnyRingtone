package com.gamechief.aifunnyringtone.adsCondition;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.soloader.nativeloader.NativeLoader;
import com.facebook.soloader.nativeloader.SystemDelegate;
import com.gamechief.aifunnyringtone.BuildConfig;
import com.gamechief.aifunnyringtone.R;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
public class AdsController extends Application {
    private static Context context;
    public static IJCustomAdMobInterstitialAdListener customAdMobKurtiInterstitialAdListener;
    public static boolean isInAppPurchased = false;
    public static String IS_ADS_FREE_VERSION = "is_in_app_purchased";
    public static boolean isDebugging = false;
    public static int adShowInterval = 3;

    public static SharedPreferences sharedPreferences;
    private static AdsController mInstance;
    public static boolean isShareAppClicked = false;
    public static boolean isShowingAdIntersial = false;
    private static RewardedAd rewardedAd;

    public static int i = -1;
    public static synchronized AdsController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);
        mInstance = this;
        context = getApplicationContext();
        isDebugging = !context.getResources().getString(R.string.isDebugging).equals("false");

        NativeLoader.init(new SystemDelegate());
        Fresco.initialize(this);
        initFirebaseRemoteConfig(this);

        Log.d("kurti", "design");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        isInAppPurchased = sharedPreferences.getBoolean(IS_ADS_FREE_VERSION, false);
        if (!isInAppPurchased) {
            MobileAds.initialize(this, initializationStatus -> {});
            new AppOpenManager ( this );
        }
    }

    public void RegisterActivityEventInFb(String activityName){
        Bundle bundle = new Bundle();
        bundle.putBoolean(activityName+"_value_1",true);
        FirebaseAnalytics.getInstance(this).logEvent(activityName,bundle);
    }


    public static boolean isIsInAppPurchased() {
        return isInAppPurchased;
    }
    public static void setIsInAppPurchased(boolean isInAppPurchased) {
        AdsController.isInAppPurchased = isInAppPurchased;
    }


    public interface IJCustomAdMobInterstitialAdListener {
        void onInterstitialAdClosed();
    }

    public static void loadSplashBannerAd(Activity activity, FrameLayout frameLayout) {
        AdSize adSize ;
        if (splashBannerType == 2) adSize = AdSize.getLargePortraitAnchoredAdaptiveBannerAdSize(activity, ViewGroup.LayoutParams.MATCH_PARENT);
        else adSize = AdSize.LARGE_BANNER;
        AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(6000).build();
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(frameLayout.getContext().getResources().getString(R.string.admob_banner_splash));
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                Log.e("776_bannerAd",loadAdError.getMessage());
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("776_bannerAd","Normal floor banner loaded");
            }
        });
    }

    public void loadBigBanner(FrameLayout frameLayout) {
        if (!isInAppPurchased) {
            AdRequest.Builder requestBuilder = new AdRequest.Builder();
            requestBuilder.setHttpTimeoutMillis(6000);
            AdSize adSize = AdSize.MEDIUM_RECTANGLE;
            loadHighFloorBanner(frameLayout, requestBuilder.build(), adSize);
        }
    }
    public void loadLargeBanner(FrameLayout frameLayout) {
        if (!isInAppPurchased) {
            AdRequest.Builder requestBuilder = new AdRequest.Builder();
            requestBuilder.setHttpTimeoutMillis(6000);
            AdSize adSize = AdSize.LARGE_BANNER;
            loadHighFloorBanner(frameLayout, requestBuilder.build(), adSize);
        }
    }
    public void loadAdaptiveBannerAd(Activity activity,FrameLayout frameLayout) {
        if (!isInAppPurchased) {
//            AdRequest.Builder requestBuilder = new AdRequest.Builder();
//            requestBuilder.setHttpTimeoutMillis(6000);
            AdSize adSize = getAdSize(activity);
            loadHighFloorBanner(frameLayout, getBannerAd(2), adSize);
        }
    }
    private void loadHighFloorBanner(FrameLayout frameLayout,AdRequest request,AdSize adSize){
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(frameLayout.getContext().getResources().getString(R.string.admob_banner_hf));
        adView.loadAd(request);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                Log.e("775_bannerAd",loadAdError.getMessage());
                loadMediumFloorBanner(frameLayout,request,adSize);
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("775_bannerAd","High floor banner loaded");
            }
        });
    }
    private void loadMediumFloorBanner(FrameLayout frameLayout,AdRequest request,AdSize adSize){
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(frameLayout.getContext().getResources().getString(R.string.admob_banner_mf));
        adView.loadAd(request);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                Log.e("775_bannerAd",loadAdError.getMessage());
                loadNormalBanner(frameLayout,request,adSize);
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("775_bannerAd","Medium floor banner loaded");
            }
        });
    }
    public void loadNormalBanner(FrameLayout frameLayout, AdRequest request, AdSize adSize){
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(frameLayout.getContext().getResources().getString(R.string.admob_banner));
        adView.loadAd(request);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                Log.e("775_bannerAd",loadAdError.getMessage());
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("775_bannerAd","Normal floor banner loaded");
            }
        });

    }

    public static AdSize getAdSize(Activity activity) {
        // Determine the screen width (less decorations) to use for the ad width.
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        // Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
    public static AdRequest getBannerAd(int type) {
        if (isAdaptiveBannerEnable) {
            Bundle extras = new Bundle();
            if (type == 1) {
                extras.putString("collapsible", "top");
            } else {
                extras.putString("collapsible", "bottom");
            }
            return new AdRequest.Builder().setHttpTimeoutMillis(6000).addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
        }
        return new AdRequest.Builder().setHttpTimeoutMillis(6000).build();
    }

    public static void showInterstitialAdWithLoader(Activity activity, FullScreenContentCallback listener) {
        if (!isInAppPurchased) {
            isShareAppClicked = true;
            ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading ad...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(4000).build();
            FullScreenContentCallback listener2 = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    isShowingAdIntersial = false;
                    if (customAdMobKurtiInterstitialAdListener != null) customAdMobKurtiInterstitialAdListener.onInterstitialAdClosed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    isShowingAdIntersial = false;
                    if (customAdMobKurtiInterstitialAdListener != null) customAdMobKurtiInterstitialAdListener.onInterstitialAdClosed();
                }
            };

            InterstitialAd.load(activity, activity.getString(R.string.interstitial_hf), adRequest, new InterstitialAdLoadCallback () {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    Log.d("779_interHighFloor_", "onAdLoaded");
                    isShowingAdIntersial = true;
                    isShareAppClicked = true;
                    if (listener != null) {
                        interstitialAd.setFullScreenContentCallback(listener);
                    } else {
                        interstitialAd.setFullScreenContentCallback(listener2);
                    }
                    progressDialog.dismiss();
                    interstitialAd.show(activity);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("779_interHighFloor_", loadAdError.getMessage());
                    InterstitialAd.load(activity, activity.getString(R.string.interstitial_mf), adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.i("779_interMediumFloor_", "onAdLoaded");
                            isShowingAdIntersial = true;
                            isShareAppClicked = true;
                            if (listener != null) {
                                interstitialAd.setFullScreenContentCallback(listener);
                            } else {
                                interstitialAd.setFullScreenContentCallback(listener2);
                            }
                            progressDialog.dismiss();
                            interstitialAd.show(activity);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i("779_interMediumFloor_", loadAdError.getMessage());

                            InterstitialAd.load(activity, activity.getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    Log.i("779_interNormal_", "onAdLoaded");

                                    isShowingAdIntersial = true;
                                    isShareAppClicked = true;
                                    if (listener != null) {
                                        interstitialAd.setFullScreenContentCallback(listener);
                                    } else {
                                        interstitialAd.setFullScreenContentCallback(listener2);
                                    }
                                    progressDialog.dismiss();
                                    interstitialAd.show(activity);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    Log.i("779_interNormal_", loadAdError.getMessage());
                                    progressDialog.dismiss();
                                    Toast.makeText(activity, "Failed to load Ads", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });

            if (isShowDoubleInterstitialAdEnabled()) {
                InterstitialAd.load(activity, activity.getString(R.string.interstitial_double), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                Log.i("779_inter_Double", "onAdLoaded");
                                interstitialAd.show(activity);
                            }
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.i("779_inter_Double", loadAdError.getMessage());
                            }
                        });
            }
        }

    }
    private static void loadRewardedAd(Activity activity, String adUnitId, AdRequest adRequest,
                                       FullScreenContentCallback listener,
                                       FullScreenContentCallback defaultListener,
                                       ProgressDialog progressDialog) {
        RewardedAd.load(activity, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                Log.d("AdLoad", "onAdLoaded: " + adUnitId);
                isShowingAdIntersial = false;
                isShareAppClicked = true;
                rewardedAd = ad;
                if (listener != null) {
                    rewardedAd.setFullScreenContentCallback(listener);
                } else {
                    rewardedAd.setFullScreenContentCallback(defaultListener);
                }
                progressDialog.dismiss();
                rewardedAd.show(activity, rewardItem -> {
                    // Handle the reward
                    Log.d("AdReward", "User earned the reward.");
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("AdLoad", "onAdFailedToLoad: " + loadAdError.getMessage());
                if (adUnitId.equals(activity.getString(R.string.rewardAd_high))) {
                    loadRewardedAd(activity, activity.getString(R.string.rewardAd_medium), adRequest, listener, defaultListener, progressDialog);
                } else if (adUnitId.equals(activity.getString(R.string.rewardAd_medium))) {
                    loadRewardedAd(activity, activity.getString(R.string.rewardAd_low), adRequest, listener, defaultListener, progressDialog);
                } else {
                    Log.i("AdLoad", "No more ad units to load.");
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Failed to load Ads", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean initialLayoutComplete = false;

    public static void createUnifiedAds(Context context, int unitId,AdRequest request, AdUnifiedListening listening) {
        AdLoader.Builder builder = new AdLoader.Builder(context,context.getString(unitId));
        builder.forNativeAd(listening);
        builder.withAdListener(listening);
        AdLoader adLoader = builder.build();
        adLoader.loadAds(request,1);
        listening.setAdLoader(adLoader);
    }

    public static void initLoadNativeAd(@NonNull Context context, @NonNull TemplateView templateView, IntroSmallNativeAdListener nativeAdListener) {
        NativeTemplateStyle style = new NativeTemplateStyle.Builder().build();
        templateView.setStyles(style);
        if (!isInAppPurchased) {
            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(6000).build();

            AdsController.createUnifiedAds(context, R.string.navtive_ad_intro_hf, adRequest, new AdUnifiedListening() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    Log.d("NativeAd_", "Native ad is loaded");
                    templateView.setNativeAd(nativeAd);
                    templateView.setVisibility(View.VISIBLE);
                    nativeAdListener.onAdLoaded(true);
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("NativeAd_", "Failed  Native loading: " + loadAdError.getMessage());
                    templateView.setVisibility(View.GONE);
                    AdsController.createUnifiedAds(context, R.string.navtive_ad_intro_mf, adRequest, new AdUnifiedListening() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            Log.d("NativeAd_", "Native ad is loaded");
                            templateView.setNativeAd(nativeAd);
                            templateView.setVisibility(View.VISIBLE);
                            nativeAdListener.onAdLoaded(true);
                        }
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e("NativeAd_", "Failed  Native loading: " + loadAdError.getMessage());
                            templateView.setVisibility(View.GONE);
                            AdsController.createUnifiedAds(context, R.string.navtive_ad_intro_nf, adRequest, new AdUnifiedListening() {
                                @Override
                                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                                    Log.d("NativeAd_", "Native ad is loaded");
                                    templateView.setNativeAd(nativeAd);
                                    templateView.setVisibility(View.VISIBLE);
                                    nativeAdListener.onAdLoaded(true);
                                }
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    Log.e("NativeAd_", "Failed  Native loading: " + loadAdError.getMessage());
                                    templateView.setVisibility(View.GONE);
                                    nativeAdListener.onAdLoaded(false);
                                }
                            });
                        }
                    });

                }
            });
        }
        else {
            templateView.setVisibility(View.GONE);
        }
    }

    /*============================================================================================*/
    public static boolean isSplashBannerAdEnable = true;//done
    public static boolean isAdaptiveBannerEnable = true;//
    //new
    public static boolean showDoubleSplashAppOpenAd = false;//
    public static boolean showDoubleInterstitialAd = false;//
    public static boolean showIntroEveryTime = false;//
    public static boolean isBigNativeIntroEnable = false;//
    public static int noOfNativeBetweenIntroSlider = 0;
    public static int splashBannerType = 2;// 1 for large banner, 2 for large anchor adaptive

    private void initFirebaseRemoteConfig(Context sContext) {
        String TAG = "mFirebaseRemoteConfig_1";
        FirebaseApp.initializeApp(sContext);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        long fetchInterval = BuildConfig.DEBUG ? 0 : 3600;
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(fetchInterval).build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean updated = task.getResult();
                if (isFirebaseEnabled()) {
                    if (!mFirebaseRemoteConfig.getString("adShowInterval").isEmpty())
                        adShowInterval = Integer.parseInt(mFirebaseRemoteConfig.getString("adShowInterval"));
                    if (!mFirebaseRemoteConfig.getString("splashBannerType").isEmpty())
                        splashBannerType = Integer.parseInt(mFirebaseRemoteConfig.getString("splashBannerType"));
                    if (!mFirebaseRemoteConfig.getString("noOfNativeBetweenIntroSlider").isEmpty())
                        noOfNativeBetweenIntroSlider = Integer.parseInt(mFirebaseRemoteConfig.getString("noOfNativeBetweenIntroSlider"));

                    showDoubleSplashAppOpenAd = mFirebaseRemoteConfig.getBoolean("showDoubleSplashAppOpenAd");
                    isBigNativeIntroEnable = mFirebaseRemoteConfig.getBoolean("isBigNativeIntroEnable");

                    isSplashBannerAdEnable = mFirebaseRemoteConfig.getBoolean("isSplashBannerAdEnable");
                    isAdaptiveBannerEnable = mFirebaseRemoteConfig.getBoolean("isAdaptiveBannerEnable");
                }
//                isShowAdOnInAppClose = mFirebaseRemoteConfig.getBoolean("");
                Log.d(TAG, "Config params updated: " + updated+" - "+adShowInterval
                        +" - " +isSplashBannerAdEnable
                        +" - "
                );
            } else {
                Log.d(TAG, "Config Fetching data failed: " +" - "+adShowInterval);
            }
        });
        Log.d(TAG, "Config data nativeInterval: " +" - "+showDoubleSplashAppOpenAd);
    }


    public static boolean isFirebaseEnabled(){
        return FirebaseRemoteConfig.getInstance().getBoolean("isFirebaseEnabled");
//        return false;
    }
    public static boolean getSplashBannerAdEnabled() {
        if (isFirebaseEnabled()) return isSplashBannerAdEnable =  FirebaseRemoteConfig.getInstance().getBoolean("isSplashBannerAdEnable");
        else return false;
//        return true;
    }
    public static boolean isShowIntroEveryTimeEnabled() {
        if (isFirebaseEnabled()) return showIntroEveryTime = FirebaseRemoteConfig.getInstance().getBoolean("showIntroEveryTime");
        else return false;
    }
    public static boolean isShowDoubleInterstitialAdEnabled() {
        if (isFirebaseEnabled()) return showDoubleInterstitialAd = FirebaseRemoteConfig.getInstance().getBoolean("showDoubleInterstitialAd");
        else return false;
//        return true;
    }
    public static int noOfNativeBetweenIntroSlider() {
        if (isFirebaseEnabled()) return noOfNativeBetweenIntroSlider = Integer.parseInt(FirebaseRemoteConfig.getInstance().getString("noOfNativeBetweenIntroSlider"));
        else return 0;
//        return 2;
    }
    public static boolean showDoubleSplashAppOpenAd() {
        if (isFirebaseEnabled()) return showDoubleSplashAppOpenAd = FirebaseRemoteConfig.getInstance().getBoolean("showDoubleSplashAppOpenAd");
        else return false;
//        return true;
    }
    public static boolean isBigNativeIntroScreenEnabled() {
        if (isFirebaseEnabled()) return isBigNativeIntroEnable = FirebaseRemoteConfig.getInstance().getBoolean("isBigNativeIntroEnable");
        else return false;
//        return true;
    }
    /*============================================================================================*/
}
