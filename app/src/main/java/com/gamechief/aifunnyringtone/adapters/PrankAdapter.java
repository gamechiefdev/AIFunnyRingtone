package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.models.PrankModel;
import com.gamechief.aifunnyringtone.R;

import java.util.ArrayList;

public class PrankAdapter extends RecyclerView.Adapter<PrankAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PrankModel> list;
    private OnPrankClickListener listener;

    public interface OnPrankClickListener {
        void onPrankClick(PrankModel prankModel, int i);
    }

    public PrankAdapter(Context context2, ArrayList<PrankModel> list2, OnPrankClickListener listener2) {
        this.context = context2;
        this.list = list2;
        this.listener = listener2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.prankcall_itemlayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        PrankModel model = this.list.get(position);
        holder.imageView.setImageResource(model.getImage());
        holder.textView.setText(model.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPrankClick(model, holder.getAdapterPosition());
                }
            }
        });

    }

    public int getItemCount() {
        return this.list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.prankImg);
            this.textView = (TextView) itemView.findViewById(R.id.prankTxt);
        }
    }
}
