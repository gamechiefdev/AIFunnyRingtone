package com.gamechief.aifunnyringtone.PuzzleGame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gamechief.aifunnyringtone.PuzzleGame.adapter.PuzzleListAdapter;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityPuzzleListBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.Arrays;

public class PuzzleList extends ParentActivity {
    ActivityPuzzleListBinding binding;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityPuzzleListBinding.inflate(getLayoutInflater());
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
        PuzzleListAdapter adapter = new PuzzleListAdapter(this, Arrays.asList(new Integer[]{
                Integer.valueOf(R.drawable.puzzle1),
                Integer.valueOf(R.drawable.puzzle2),
                Integer.valueOf(R.drawable.puzzle3),
                Integer.valueOf(R.drawable.puzzle4),
                Integer.valueOf(R.drawable.puzzle5),
                Integer.valueOf(R.drawable.puzzle6),
                Integer.valueOf(R.drawable.puzzle7),
                Integer.valueOf(R.drawable.puzzle8),
                Integer.valueOf(R.drawable.puzzle9),
                Integer.valueOf(R.drawable.puzzle10),
                Integer.valueOf(R.drawable.puzzle11),

        }));
        adapter.setOnItemClickListener(new PuzzleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int i, int intRes) {
                Intent intent = new Intent(PuzzleList.this, Puzzlegame.class);
                intent.putExtra("selectedPuzzleImage", intRes);
                startActivity(intent);
            }
        });
        this.binding.puzzleList.setLayoutManager(new GridLayoutManager(this, 2));
        this.binding.puzzleList.setAdapter(adapter);

    }

}
