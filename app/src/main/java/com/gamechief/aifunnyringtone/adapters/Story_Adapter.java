package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.gamechief.aifunnyringtone.R;
import java.util.ArrayList;

public class Story_Adapter extends RecyclerView.Adapter<Story_Adapter.ViewHolder> {
    Context context;
    ArrayList<Integer> imageList;
    private onCardClickListener onCardClickListener;
    private int selectedPosition = -1;
    ArrayList<String> textList;

    public interface onCardClickListener {
        void onCardClick(int i);
    }

    public Story_Adapter(Context context2, ArrayList<Integer> imageList2, ArrayList<String> textList2, onCardClickListener onCardClickListener2) {
        this.imageList = imageList2;
        this.textList = textList2;
        this.onCardClickListener = onCardClickListener2;
        this.context = context2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.story_itemlayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(this.imageList.get(position)).into(holder.imageView);
        holder.textView6.setText(this.textList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClickListener.onCardClick(position);
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });
    }



    public int getItemCount() {
        return this.imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView6;
        ImageView tickImg;
        TextView time;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.bgImage);
            this.tickImg = (ImageView) itemView.findViewById(R.id.tickImg);
            this.view = itemView.findViewById(R.id.view00);
            this.textView6 = (TextView) itemView.findViewById(R.id.textView6);
        }
    }
}
