package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.models.SoundCategoryModel;
import com.gamechief.aifunnyringtone.R;
import java.util.ArrayList;

public class SoundCategoryAdapter extends RecyclerView.Adapter<SoundCategoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SoundCategoryModel> list;
    private OnCategoryClick listener;

    public interface OnCategoryClick {
        void onClick(SoundCategoryModel soundCategoryModel);
    }

    public SoundCategoryAdapter(Context context2, ArrayList<SoundCategoryModel> list2, OnCategoryClick listener2) {
        this.context = context2;
        this.list = list2;
        this.listener = listener2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.sound_listitem, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        SoundCategoryModel model = this.list.get(position);
        holder.title.setText(model.title);
        holder.icon.setImageResource(model.icon);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(model);
                }
            }
        });
    }



    public int getItemCount() {
        return this.list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ConstraintLayout root;
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            this.root = (ConstraintLayout) itemView.findViewById(R.id.goFunny);
            this.icon = (ImageView) itemView.findViewById(R.id.imageView);
            this.title = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
