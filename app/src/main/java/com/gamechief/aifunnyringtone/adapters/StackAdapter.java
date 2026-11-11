package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gamechief.aifunnyringtone.R;
import com.google.android.material.card.MaterialCardView;
import java.util.Arrays;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;

public class StackAdapter extends RecyclerView.Adapter<StackAdapter.ViewHolder> {
    List<Integer> cardColors = Arrays.asList(new Integer[]{Integer.valueOf(Color.parseColor("#FFCDD2")), Integer.valueOf(Color.parseColor("#C8E6C9")), Integer.valueOf(Color.parseColor("#BBDEFB")), Integer.valueOf(Color.parseColor("#FFF9C4")), Integer.valueOf(Color.parseColor("#D1C4E9")), Integer.valueOf(Color.parseColor("#F2D2C8"))});
    private Context context;
    private int itemLayout;
    private List<String> numberWord;
    private TextToSpeech textToSpeech;

    public StackAdapter(List<String> word, int resource, Context context2, TextToSpeech textToSpeech2) {
        this.numberWord = word;
        this.itemLayout = resource;
        this.context = context2;
        this.textToSpeech = textToSpeech2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.itemLayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = this.numberWord.get(position);
        holder.textView.setText(text);
        holder.cardView.setCardBackgroundColor(this.cardColors.get(position % this.cardColors.size()).intValue());
        holder.countFlip.setText((position + 1) + InternalZipConstants.ZIP_FILE_SEPARATOR + getItemCount());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech != null && text != null && !text.isEmpty()) {
                    textToSpeech.speak(text, 0, null,  null);
                }
            }
        });
    }


    public int getItemCount() {
        if (this.numberWord == null) {
            return 0;
        }
        return this.numberWord.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView countFlip;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.txtList);
            this.countFlip = (TextView) itemView.findViewById(R.id.countFlip);
            this.cardView = (MaterialCardView) itemView.findViewById(R.id.cardview);
        }
    }
}
