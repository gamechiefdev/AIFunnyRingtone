package com.gamechief.aifunnyringtone.adapters;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.StickerPack.StickerPack;
import com.gamechief.aifunnyringtone.StickerPack.StickerPackLoader;


public class StickerPreviewAdapter extends RecyclerView.Adapter<StickerPreviewViewHolder> {
    private int cellLimit = 0;
    private int cellPadding;
    private final int cellSize;
    private final int errorResource;
    private final LayoutInflater layoutInflater;
    private StickerPack stickerPack;

    public StickerPreviewAdapter(LayoutInflater layoutInflater2, int errorResource2, int cellSize2, int cellPadding2, StickerPack stickerPack2) {
        this.cellSize = cellSize2;
        this.cellPadding = cellPadding2;
        this.layoutInflater = layoutInflater2;
        this.errorResource = errorResource2;
        this.stickerPack = stickerPack2;
    }

    public StickerPreviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        StickerPreviewViewHolder vh = new StickerPreviewViewHolder(this.layoutInflater.inflate(R.layout.sticker_image, viewGroup, false));
        ViewGroup.LayoutParams layoutParams = vh.stickerPreviewView.getLayoutParams();
        layoutParams.height = this.cellSize;
        layoutParams.width = this.cellSize;
        vh.stickerPreviewView.setLayoutParams(layoutParams);
        vh.stickerPreviewView.setPadding(this.cellPadding, this.cellPadding, this.cellPadding, this.cellPadding);
        return vh;
    }

    public void onBindViewHolder(StickerPreviewViewHolder holder, int position) {
        holder.stickerPreviewView.setImageResource(this.errorResource);
        if (this.stickerPack.getStickers() == null || position >= this.stickerPack.getStickers().size() || this.stickerPack.getStickers().get(position) == null) {
            holder.stickerPreviewView.setImageResource(this.errorResource);
            holder.stickerPreviewView.setOnClickListener((View.OnClickListener) null);
            return;
        }
        String imageFileName = this.stickerPack.getStickers().get(position).imageFileName;
        ((RequestBuilder)
                Glide.with(holder.stickerPreviewView.getContext())
                        .load(StickerPackLoader.getStickerAssetUri(this.stickerPack.identifier, imageFileName)).
                        error(this.errorResource)).
                into(holder.stickerPreviewView);

        holder.stickerPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                showStickerDialog(context, stickerPack.identifier, imageFileName);
            }
        });

    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$0$com-gamechief-funnyringtone-adapters-StickerPreviewAdapter  reason: not valid java name */


    private void showStickerDialog(Context context, String identifier, String imageFileName) {
        ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setImageURI(StickerPackLoader.getStickerAssetUri(identifier, imageFileName));
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Sticker Preview")
                .setView(imageView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();    }

    public int getItemCount() {
        int numberOfPreviewImagesInPack = this.stickerPack.getStickers().size();
        if (this.cellLimit > 0) {
            return Math.min(numberOfPreviewImagesInPack, this.cellLimit);
        }
        return numberOfPreviewImagesInPack;
    }
}
