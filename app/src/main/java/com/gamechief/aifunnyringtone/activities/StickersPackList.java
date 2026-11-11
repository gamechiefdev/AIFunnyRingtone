package com.gamechief.aifunnyringtone.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.StickerPack.StickerPack;
import com.gamechief.aifunnyringtone.StickerPack.StickerPackLoader;
import com.gamechief.aifunnyringtone.StickerPack.WhitelistCheck;
import com.gamechief.aifunnyringtone.adapters.StickerPackListAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityStickersPackListBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StickersPackList extends ParentActivity {
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 1;
    private StickerPackListAdapter allStickerPacksListAdapter;
    private ActivityStickersPackListBinding binding;
    private GridLayoutManager packLayoutManager;
    /* access modifiers changed from: private */
    public List<StickerPack> stickerPackList = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityStickersPackListBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(onClickListener -> onBackPressed());
        /* UI only: Pinterest-style two column board grid (was a plain vertical list) */
        this.packLayoutManager = new GridLayoutManager(this, 2);
        this.binding.packList.setLayoutManager(this.packLayoutManager);
        new LoadStickerPacksTask(this).execute(new Void[0]);
    }





    /* access modifiers changed from: private */
    public void showStickerPackList(List<StickerPack> stickerPackList2) {
        if (stickerPackList2 != null && !stickerPackList2.isEmpty()) {
            this.allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList2, this, new StickerPackListAdapter.OnAddButtonClickedListener() {
                @Override
                public void onAddButtonClicked(StickerPack stickerPack, int i) {

                }
            });
            this.binding.packList.setAdapter(this.allStickerPacksListAdapter);
            this.binding.packList.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
            this.allStickerPacksListAdapter.notifyDataSetChanged();
        }
    }


    /* access modifiers changed from: private */
    public void recalculateColumnCount() {
        if (this.allStickerPacksListAdapter != null && this.binding.packList.getChildCount() != 0) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.binding.packList.getViewTreeObserver().isAlive()) {
            this.binding.packList.getViewTreeObserver().removeOnGlobalLayoutListener(this::recalculateColumnCount);
        }
    }

    private static class LoadStickerPacksTask extends AsyncTask<Void, Void, List<StickerPack>> {
        private final WeakReference<StickersPackList> activityRef;

        LoadStickerPacksTask(StickersPackList activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        /* access modifiers changed from: protected */
        public List<StickerPack> doInBackground(Void... voids) {
            StickersPackList activity = (StickersPackList) this.activityRef.get();
            if (activity == null) {
                return new ArrayList();
            }
            List<StickerPack> stickerPacks = new ArrayList<>();
            try {
                stickerPacks = StickerPackLoader.fetchStickerPacks(activity);
                for (StickerPack pack : stickerPacks) {
                    pack.setIsWhitelisted(WhitelistCheck.isWhitelisted(activity, pack.identifier));
                }
            } catch (Exception e) {
                Log.e("StickersPackList", "Error loading sticker packs", e);
            }
            return stickerPacks;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(List<StickerPack> packs) {
            StickersPackList activity = (StickersPackList) this.activityRef.get();
            if (activity != null) {
                activity.stickerPackList.clear();
                activity.stickerPackList.addAll(packs);
                activity.showStickerPackList(activity.stickerPackList);
            }
        }
    }
}
