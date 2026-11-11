package com.gamechief.aifunnyringtone.PuzzleGame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.PuzzleGame.Puzzlegame;
import com.gamechief.aifunnyringtone.PuzzleGame.models.Pieces;
import com.gamechief.aifunnyringtone.R;

import java.util.ArrayList;
import java.util.List;

public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder> {
    private Context context;
    private int mPreviousPosition = 0;
    private List<Pieces> piecesList;

    public PuzzleAdapter(Context context2, List<Pieces> piecesList2) {
        this.context = context2;
        this.piecesList = piecesList2;
    }

    public PuzzleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PuzzleViewHolder(LayoutInflater.from(this.context).inflate(R.layout.puzzle_item, parent, false));
    }

    public void onBindViewHolder(PuzzleViewHolder holder, int position) {
        Pieces piece = this.piecesList.get(position);
        holder.imageView.setImageBitmap(piece.getOriginalResource());
        holder.imageView.setTag(piece.getpX() + "," + piece.getpY());
        holder.imageView.setOnLongClickListener(new Puzzlegame.MyClickListener());
        holder.imageView.setVisibility(View.VISIBLE); // Ensure it's visible when bound/recycled
        animationMethod(holder, position);
    }

    public int getItemCount() {
        return this.piecesList.size();
    }

    public static class PuzzleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PuzzleViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public void updateList(List<Pieces> newList) {
        this.piecesList = newList;
        notifyDataSetChanged();
    }

    private void animationMethod(PuzzleViewHolder holder, int position) {
        if (position > this.mPreviousPosition) {
            AnimationUtils.animate(holder, true);
        } else {
            AnimationUtils.animate(holder, false);
        }
        this.mPreviousPosition = position;
    }
}
