package com.gamechief.aifunnyringtone.myRingtone;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.gamechief.aifunnyringtone.databinding.CallingSaveItemlayoutBinding;
import com.gamechief.aifunnyringtone.myRingtone.PlaySaveRingtone;
import com.gamechief.aifunnyringtone.models.SavedRingtone;

import java.util.List;
import java.util.Locale;

public class SavedRingtoneAdapter extends RecyclerView.Adapter<SavedRingtoneAdapter.ViewHolder> {
    Context context;
    List<SavedRingtone> list;
    TextToSpeech tts;

    public SavedRingtoneAdapter(Context context2, List<SavedRingtone> list2) {
        this.context = context2;
        this.list = list2;
        this.tts = new TextToSpeech(context2, status -> {
            if (status != -1) {
                this.tts.setLanguage(Locale.US);
            }
        });
    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(CallingSaveItemlayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        SavedRingtone model = this.list.get(position);
        holder.binding.get.setText("My Name Ringtone");
        holder.binding.getPrompt.setText(model.getText());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, PlaySaveRingtone.class);
            intent.putExtra("ringtone", model.getText());
            this.context.startActivity(intent);

        });
    }



    public int getItemCount() {
        return this.list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CallingSaveItemlayoutBinding binding;

        ViewHolder(CallingSaveItemlayoutBinding binding2) {
            super(binding2.getRoot());
            this.binding = binding2;
        }
    }
}
