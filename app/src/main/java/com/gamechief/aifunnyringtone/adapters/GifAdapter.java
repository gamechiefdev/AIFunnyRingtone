package com.gamechief.aifunnyringtone.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.gamechief.aifunnyringtone.activities.ShowGif;
import com.gamechief.aifunnyringtone.R;
import java.util.List;
import pl.droidsonroids.gif.GifDrawable;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.GifViewHolder> {
    private final List<GifDrawable> gifList;

    public GifAdapter(List<GifDrawable> gifList2) {
        this.gifList = gifList2;
    }

    public GifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GifViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gif_sample_item, parent, false));
    }

    public void onBindViewHolder(final GifViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load((Drawable) this.gifList.get(position)).into(holder.gifImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ShowGif.class);
                intent.putExtra("selectedAnimation", holder.getAdapterPosition());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return this.gifList.size();
    }

    public class GifViewHolder extends RecyclerView.ViewHolder {
        final ImageView gifImageView;

        public GifViewHolder(View itemView) {
            super(itemView);
            this.gifImageView = (ImageView) itemView.findViewById(R.id.gif_sample_image);
        }
    }
}
