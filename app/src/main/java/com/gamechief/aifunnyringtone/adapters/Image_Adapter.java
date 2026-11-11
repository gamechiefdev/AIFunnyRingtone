package com.gamechief.aifunnyringtone.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.gamechief.aifunnyringtone.models.ImageModel;
import com.gamechief.aifunnyringtone.R;

import java.util.ArrayList;

public class Image_Adapter extends RecyclerView.Adapter<Image_Adapter.ViewHolder> {
    Context context;
    ArrayList<ImageModel> imageList;
    private onCardClickListener onCardClickListener;
    private int selectedPosition = -1;

    public interface onCardClickListener {
        void onCardClick(int i);
    }

    public Image_Adapter(Context context2, ArrayList<ImageModel> imageList2, onCardClickListener onCardClickListener2) {
        this.imageList = imageList2;
        this.onCardClickListener = onCardClickListener2;
        this.context = context2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_itemlayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
    /* renamed from: lambda$onBindViewHolder$0$com-gamechief-funnyringtone-adapters-Image_Adapter  reason: not valid java name */
    public /* synthetic */ void m51lambda$onBindViewHolder$0$comgamechieffunnyringtoneadaptersImage_Adapter(int position, View v) {
        if (this.onCardClickListener != null) {
            this.onCardClickListener.onCardClick(position);
        }
    }

    public int getItemCount() {
        return this.imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.wallImage);
        }
    }
}
