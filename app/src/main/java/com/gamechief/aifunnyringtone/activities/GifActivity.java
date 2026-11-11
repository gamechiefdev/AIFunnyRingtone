package com.gamechief.aifunnyringtone.activities;


import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.GifAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityGifBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.gif.GifDrawable;

public class GifActivity extends ParentActivity {
    ActivityGifBinding binding;
    private List<GifDrawable> gifList;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityGifBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(onClickListener -> {
            onBackPressed();
        });
        Resources resources = getResources();
        this.gifList = new ArrayList();
        try {
            this.gifList.add(new GifDrawable(resources, R.drawable.gif25));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif26));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif27));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif29));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif31));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif32));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif33));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif35));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif36));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif38));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif39));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif1));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif2));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif3));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif4));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif5));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif6));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif7));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif8));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif9));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif10));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif11));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif12));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif13));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif14));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif15));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif16));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif17));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif18));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif19));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif20));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif21));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif22));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif23));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif24));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.binding.gifRv.setAdapter(new GifAdapter(this.gifList));
    }



    public void onResume() {
        super.onResume();
        this.binding.gifRv.setAdapter(new GifAdapter(this.gifList));
    }
}
