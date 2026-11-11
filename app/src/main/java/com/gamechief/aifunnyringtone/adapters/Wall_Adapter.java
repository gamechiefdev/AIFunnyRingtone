package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.gamechief.aifunnyringtone.models.BgModel;
import com.gamechief.aifunnyringtone.R;

import java.util.ArrayList;

public class Wall_Adapter extends RecyclerView.Adapter<Wall_Adapter.ViewHolder> {
    Context context;
    ArrayList<BgModel> imageList;
    private onCardClickListener onCardClickListener;
    private int selectedPosition = -1;

    public interface onCardClickListener {
        void onCardClick(int i);
    }

    public Wall_Adapter(Context context2, ArrayList<BgModel> imageList2, onCardClickListener onCardClickListener2) {
        this.imageList = imageList2;
        this.onCardClickListener = onCardClickListener2;
        this.context = context2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bg_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(Integer.valueOf(this.imageList.get(position).getImage())).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardClickListener != null) {
                    onCardClickListener.onCardClick(position);
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$0$com-gamechief-funnyringtone-adapters-Wall_Adapter  reason: not valid java name */
    public /* synthetic */ void m65lambda$onBindViewHolder$0$comgamechieffunnyringtoneadaptersWall_Adapter(int position, View v) {
        if (this.onCardClickListener != null) {
            this.onCardClickListener.onCardClick(position);
        }
    }

    public int getItemCount() {
        return this.imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView tickImg;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.bgImage);
            this.view = itemView.findViewById(R.id.view00);
        }
    }
}
