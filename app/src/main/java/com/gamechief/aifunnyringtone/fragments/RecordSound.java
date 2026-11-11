package com.gamechief.aifunnyringtone.fragments;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.RecordGenerate;
import com.gamechief.aifunnyringtone.databinding.FragmentRecordSoundBinding;
import com.gamechief.aifunnyringtone.models.VoiceEffect;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RecordSound extends Fragment {
    private static final String TAG = "RecordSoundFragment";
    private String audioFilePath;
    /* access modifiers changed from: private */
    public FragmentRecordSoundBinding binding;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = true;
                for (Boolean b : result.values()) if (!b) granted = false;

                Log.d(TAG, "Permission result: " + result + ", granted=" + granted);

                if (granted) {
                    binding.startRec0.setEnabled(true);
                } else {
                    Toast.makeText(getActivity(), "Permissions are required!", Toast.LENGTH_SHORT).show();
                    binding.startRec0.setEnabled(false);
                }
            });    private VoiceEffect selectedEffect = null;
    /* access modifiers changed from: private */
    public long startTime = 0;
    /* access modifiers changed from: private */
    public Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private List<VoiceEffect> voices;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        this.binding = FragmentRecordSoundBinding.inflate(inflater, container, false);
        if (!checkPermissions()) {
            Log.d(TAG, "Permissions not granted, requesting...");
            this.permissionLauncher.launch(new String[]{"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"});
            this.binding.startRec0.setEnabled(false);
        } else {
            Log.d(TAG, "Permissions already granted");
            this.binding.startRec0.setEnabled(true);
        }
        this.binding.startRec0.setOnClickListener(v -> {
            Log.d(TAG, "startRec button clicked, isRecording=" + this.isRecording);
            if (!this.isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });
        return this.binding.getRoot();
    }



    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), "android.permission.RECORD_AUDIO") == 0;
    }

    private void setupRecordingControls() {
    }

    private void startRecording() {
        try {
            this.audioFilePath = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "record_" + System.currentTimeMillis() + ".m4a").getAbsolutePath();
            this.mediaRecorder = new MediaRecorder();
            this.mediaRecorder.setAudioSource(1);
            this.mediaRecorder.setOutputFormat(2);
            this.mediaRecorder.setAudioEncoder(3);
            this.mediaRecorder.setOutputFile(this.audioFilePath);
            this.mediaRecorder.prepare();
            this.mediaRecorder.start();
            this.isRecording = true;
            Log.d(TAG, "Recording started at path: " + this.audioFilePath);
            this.binding.startRec0.setImageResource(R.drawable.stop);
            this.binding.recordAnimm0.playAnimation();
            this.binding.recTxt0.setText("Recording...");
            startTimer();
            Toast.makeText(getActivity(), "Recording started", 0).show();
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "startRecording failed: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Recording failed: " + e.getMessage(), 1).show();
        }
    }

    private void stopRecording() {
        if (this.mediaRecorder != null) {
            try {
                this.mediaRecorder.stop();
                Log.d(TAG, "Recording stopped");
            } catch (RuntimeException e) {
                Log.e(TAG, "stopRecording failed: " + e.getMessage(), e);
            }
            this.mediaRecorder.release();
            this.mediaRecorder = null;
            this.isRecording = false;
            this.binding.startRec0.setImageResource(R.drawable.start_rec);
            this.binding.recordAnimm0.pauseAnimation();
            this.binding.recTxt0.setText("Hold the mic to record");
            stopTimer();
            Log.d(TAG, "File saved at: " + this.audioFilePath);
            Toast.makeText(getActivity(), "Recording saved", 0).show();
            if (this.audioFilePath != null) {
                Intent intent = new Intent(getActivity(), RecordGenerate.class);
                intent.putExtra("recorded_path", this.audioFilePath);
                startActivity(intent);
            }
        }
    }

    private void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.timerRunnable = new Runnable() {
            public void run() {
                int seconds = (int) ((System.currentTimeMillis() - RecordSound.this.startTime) / 1000);
                int minutes = seconds / 60;
                RecordSound.this.binding.countTime0.setText(String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(minutes / 60), Integer.valueOf(minutes % 60), Integer.valueOf(seconds % 60)}));
                RecordSound.this.timerHandler.postDelayed(this, 500);
            }
        };
        this.timerHandler.post(this.timerRunnable);
        Log.d(TAG, "Timer started");
    }

    private void stopTimer() {
        if (this.timerRunnable != null) {
            this.timerHandler.removeCallbacks(this.timerRunnable);
            this.binding.countTime0.setText("00:00:00");
            Log.d(TAG, "Timer stopped");
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
        if (this.isRecording) {
            stopRecording();
        }
        this.binding = null;
        Log.d(TAG, "onDestroyView called");
    }
}
