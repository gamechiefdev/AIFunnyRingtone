package com.gamechief.aifunnyringtone.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.gamechief.aifunnyringtone.Interfaces.OnClick;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.ShowGif;

import java.util.List;
import pl.droidsonroids.gif.GifDrawable;

public class HorizonRVAdapterGif extends RecyclerView.Adapter<HorizonRVAdapterGif.ViewHolder> {
    private List<GifDrawable> list;
    private OnClick listener;

    public HorizonRVAdapterGif(List<GifDrawable> list2, OnClick listener2) {
        this.list = list2;
        this.listener = listener2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView im;

        public ViewHolder(View itemView) {
            super(itemView);
            this.im = (ImageView) itemView.findViewById(R.id.horizontal_im);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_rv_sample, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GifDrawable listItem = this.list.get(position);
        Glide.with(holder.itemView.getContext()).load((Drawable) listItem).into(holder.im);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemPosition(position);
                Glide.with(holder.itemView.getContext()).load((Drawable) listItem).into((ImageView) ((ShowGif) holder.itemView.getContext()).findViewById(R.id.imageView9));
                Log.d("valueposition", "onBindViewHolder: " + listItem);

            }
        });
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$0$com-gamechief-funnyringtone-adapters-HorizonRVAdapterGif  reason: not valid java name */


    public int getItemCount() {
        if (this.list != null) {
            return this.list.size();
        }
        return 0;
    }
}
