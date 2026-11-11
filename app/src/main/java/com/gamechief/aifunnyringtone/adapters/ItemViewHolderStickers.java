package com.gamechief.aifunnyringtone.adapters;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gamechief.aifunnyringtone.R;

public class ItemViewHolderStickers extends RecyclerView.ViewHolder {
    View lockOverlay;
    TextView packSize;
    TextView tvSubTitle;
    TextView tvTitle;

    public ItemViewHolderStickers(View itemView) {
        super(itemView);
        this.tvTitle = (TextView) itemView.findViewById(R.id.packCount);
        this.tvSubTitle = (TextView) itemView.findViewById(R.id.packName);
        this.packSize = (TextView) itemView.findViewById(R.id.stickSize);
    }
}
