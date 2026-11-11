package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.models.PromptItem;
import com.gamechief.aifunnyringtone.R;
import java.util.List;

public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.ViewHolder> {
    private Context context;
    private List<PromptItem> list;
    private OnPromptClickListener listener;
    private String userName;

    public interface OnPromptClickListener {
        void onPromptClick(PromptItem promptItem);
    }

    public PromptAdapter(Context context2, List<PromptItem> list2, String userName2, OnPromptClickListener listener2) {
        this.context = context2;
        this.list = list2;
        this.userName = userName2;
        this.listener = listener2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.calling_prompt_itemlayout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        PromptItem item = this.list.get(position);
        holder.promptText.setText(item.getFullText(this.userName));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPromptClick(item);
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$0$com-gamechief-funnyringtone-adapters-PromptAdapter  reason: not valid java name */

    public int getItemCount() {
        return this.list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView promptText;

        public ViewHolder(View itemView) {
            super(itemView);
            this.promptText = (TextView) itemView.findViewById(R.id.getPrompt);
        }
    }
}
