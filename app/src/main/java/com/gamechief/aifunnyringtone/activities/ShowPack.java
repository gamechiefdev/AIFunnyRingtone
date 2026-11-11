package com.gamechief.aifunnyringtone.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.gamechief.aifunnyringtone.adapters.StickerPreviewAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;

import com.gamechief.aifunnyringtone.databinding.ActivityShowPackBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.lang.ref.WeakReference;

public class ShowPack extends AddStickerPackActivity {
    public static final String EXTRA_SHOW_UP_BUTTON = "show_up_button";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_DATA = "sticker_pack";
    public static final String EXTRA_STICKER_PACK_EMAIL = "sticker_pack_email";
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";
    public static final String EXTRA_STICKER_PACK_PRIVACY_POLICY = "sticker_pack_privacy_policy";
    public static final String EXTRA_STICKER_PACK_TRAY_ICON = "sticker_pack_tray_icon";
    public static final String EXTRA_STICKER_PACK_WEBSITE = "sticker_pack_website";
    ActivityShowPackBinding binding;
    /* access modifiers changed from: private */
    public View divider;
    private final RecyclerView.OnScrollListener dividerScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            updateDivider(recyclerView);
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateDivider(recyclerView);
        }

        private void updateDivider(RecyclerView recyclerView) {
            int i = 0;
            boolean showDivider = recyclerView.computeVerticalScrollOffset() > 0;
            if (ShowPack.this.divider != null) {
                View r2 = ShowPack.this.divider;
                if (!showDivider) {
                    i = 4;
                }
                r2.setVisibility(View.VISIBLE);
            }
        }
    };
    Intent intent = getIntent();
    private GridLayoutManager layoutManager;
    private int numColumns;
    private final ViewTreeObserver.OnGlobalLayoutListener pageLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            ShowPack.this.setNumColumns(ShowPack.this.binding.recyclerView.getWidth() / ShowPack.this.binding.recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size));
        }
    };
    private StickerPack stickerPack;
    private StickerPreviewAdapter stickerPreviewAdapter;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityShowPackBinding.inflate(getLayoutInflater());
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
                ShowPack.this.onBackPressed();
            }
        });
        boolean showUpButton = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        this.stickerPack = null;
        this.intent = getIntent();
        if (this.intent != null) {
            try {
                this.stickerPack = (StickerPack) this.intent.getParcelableExtra(EXTRA_STICKER_PACK_DATA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.stickerPack == null) {
            finish();
            return;
        }
        this.layoutManager = new GridLayoutManager(this, 4);
        this.binding.recyclerView.setLayoutManager(this.layoutManager);
        this.binding.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this.pageLayoutListener);
        this.binding.recyclerView.addOnScrollListener(this.dividerScrollListener);
        if (this.stickerPreviewAdapter == null) {
            this.stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), this.stickerPack);
            this.binding.recyclerView.setAdapter(this.stickerPreviewAdapter);
        }
        this.binding.packCount.setText(this.stickerPack.name);
        this.binding.packName.setText(this.stickerPack.publisher);
        this.binding.stickImg.setImageURI(StickerPackLoader.getStickerAssetUri(this.stickerPack.identifier, this.stickerPack.trayImageFile));
        this.binding.stickSize.setText(Formatter.formatShortFileSize(this, this.stickerPack.getTotalSize()));
        binding.addButton.setOnClickListener(v -> addStickerPackToWhatsApp(stickerPack.identifier, stickerPack.name));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
            getSupportActionBar().setTitle(showUpButton ? R.string.title_activity_sticker_pack_details_multiple_pack : R.string.title_activity_sticker_pack_details_single_pack);
        }
    }



    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-gamechief-funnyringtone-activities-ShowPack  reason: not valid java name */


    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /* access modifiers changed from: private */
    public void setNumColumns(int numColumns2) {
        if (this.numColumns != numColumns2) {
            this.layoutManager.setSpanCount(numColumns2);
            this.numColumns = numColumns2;
            if (this.stickerPreviewAdapter != null) {
                this.stickerPreviewAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        this.whiteListCheckAsyncTask.execute(new StickerPack[]{this.stickerPack});
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.whiteListCheckAsyncTask != null && !this.whiteListCheckAsyncTask.isCancelled()) {
            this.whiteListCheckAsyncTask.cancel(true);
        }
    }

    /* access modifiers changed from: private */
    public void updateAddUI(Boolean isWhitelisted) {
        boolean value = isWhitelisted != null && isWhitelisted;
        binding.addButton.setVisibility(
                value ? View.GONE : View.VISIBLE
        );
    }
    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, Boolean> {
        private final WeakReference<ShowPack> stickerPackDetailsActivityWeakReference;

        WhiteListCheckAsyncTask(ShowPack stickerPackListActivity) {
            this.stickerPackDetailsActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        /* access modifiers changed from: protected */
        public final Boolean doInBackground(StickerPack... stickerPacks) {
            StickerPack stickerPack = stickerPacks[0];
            ShowPack stickerPackDetailsActivity = (ShowPack) this.stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity == null) {
                return false;
            }
            return Boolean.valueOf(WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier));
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean isWhitelisted) {
            ShowPack stickerPackDetailsActivity = (ShowPack) this.stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity != null) {
                stickerPackDetailsActivity.updateAddUI(isWhitelisted);
            }
        }
    }
}
