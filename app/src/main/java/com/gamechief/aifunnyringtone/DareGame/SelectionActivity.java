package com.gamechief.aifunnyringtone.DareGame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;

import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivitySelectionBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class SelectionActivity extends ParentActivity {
    ActivitySelectionBinding binding;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivitySelectionBinding.inflate(getLayoutInflater());
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
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.binding.goBottle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SelectionActivity.this.startActivity(new Intent(SelectionActivity.this, AddNameList.class));
            }
        });
        this.binding.goAuto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SelectionActivity.this.startActivity(new Intent(SelectionActivity.this, AutoTurn.class));
            }
        });
    }

}
