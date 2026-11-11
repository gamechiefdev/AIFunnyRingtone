package com.gamechief.aifunnyringtone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gamechief.aifunnyringtone.adapters.IntroImageAdapter;
import com.gamechief.aifunnyringtone.databinding.ActivityIntroBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;
    private final int[] imageList = {
            R.drawable.intro_1,
            R.drawable.intro_2,
            R.drawable.intro_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        UltimateBarX.statusBarOnly(this).fitWindow(true).transparent()
                .colorRes(android.R.color.white).light(true).apply();



        if (isActivityCompleted(getIntent().getBooleanExtra("check_completion", true))) {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
            return;
        }

        loadNativeAd();
        // ✅ Setup ViewPager with only images
        binding.viewPager.setAdapter(new IntroImageAdapter(imageList));
        binding.viewPager.setOffscreenPageLimit(imageList.length);

        // ✅ Indicator & button handling
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);

                // ✅ Change text for each intro slide
                switch (position) {
                    case 0:
                        binding.heading.setText("Smart Experience");
                        binding.subHeading.setText("Discover amazing features designed to make your daily life more fun, easy, and interactive.");
                        break;
                    case 1:
                        binding.heading.setText("Explore Powerful Features");
                        binding.subHeading.setText("Enjoy tools like AI generation, sound effects, wallpapers, and creative utilities all in one app.");
                        break;
                    case 2:
                        binding.heading.setText("Let’s Get Started");
                        binding.subHeading.setText("Start using the app now and unlock creative possibilities made just for you.");
                        break;
                }

                // ✅ Toggle Next / Go button
                if (position == binding.viewPager.getAdapter().getItemCount() - 1) {
                    binding.NextId.setVisibility(View.GONE);
                    binding.NextIdGo.setVisibility(View.VISIBLE);
                } else {
                    binding.NextId.setVisibility(View.VISIBLE);
                    binding.NextIdGo.setVisibility(View.GONE);
                }
            }
        });


        // Next button → move to next page
        binding.NextId.setOnClickListener(v -> {
            int next = binding.viewPager.getCurrentItem() + 1;
            if (next < imageList.length) binding.viewPager.setCurrentItem(next, true);
        });

        // Final Go button → finish intro
        binding.NextIdGo.setOnClickListener(v -> {
            setActivityCompleted();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });


    }

    // ✅ Update 3 indicators
    private void updateIndicators(int position) {
        binding.imageView1.setImageResource(position == 0 ? R.drawable.indicator1 : R.drawable.indicator2);
        binding.imageView2.setImageResource(position == 1 ? R.drawable.indicator1 : R.drawable.indicator2);
        binding.imageView3.setImageResource(position == 2 ? R.drawable.indicator1 : R.drawable.indicator2);
    }

    private boolean isActivityCompleted(boolean checkCompletion) {
        if (!checkCompletion) return false;
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getBoolean("activity_completed", false);
    }

    private void setActivityCompleted() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("activity_completed", true);
        editor.apply();
    }



//    private void loadFullNativeAd() {
//        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.navtive_ad_id))
//                .forNativeAd(ad -> {
//                    nativeAd = ad;
//                    // Make sure nativeAd is not null before setting it to the adapter
//                    if (nativeAd != null) {
//                        binding.viewPager.setAdapter(new IntroImageAdapter(imageList, nativeAd, new IntroImageAdapter.OnNextSlideClickListener() {
//                            @Override
//                            public void onNextSlide() {
//                                binding.viewPager.setCurrentItem(3, true); // scroll to 4th screen (position 3)
//
//                            }
//                        }));
//                        updateIndicators(0);
//                    } else {
//                        Log.e("AdLoader", "Native ad is null");
//                    }
//                })
//                .build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());
//    }
//

    public void loadNativeAd() {
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.navtive_ad_id))
                .forNativeAd(nativeAd -> {
                    int[] adFrameIds = {
                            R.id.adView,
                    };
                    for (int frameId : adFrameIds) {
                        FrameLayout adFrame = findViewById(frameId);
                        adFrame.removeAllViews();
                        // Inflate a NEW adView for each container
                        NativeAdView adView = (NativeAdView) getLayoutInflater()
                                .inflate(R.layout.ad_native_layout_banner, null);

                        populateNativeAdView(nativeAd, adView);

                        adFrame.addView(adView);
                    }

                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.e("NativeAd", "Failed to load: " + adError.getMessage());
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Headline
        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        // Body
        TextView bodyView = adView.findViewById(R.id.ad_body);
        if (nativeAd.getBody() != null) {
            bodyView.setText(nativeAd.getBody());
            bodyView.setVisibility(View.VISIBLE);
        } else {
            bodyView.setVisibility(View.GONE);
        }
        adView.setBodyView(bodyView);

        // Call to Action
        Button ctaView = adView.findViewById(R.id.ad_call_to_action);
        if (nativeAd.getCallToAction() != null) {
            ctaView.setText(nativeAd.getCallToAction());
            ctaView.setVisibility(View.VISIBLE);
        } else {
            ctaView.setVisibility(View.GONE);
        }
        adView.setCallToActionView(ctaView);

        // App Icon
        ImageView iconView = adView.findViewById(R.id.ad_app_icon);
        if (nativeAd.getIcon() != null) {
            iconView.setImageDrawable(nativeAd.getIcon().getDrawable());
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }
        adView.setIconView(iconView);

        // Advertiser
        TextView advertiserView = adView.findViewById(R.id.ad_advertiser);
        if (nativeAd.getAdvertiser() != null) {
            advertiserView.setText(nativeAd.getAdvertiser());
            advertiserView.setVisibility(View.VISIBLE);
        } else {
            advertiserView.setVisibility(View.GONE);
        }
        adView.setAdvertiserView(advertiserView);

        // Media View
//        MediaView mediaView = adView.findViewById(R.id.ad_media);
//        adView.setMediaView(mediaView);

        // Final binding
        adView.setNativeAd(nativeAd);

    }
}