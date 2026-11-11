package com.gamechief.aifunnyringtone.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.DonkeyAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityDonkeyScreenBinding;
import com.gamechief.aifunnyringtone.models.donkeyModel;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.ArrayList;
import java.util.List;

public class DonkeyScreen extends ParentActivity {

    ActivityDonkeyScreenBinding binding;
    private List<donkeyModel> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityDonkeyScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);

        list.add(new donkeyModel("Chillstep", R.drawable.donkey1, "The Victory Celebration",0));
        list.add(new donkeyModel("Shuffle King", R.drawable.donkey2, "The Crowned Shuffler",0));
        list.add(new donkeyModel("Bollywood Donkey", R.drawable.donkey3, "Full On Entertainment!",0));
        list.add(new donkeyModel("Dhol & Dance", R.drawable.donkey4, "Mein Ho Jaye Balle Balle",0));
        list.add(new donkeyModel("Funny Dance", R.drawable.donkey5, "Jazz Swing",0));
        list.add(new donkeyModel("Rain Dance", R.drawable.donkey6, "The Emotional Pop",0));
        list.add(new donkeyModel("DJ Festival", R.drawable.donkey7, "The donkey performs",0));
        list.add(new donkeyModel("Morning Dance", R.drawable.donkey8, "Donkey in Park",0));
        list.add(new donkeyModel("Cherry Blossom", R.drawable.donkey9, "Donkey performs graceful",0));
        list.add(new donkeyModel("Good Bye", R.drawable.donkey10, "The Dancing King",0));


        DonkeyAdapter adapter = new DonkeyAdapter(list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        binding.donkeyList.setLayoutManager(gridLayoutManager);
        binding.donkeyList.setAdapter(new DonkeyAdapter(list));
        binding.donkeyList.setAdapter(adapter);

    }



}