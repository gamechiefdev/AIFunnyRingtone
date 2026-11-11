package com.gamechief.aifunnyringtone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.activities.PlaySounds;
import com.gamechief.aifunnyringtone.adapters.HistoryAdapter;
import com.gamechief.aifunnyringtone.databinding.FragmentHistroyBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;

import java.util.ArrayList;
import java.util.List;

public class Histroy extends Fragment {
    private HistoryAdapter adapter;
    private FragmentHistroyBinding binding;
    private List<SoundModel> historyList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.historyList = new ArrayList();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentHistroyBinding.inflate(inflater, container, false);
        this.adapter = new HistoryAdapter(getContext(), this.historyList, v -> {
            Intent intent = new Intent(getContext(), PlaySounds.class);
            intent.putExtra("soundUrl", v.getFilePath());
            intent.putExtra("soundName", v.getName());
            startActivity(intent);
        });
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.binding.recyclerView.setAdapter(this.adapter);
        return this.binding.getRoot();
    }

    private void loadHistory() {
        List<SoundModel> fullHistory = HistoryStorage.loadHistory(getContext());
        List<SoundModel> trimmedHistory = HistoryStorage.loadTrimmedHistory(getContext());
        this.historyList.clear();
        if (fullHistory != null && !fullHistory.isEmpty()) {
            this.historyList.addAll(fullHistory);
        }
        if (trimmedHistory != null && !trimmedHistory.isEmpty()) {
            this.historyList.addAll(trimmedHistory);
        }
        this.adapter.notifyDataSetChanged();
    }

    public void onResume() {
        super.onResume();
        loadHistory();
    }
}
