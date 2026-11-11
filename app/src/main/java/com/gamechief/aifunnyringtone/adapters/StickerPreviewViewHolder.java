package com.gamechief.aifunnyringtone.adapters;

import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;


public class StickerPreviewViewHolder extends RecyclerView.ViewHolder {
    public ImageView stickerPreviewView;

    StickerPreviewViewHolder(View itemView) {
        super(itemView);
        this.stickerPreviewView = (ImageView) itemView.findViewById(R.id.sticker_preview);
    }
}
