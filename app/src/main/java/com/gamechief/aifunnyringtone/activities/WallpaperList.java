package com.gamechief.aifunnyringtone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gamechief.aifunnyringtone.R;

import com.gamechief.aifunnyringtone.adapters.Wall_Adapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityWallpaperListBinding;
import com.gamechief.aifunnyringtone.models.BgModel;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;

public class WallpaperList extends ParentActivity {
    Wall_Adapter adapter;
    ActivityWallpaperListBinding binding;
    ArrayList<BgModel> imageListBg = new ArrayList<>();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityWallpaperListBinding.inflate(getLayoutInflater());
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
                WallpaperList.this.onBackPressed();
            }
        });
        if (this.imageListBg.isEmpty()) {
            this.imageListBg.add(new BgModel(R.drawable.wall1));
            this.imageListBg.add(new BgModel(R.drawable.wall2));
            this.imageListBg.add(new BgModel(R.drawable.wall3));
            this.imageListBg.add(new BgModel(R.drawable.wall4));
            this.imageListBg.add(new BgModel(R.drawable.wall6));
            this.imageListBg.add(new BgModel(R.drawable.wall7));
            this.imageListBg.add(new BgModel(R.drawable.wall8));
            this.imageListBg.add(new BgModel(R.drawable.wall9));
            this.imageListBg.add(new BgModel(R.drawable.wall10));
            this.imageListBg.add(new BgModel(R.drawable.wall11));
            this.imageListBg.add(new BgModel(R.drawable.wall12));
            this.imageListBg.add(new BgModel(R.drawable.wall13));
            this.imageListBg.add(new BgModel(R.drawable.wall14));
            this.imageListBg.add(new BgModel(R.drawable.wall15));
            this.adapter = new Wall_Adapter(this, this.imageListBg, new Wall_Adapter.onCardClickListener() {
                @Override
                public void onCardClick(int i) {
                    Intent intent = new Intent(WallpaperList.this, WallpaperPreview.class);
                    intent.putExtra("wallpaper", imageListBg.get(i).getImage());
                    Log.d("getWallpaper", "onCardClick: " + imageListBg.get(i));
                    startActivity(intent);
                }
            });
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            this.binding.wallList.setAdapter(this.adapter);
            this.binding.wallList.setLayoutManager(gridLayoutManager);
            this.binding.wallList.setHasFixedSize(true);
        }
    }




}
