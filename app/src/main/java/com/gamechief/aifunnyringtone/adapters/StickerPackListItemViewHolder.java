package com.gamechief.aifunnyringtone.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;


public class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {
    View container;
    TextView filesizeView;
    TextView publisherView;
    ImageView stickImage;
    TextView titleView;

    StickerPackListItemViewHolder(View itemView) {
        super(itemView);
        this.container = itemView;
        this.titleView = (TextView) itemView.findViewById(R.id.sticker_pack_title);
        this.publisherView = (TextView) itemView.findViewById(R.id.sticker_pack_publisher);
        this.filesizeView = (TextView) itemView.findViewById(R.id.sticker_pack_filesize);
        this.stickImage = (ImageView) itemView.findViewById(R.id.stickImg);
    }
}
