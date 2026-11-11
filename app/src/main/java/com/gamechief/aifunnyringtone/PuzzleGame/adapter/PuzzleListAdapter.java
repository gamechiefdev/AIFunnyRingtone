package com.gamechief.aifunnyringtone.PuzzleGame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;

import java.util.List;

public class PuzzleListAdapter extends RecyclerView.Adapter<PuzzleListAdapter.ViewHolder> {
    private final Context context;
    private final List<Integer> imageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int i, int i2);
    }

    public PuzzleListAdapter(Context context2, List<Integer> imageList2) {
        this.context = context2;
        this.imageList = imageList2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.puzzle_itemlayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        int imageRes = this.imageList.get(position).intValue();
        holder.imageView.setImageResource(imageRes);
        holder.itemView.setOnClickListener(v -> {
            if (this.listener != null) {
                this.listener.onItemClick(position, imageRes);
            }
        });
    }



    public int getItemCount() {
        return this.imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.puzzleImg);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener2) {
        this.listener = listener2;
    }
}
