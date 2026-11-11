package com.gamechief.aifunnyringtone.myRingtone;

import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.adsCondition.SharedPreferenceManager;
import com.gamechief.aifunnyringtone.databinding.ActivitySaveNameRingtoneBinding;
import com.gamechief.aifunnyringtone.models.SavedRingtone;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.List;

public class SaveNameRingtone extends ParentActivity {
    SavedRingtoneAdapter adapter;
    ActivitySaveNameRingtoneBinding binding;
    List<SavedRingtone> list = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivitySaveNameRingtoneBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(v -> onBackPressed());
        this.adapter = new SavedRingtoneAdapter(this, this.list);
        this.binding.saveList.setLayoutManager(new LinearLayoutManager(this));
        this.binding.saveList.setAdapter(this.adapter);
        SharedPreferenceManager sp = new SharedPreferenceManager(this, "my_ringtone_prefs");
        this.list.clear();
        this.list.addAll(sp.getRingtoneList());
        this.adapter.notifyDataSetChanged();
    }

}
