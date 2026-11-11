package com.gamechief.aifunnyringtone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gamechief.aifunnyringtone.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class MemAdapter extends RecyclerView.Adapter<MemAdapter.MemViewHolder> {
    private OnItemClickListener listener;
    private List<String> memList;

    public interface OnItemClickListener {
        void onCloseClick(int i);
    }

    public MemAdapter(List<String> memList2, OnItemClickListener listener2) {
        this.memList = memList2;
        this.listener = listener2;
    }

    public MemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.member_itemlayout, parent, false));
    }

    public void onBindViewHolder(MemViewHolder holder, int position) {
        holder.memTxt.setText(this.memList.get(position));
        if (position % 2 == 0) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.yellow));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.green));
        }
        holder.memClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCloseClick(position);
                }
            }
        });
    }



    public int getItemCount() {
        return this.memList.size();
    }

    static class MemViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView memClose;
        TextView memTxt;

        public MemViewHolder(View itemView) {
            super(itemView);
            this.memTxt = (TextView) itemView.findViewById(R.id.memTxt);
            this.memClose = (ImageView) itemView.findViewById(R.id.memClose);
            this.cardView = (MaterialCardView) itemView.findViewById(R.id.cardView);
        }
    }
}
