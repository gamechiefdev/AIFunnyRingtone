package com.gamechief.aifunnyringtone.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.databinding.HistoryItemlayoutBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;

import java.io.File;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public final List<SoundModel> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SoundModel soundModel);
    }

    public HistoryAdapter(Context context2, List<SoundModel> list2, OnItemClickListener listener2) {
        this.list = list2;
        this.listener = listener2;
        this.context = context2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(HistoryItemlayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(this.list.get(position), this.listener, position);
    }

    public int getItemCount() {
        return this.list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final HistoryItemlayoutBinding binding;

        ViewHolder(HistoryItemlayoutBinding binding2) {
            super(binding2.getRoot());
            this.binding = binding2;
        }

        /* access modifiers changed from: package-private */
        public void bind(SoundModel item, OnItemClickListener listener, int position) {
            this.binding.numbers.setText(item.getNumber());
            this.binding.namesR.setText(item.getName());
            this.binding.durationR.setText(item.getDuration());
            this.binding.wallImage.setImageResource(item.getImageResId());
            this.binding.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(HistoryAdapter.this.context)
                            .setTitle("Delete Recording")
                            .setMessage("Are you sure you want to delete this recording permanently?").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (item.getFilePath() != null) {
                                        File f = new File(item.getFilePath());
                                        if (f.exists()) {
                                            f.delete();
                                        }
                                    }
                                    
                                    // Identify if it's from normal history or trimmed history
                                    List<SoundModel> fullHistory = HistoryStorage.loadHistory(context);
                                    List<SoundModel> trimmedHistory = HistoryStorage.loadTrimmedHistory(context);
                                    
                                    boolean removedFromFull = false;
                                    for(int i=0; i<fullHistory.size(); i++) {
                                        if(fullHistory.get(i).getFilePath() != null && fullHistory.get(i).getFilePath().equals(item.getFilePath())) {
                                            fullHistory.remove(i);
                                            HistoryStorage.saveHistory(context, fullHistory);
                                            removedFromFull = true;
                                            break;
                                        }
                                    }
                                    
                                    if(!removedFromFull) {
                                        for(int i=0; i<trimmedHistory.size(); i++) {
                                            if(trimmedHistory.get(i).getFilePath() != null && trimmedHistory.get(i).getFilePath().equals(item.getFilePath())) {
                                                trimmedHistory.remove(i);
                                                HistoryStorage.saveFullTrimmedList(context, trimmedHistory);
                                                break;
                                            }
                                        }
                                    }

                                    // Clear Recent if it matches the deleted item
                                    SoundModel recent = HistoryStorage.loadRecent(context);
                                    if (recent != null && recent.getFilePath() != null && recent.getFilePath().equals(item.getFilePath())) {
                                        HistoryStorage.clearRecent(context);
                                    }

                                    int pos = getAdapterPosition();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        HistoryAdapter.this.list.remove(pos);
                                        HistoryAdapter.this.notifyItemRemoved(pos);
                                    }
                                }
                            })
                            .setNegativeButton("No", (DialogInterface.OnClickListener) null)
                            .show();

                }
            });
            this.binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }

    }
}
