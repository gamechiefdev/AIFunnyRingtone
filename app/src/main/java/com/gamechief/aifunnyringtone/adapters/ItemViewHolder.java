package com.gamechief.aifunnyringtone.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.CustomUiViews.CustomPopupButton;
import com.gamechief.aifunnyringtone.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    ImageView btn2_save;
    ImageView btn3_copy;
    ImageView btn4_share;
    CustomPopupButton btn5_next;
    ImageView btn6_list;
    ImageView btn7_load;
    ImageView btn8_edit;
    ImageView imageView;
    RelativeLayout relativeLayout;
    TextView textView;
    RelativeLayout view_waterMark;

    public ItemViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView.findViewById(R.id.iv_row_vi);
        this.textView = (TextView) itemView.findViewById(R.id.tv_row_vi);
        this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_row_vi);
        this.view_waterMark = (RelativeLayout) itemView.findViewById(R.id.rel_water_mark);
        this.btn2_save = (ImageView) itemView.findViewById(R.id.btn2_save_row_vi);
        this.btn3_copy = (ImageView) itemView.findViewById(R.id.btn3_copy_row_vi);
        this.btn4_share = (ImageView) itemView.findViewById(R.id.btn4_share_row_vi);
        this.btn5_next = (CustomPopupButton) itemView.findViewById(R.id.btn5_next_row_vi);
//        this.btn7_load = (ImageView) itemView.findViewById(R.id.btn7_load_row_vi);
    }
}
