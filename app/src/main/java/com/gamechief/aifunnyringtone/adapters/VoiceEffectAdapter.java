package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.models.VoiceEffect;
import com.gamechief.aifunnyringtone.R;
import java.util.List;

public class VoiceEffectAdapter extends RecyclerView.Adapter<VoiceEffectAdapter.ViewHolder> {
    private final Context context;
    private final List<VoiceEffect> effects;
    private final OnEffectClickListener listener;

    public interface OnEffectClickListener {
        void onEffectClick(VoiceEffect voiceEffect);
    }

    public VoiceEffectAdapter(Context context2, List<VoiceEffect> effects2, OnEffectClickListener listener2) {
        this.context = context2;
        this.effects = effects2;
        this.listener = listener2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.voice_itemlayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        VoiceEffect effect = this.effects.get(position);
        holder.name.setText(effect.getName());
        holder.icon.setImageResource(effect.getIconRes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEffectClick(effect);
            }
        });
    }



    public int getItemCount() {
        return this.effects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.effectIcon);
            this.name = (TextView) itemView.findViewById(R.id.effectName);
        }
    }
}
