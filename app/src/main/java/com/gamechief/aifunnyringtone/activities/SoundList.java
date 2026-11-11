package com.gamechief.aifunnyringtone.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.SoundAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivitySoundListBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoundList extends ParentActivity {
    ActivitySoundListBinding binding;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivitySoundListBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        showSeldomIntersAd(this);
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SoundList.this.onBackPressed();
            }
        });
        List<Integer> soundRes = getIntent().getIntegerArrayListExtra("sound_res");
        List<Integer> imageRes = getIntent().getIntegerArrayListExtra("image_res");
        List<String> soundNames = getIntent().getStringArrayListExtra("sound_names");
        final List<SoundModel> soundList = new ArrayList<>();
        List<SoundModel> savedFavorites = HistoryStorage.loadFavorites(this);
        if (soundRes != null) {
            for (int i = 0; i < soundRes.size(); i++) {
                String duration = getSoundDuration(soundRes.get(i).intValue());
                boolean isFav = false;
                Iterator<SoundModel> it = savedFavorites.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().getSoundResId() == soundRes.get(i).intValue()) {
                            isFav = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                int image = (imageRes != null && i < imageRes.size()) ? imageRes.get(i) : (imageRes != null && !imageRes.isEmpty() ? imageRes.get(0) : R.drawable.app_icon);
                String name = (soundNames != null && i < soundNames.size()) ? soundNames.get(i) : "Sound " + (i + 1);
                soundList.add(new SoundModel(String.valueOf(i + 1), image, name, duration, isFav, soundRes.get(i).intValue()));
            }
        }
        SoundAdapter adapter = new SoundAdapter(this, soundList, new SoundAdapter.OnItemClickListener() {
            public void onItemClick(SoundModel soundModel, int position) {
                Intent intent = new Intent(SoundList.this, PlaySounds.class);
                intent.putParcelableArrayListExtra("soundList", new ArrayList(soundList));
                intent.putExtra("position", position);
                SoundList.this.startActivity(intent);
            }

            public void onFavoriteClick(SoundModel soundModel, int position) {
                Toast.makeText(SoundList.this, soundModel.isFavorite() ? "Added to favorites" : "Removed from favorites", 0).show();
            }
        });
        this.binding.soundList.setLayoutManager(new LinearLayoutManager(this));
        this.binding.soundList.setAdapter(adapter);
    }


    private String getSoundDuration(int resId) {
        MediaPlayer mp = MediaPlayer.create(this, resId);
        if (mp == null) {
            return "00:00";
        }
        int durationMs = mp.getDuration();
        mp.release();
        return String.format("%02d:%02d", new Object[]{Integer.valueOf((durationMs / 1000) / 60), Integer.valueOf((durationMs / 1000) % 60)});
    }
}
