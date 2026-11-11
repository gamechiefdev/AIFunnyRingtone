package com.gamechief.aifunnyringtone.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.DonkeyScreen;
import com.gamechief.aifunnyringtone.activities.PlayDonkeyVideo;
import com.gamechief.aifunnyringtone.models.donkeyModel;

import java.util.List;

public class DonkeyAdapter extends RecyclerView.Adapter<DonkeyAdapter.ViewHolder>{


    private List<donkeyModel> list;

    public DonkeyAdapter(List<donkeyModel> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public DonkeyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donkey_itemlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonkeyAdapter.ViewHolder holder, int position) {

        donkeyModel model = list.get(position);
        holder.imageView.setImageResource(model.getImagge());
        holder.textView.setText(model.getName());
        holder.textView2.setText(model.getDes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    donkeyModel model = list.get(adapterPosition);
                    Intent intent = new Intent(view.getContext(), PlayDonkeyVideo.class);
                    intent.putExtra("donkeyName", model.getName());
                    intent.putExtra("position", adapterPosition);
                    view.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        TextView textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.donkeyImg);
            textView = itemView.findViewById(R.id.donkeyTxt0);
            textView2 = itemView.findViewById(R.id.donkeyTxt1);

        }
    }
}
