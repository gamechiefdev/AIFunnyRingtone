package com.gamechief.aifunnyringtone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.SoundAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityFavoritesScreenBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;

import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.List;

public class FavoritesScreen extends ParentActivity {
    private SoundAdapter adapter;
    ActivityFavoritesScreenBinding binding;
    /* access modifiers changed from: private */
    public List<SoundModel> favList;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityFavoritesScreenBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        binding.imageView7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FavoritesScreen.this.onBackPressed();
            }
        });
        this.favList = new ArrayList();
        this.adapter = new SoundAdapter(this, this.favList, new SoundAdapter.OnItemClickListener() {
            public void onItemClick(SoundModel soundModel, int position) {
                Intent intent = new Intent(FavoritesScreen.this, PlaySounds.class);
                intent.putParcelableArrayListExtra("soundList", new ArrayList(FavoritesScreen.this.favList));
                intent.putExtra("position", position);
                FavoritesScreen.this.startActivity(intent);
            }

            public void onFavoriteClick(SoundModel soundModel, int position) {
                FavoritesScreen.this.loadFavorites();
            }
        });
        this.binding.favList.setLayoutManager(new LinearLayoutManager(this));
        this.binding.favList.setAdapter(this.adapter);
    }



    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    /* access modifiers changed from: private */
    public void loadFavorites() {
        List<SoundModel> favs = HistoryStorage.loadFavorites(this);
        this.favList.clear();
        if (favs != null) {
            this.favList.addAll(favs);
        }
        
        for (int i = 0; i < this.favList.size(); i++) {
            this.favList.get(i).setNumber(String.valueOf(i + 1));
        }

        this.binding.countFav.setText(this.favList.size() + " Favorite(s)");
        this.adapter.updateData(this.favList);
    }
}
