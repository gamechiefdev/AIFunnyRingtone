package com.gamechief.aifunnyringtone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.WallpaperPreview;
import com.gamechief.aifunnyringtone.adapters.Wall_Adapter;
import com.gamechief.aifunnyringtone.databinding.FragmentFavouriteBinding;
import com.gamechief.aifunnyringtone.models.BgModel;

import java.util.ArrayList;

public class Favourite extends Fragment {
    Wall_Adapter adapter;
    FragmentFavouriteBinding binding;
    ArrayList<BgModel> imageListBg = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.imageListBg = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        this.imageListBg = new ArrayList<>();
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
            this.adapter = new Wall_Adapter(getContext(), this.imageListBg, v -> {
                Intent intent = new Intent(getContext(), WallpaperPreview.class);
                intent.putExtra("wallpaper", this.imageListBg.get(v).getImage());
                Log.d("getWallpaper", "onCardClick: " + this.imageListBg.get(v));
                startActivity(intent);
            });
            StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            this.binding.wallList.setAdapter(this.adapter);
            this.binding.wallList.setLayoutManager(gridLayoutManager);
        }
        return this.binding.getRoot();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-gamechief-funnyringtone-fragments-Favourite  reason: not valid java name */

}
