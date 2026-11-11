package com.gamechief.aifunnyringtone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;


import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.VoiceEffectAdapter;
import com.gamechief.aifunnyringtone.databinding.FragmentLiveSpeakerBinding;
import com.gamechief.aifunnyringtone.models.VoiceEffect;

import java.util.ArrayList;
import java.util.List;

public class LiveSpeaker extends Fragment {
    private FragmentLiveSpeakerBinding binding;
    private VoiceEffectEngine engine;
    private final ActivityResultLauncher<String> micPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (!granted) {
                            Toast.makeText(
                                    requireContext(),
                                    "Microphone permission required",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Permission granted, tap again",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
    private VoiceEffect selectedVoice;
    private List<VoiceEffect> voices;

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$com-gamechief-funnyringtone-fragments-LiveSpeaker  reason: not valid java name */
    public /* synthetic */ void m14lambda$new$0$comgamechieffunnyringtonefragmentsLiveSpeaker(Boolean granted) {
        if (!granted.booleanValue()) {
            Toast.makeText(requireContext(), "Microphone permission required", 0).show();
        } else {
            Toast.makeText(requireContext(), "Permission granted, tap again", 0).show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentLiveSpeakerBinding.inflate(inflater, container, false);
        this.engine = new VoiceEffectEngine(requireContext());
        setupVoiceEffects();
        updateIdleUI();
        this.binding.liveSpeaker.setOnClickListener(v -> {

            if (!checkPermission()) {
                this.micPermission.launch("android.permission.RECORD_AUDIO");
            } else if (!this.engine.isRunning()) {
                this.engine.setEffect(selectedVoice);
                this.engine.start();
                this.binding.liveSpeaker.setImageResource(R.drawable.stop_live);
                updateRecText("🎤 Live Mic ON");
            } else {
                this.engine.stop();
                this.binding.liveSpeaker.setImageResource(R.drawable.start_live);
                updateRecText("Hold the mic to Live Speaker");
            }

        });
        return this.binding.getRoot();
    }

    private void setupVoiceEffects() {
        this.voices = new ArrayList();
        this.voices.add(new VoiceEffect("Normal", 1.0f, 1.0f, R.drawable.normal));
        this.voices.add(new VoiceEffect("Chipmunk", 1.3f, 1.5f, R.drawable.chipmunk));
        this.voices.add(new VoiceEffect("Monster", 0.7f, 0.8f, R.drawable.monster));
        this.voices.add(new VoiceEffect("Fast", 1.5f, 1.2f, R.drawable.fast));
        this.voices.add(new VoiceEffect("Slow", 0.6f, 0.7f, R.drawable.slow));
        this.voices.add(new VoiceEffect("Alien", 1.1f, 1.4f, R.drawable.alien));
        this.voices.add(new VoiceEffect("Demon", 0.5f, 0.6f, R.drawable.demon));
        this.voices.add(new VoiceEffect("Robot", 1.0f, 1.0f, R.drawable.robot));
        this.voices.add(new VoiceEffect("Echo", 1.0f, 1.0f, R.drawable.echo));
        this.selectedVoice = this.voices.get(0);
        VoiceEffectAdapter adapter = new VoiceEffectAdapter(requireContext(), this.voices, v -> {
            this.selectedVoice = v;
            this.engine.setEffect(v);
            updateRecText("Hold the mic to Live Speaker");
        });
        this.binding.effectList.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        this.binding.effectList.setAdapter(adapter);
    }


    private void updateIdleUI() {
        this.binding.liveSpeaker.setImageResource(R.drawable.start_live);
        updateRecText("Hold the mic to Live Speaker");
    }

    private void updateRecText(String status) {
        if (this.binding != null && this.selectedVoice != null) {
            this.binding.recTxt.setText(status + " (" + this.selectedVoice.getName() + ")");
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), "android.permission.RECORD_AUDIO") == 0;
    }

    public void onDestroyView() {
        if (this.engine != null) {
            this.engine.stop();
        }
        this.binding = null;
        super.onDestroyView();
    }
}
