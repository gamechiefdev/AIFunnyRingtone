package com.gamechief.aifunnyringtone.activities;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityAiRecordSoundBinding;
import com.gamechief.aifunnyringtone.fragments.LiveSpeaker;
import com.gamechief.aifunnyringtone.fragments.RecordSound;

import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.List;

public class AiRecordSound extends ParentActivity {
    ActivityAiRecordSoundBinding binding;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAiRecordSoundBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AiRecordSound.this.onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= 33) {
            List<String> permissions = new ArrayList<>();
            if (checkSelfPermission("android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
                permissions.add("android.permission.READ_PHONE_STATE");
            }
            if (checkSelfPermission("android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                permissions.add("android.permission.RECORD_AUDIO");
                if (checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                    permissions.add("android.permission.READ_MEDIA_AUDIO");
                }
                if (!permissions.isEmpty()) {
                    requestPermissions((String[]) permissions.toArray(new String[0]), 1001);
                }
            } else {
                List<String> permissionss = new ArrayList<>();
                if (checkSelfPermission("android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                    permissionss.add("android.permission.RECORD_AUDIO");
                }
                if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                    permissionss.add("android.permission.READ_EXTERNAL_STORAGE");
                }
                if (!permissionss.isEmpty()) {
                    requestPermissions((String[]) permissionss.toArray(new String[0]), 1001);
                }
            }
        }
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new RecordSound(), "");
        pagerAdapter.addFragment(new LiveSpeaker(), "");
        this.binding.viewPagerNailArt.setAdapter(pagerAdapter);
        this.binding.viewPagerNailArt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                AiRecordSound.this.resetIconColors();
                switch (position) {
                    case 0:
                        AiRecordSound.this.binding.homeTxt.setColorFilter(ContextCompat.getColor(AiRecordSound.this, R.color.nav_selected));
                        AiRecordSound.this.binding.homTxt.setTextColor(ContextCompat.getColor(AiRecordSound.this, R.color.nav_selected));
                        return;
                    case 1:
                        AiRecordSound.this.binding.dualTxt.setColorFilter(ContextCompat.getColor(AiRecordSound.this, R.color.nav_selected));
                        AiRecordSound.this.binding.listTxt.setTextColor(ContextCompat.getColor(AiRecordSound.this, R.color.nav_selected));
                        return;
                    default:
                        return;
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.binding.homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewPagerNailArt.setCurrentItem(0);
            }
        });
        this.binding.dualTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.viewPagerNailArt.setCurrentItem(1);
            }
        });
    }


    private static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList();
        private final List<String> fragmentTitleList = new ArrayList();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.fragmentTitleList.add(title);
        }

        public Fragment getItem(int position) {
            return this.fragmentList.get(position);
        }

        public int getCount() {
            return this.fragmentList.size();
        }

        public CharSequence getPageTitle(int position) {
            return this.fragmentTitleList.get(position);
        }
    }

    /* access modifiers changed from: private */
    public void resetIconColors() {
        int unselectedColor = ContextCompat.getColor(this, R.color.white);
        this.binding.homeTxt.setColorFilter(unselectedColor);
        this.binding.dualTxt.setColorFilter(unselectedColor);
        this.binding.homTxt.setTextColor(unselectedColor);
        this.binding.listTxt.setTextColor(unselectedColor);
    }
}
