package com.gamechief.aifunnyringtone.adsCondition;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.nativead.NativeAd;

public abstract class AdUnifiedListening extends AdListener implements NativeAd.OnNativeAdLoadedListener {
    private AdLoader adLoader;

    public AdLoader getAdLoader() {
        return adLoader;
    }

    public void setAdLoader(AdLoader adLoader) {
        this.adLoader = adLoader;
    }
}
