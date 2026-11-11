package com.gamechief.aifunnyringtone.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.StickerPack.StickerPack;
import com.gamechief.aifunnyringtone.StickerPack.StickerPackLoader;
import com.gamechief.aifunnyringtone.activities.ShowPack;


import java.util.List;

public class StickerPackListAdapter extends RecyclerView.Adapter<StickerPackListItemViewHolder> {
    Activity activity;
    private int maxNumberOfStickersInARow;
    private final OnAddButtonClickedListener onAddButtonClickedListener;
    StickerPack pack;
    private List<StickerPack> stickerPacks;

    public interface OnAddButtonClickedListener {
        void onAddButtonClicked(StickerPack stickerPack, int i);
    }

    public StickerPackListAdapter(List<StickerPack> stickerPacks2, Activity activity2, OnAddButtonClickedListener onAddButtonClickedListener2) {
        this.stickerPacks = stickerPacks2;
        this.onAddButtonClickedListener = onAddButtonClickedListener2;
        this.activity = activity2;
    }

    public StickerPackListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new StickerPackListItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sticker_itemlayout, viewGroup, false));
    }

    public void onBindViewHolder(StickerPackListItemViewHolder viewHolder, @SuppressLint("RecyclerView") int index) {
        this.pack = this.stickerPacks.get(index);
        viewHolder.filesizeView.setText(Formatter.formatShortFileSize(viewHolder.itemView.getContext(), this.pack.getTotalSize()));
        viewHolder.titleView.setText(this.pack.name);
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pack = stickerPacks.get(index);
                if (pack != null) {
                    Intent intent = new Intent(v.getContext(), ShowPack.class);
                    intent.putExtra(ShowPack.EXTRA_SHOW_UP_BUTTON, true);
                    intent.putExtra(ShowPack.EXTRA_STICKER_PACK_DATA, pack);
                    v.getContext().startActivity(intent);
                }
            }
        });
        if (this.pack.getStickers() == null || this.pack.getStickers().isEmpty()) {
            viewHolder.stickImage.setImageResource(R.drawable.sticker_error);
            return;
        }
        viewHolder.stickImage.setImageURI(StickerPackLoader.getStickerAssetUri(this.pack.identifier, this.pack.getStickers().get(0).getImageFileName()));
    }



    private void showStickerDialog(Context context, String identifier, String imageFileName) {
        ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(30, 30, 30, 30);
        imageView.setImageURI(StickerPackLoader.getStickerAssetUri(identifier, imageFileName));
        new AlertDialog.Builder(context)
                .setTitle("Sticker Preview")
                .setView(imageView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setBackground(View view, Drawable background) {
        view.setBackground(background);
    }

    public int getItemCount() {
        return this.stickerPacks.size();
    }

    public void setMaxNumberOfStickersInARow(int maxNumberOfStickersInARow2) {
        if (this.maxNumberOfStickersInARow != maxNumberOfStickersInARow2) {
            this.maxNumberOfStickersInARow = maxNumberOfStickersInARow2;
            notifyDataSetChanged();
        }
    }

    public void setStickerPackList(List<StickerPack> stickerPackList) {
        this.stickerPacks = stickerPackList;
    }
}
