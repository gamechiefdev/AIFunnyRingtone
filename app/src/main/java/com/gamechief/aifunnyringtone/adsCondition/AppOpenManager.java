package com.gamechief.aifunnyringtone.adsCondition;

import static androidx.lifecycle.Lifecycle.Event.ON_START;


import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isInAppPurchased;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.isShowingAdIntersial;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.gamechief.aifunnyringtone.SplashActivity;
import com.gamechief.aifunnyringtone.R;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;
public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private final AdsController myApplication;
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    public static boolean isShowingAd = false;
    public static boolean isAppOpenAllowed = true;
    private static boolean isShowAd ;
    private long loadTime = 0;

    public static boolean isIsShowAd() {
        return isShowAd;
    }

    public static void setIsShowAd(boolean isShowAd) {
        AppOpenManager.isShowAd = isShowAd;
    }

    public AppOpenManager(AdsController myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /** LifecycleObserver methods */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!(currentActivity instanceof SplashActivity)) {
            if (!isInAppPurchased && !isShowingAdIntersial){
                if (isAppOpenAllowed) showAdIfAvailable();
                else isAppOpenAllowed = true;
            }
        }
    }

    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            Log.d("salman", "Will show ad.");
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
//                            isShowAd = false;
                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {}

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };
            if (!isInAppPurchased) {
                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);
            }
        } else {
            Log.d("salman", "Can not show ad.");
            fetchAd();
        }
    }

    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                super.onAdLoaded(appOpenAd);
                AppOpenManager.this.appOpenAd = appOpenAd;
                AppOpenManager.this.loadTime = (new Date()).getTime();
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        };
        AdRequest request = getAdRequest();
        AppOpenAd.load(myApplication, myApplication.getString( R.string.admob_startup), request, loadCallback);
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
        isShowAd = true;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) { currentActivity = activity; }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        isShowAd = false;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) { isShowingAd = false; }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { currentActivity = null; }

}