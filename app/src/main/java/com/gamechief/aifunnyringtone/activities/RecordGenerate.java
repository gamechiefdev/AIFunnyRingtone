package com.gamechief.aifunnyringtone.activities;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.VoiceEffectAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityRecordGenerateBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.gamechief.aifunnyringtone.models.VoiceEffect;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordGenerate extends ParentActivity {
    /* access modifiers changed from: private */
    public String audioFilePath;
    private AudioTrack audioTrack;
    ActivityRecordGenerateBinding binding;
    private boolean isMuted = false;
    private MediaPlayer originalPlayer;
    private int sampleRate = 44100;
    private VoiceEffect selectedEffect;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityRecordGenerateBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        this.audioFilePath = getIntent().getStringExtra("recorded_path");
        if (this.audioFilePath != null) {
            initOriginalPlayer();
        }

        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);

        setupRecordingControls();
        setupVoiceEffects();
        setupAds();
    }



    private void initOriginalPlayer() {
        try {
            this.originalPlayer = new MediaPlayer();
            this.originalPlayer.setDataSource(this.audioFilePath);
            this.originalPlayer.prepare();
            this.originalPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    binding.playPause.setImageResource(R.drawable.pause_mimi);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load audio", Toast.LENGTH_SHORT).show();
        }
    }



    private void setupAds() {
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void setupRecordingControls() {
        this.binding.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalPlayer != null) {
                    if (originalPlayer.isPlaying()) {
                        originalPlayer.pause();
                        binding.playPause.setImageResource(R.drawable.pause_mimi);
                        binding.lottie.pauseAnimation();
                        return;
                    }
                    originalPlayer.start();
                    binding.playPause.setImageResource(R.drawable.play_mini);
                    binding.lottie.playAnimation();
                }
            }
        });
        this.binding.simpleVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOriginalAgain();
                binding.simpleVoice.setImageResource(R.drawable.simple_mode);
                binding.changeVoice.setImageResource(R.drawable.change_mode);
            }
        });
        this.binding.changeVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.simpleVoice.setImageResource(R.drawable.simple_btn);
                binding.changeVoice.setImageResource(R.drawable.change_btn);
                if (selectedEffect != null) {
                    if (originalPlayer != null && originalPlayer.isPlaying()) {
                        originalPlayer.pause();
                        binding.playPause.setImageResource(R.drawable.pause_mimi);
                    }
                    playEffect(selectedEffect.getName());
                }
            }
        });
        this.binding.volOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMute();
            }
        });
        this.binding.save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(RecordGenerate.this, PlaySounds.class);
                intent.putExtra("soundUrl", RecordGenerate.this.audioFilePath);
                RecordGenerate.this.startActivity(intent);
                RecordGenerate.this.saveRecording();
            }
        });
    }





    private void toggleMute() {
        this.isMuted = !this.isMuted;
        float f = 0.0f;
        if (this.originalPlayer != null) {
            this.originalPlayer.setVolume(this.isMuted ? 0.0f : 1.0f, this.isMuted ? 0.0f : 1.0f);
        }
        if (this.audioTrack != null) {
            AudioTrack audioTrack2 = this.audioTrack;
            if (!this.isMuted) {
                f = 1.0f;
            }
            audioTrack2.setVolume(f);
        }
        this.binding.volOnOff.setImageResource(this.isMuted ? R.drawable.mute : R.drawable.volume);
    }

    private void playOriginalAgain() {
        if (this.audioTrack != null) {
            this.audioTrack.stop();
            this.audioTrack.release();
            this.audioTrack = null;
        }
        if (this.originalPlayer != null) {
            try {
                this.originalPlayer.stop();
            } catch (Exception e) {
            }
            this.originalPlayer.release();
            this.originalPlayer = null;
        }
        this.selectedEffect = null;
        initOriginalPlayer();
        this.originalPlayer.seekTo(0);
        this.originalPlayer.start();
        this.binding.playPause.setImageResource(R.drawable.play_mini);
        this.binding.lottie.playAnimation();
    }

    private void setupVoiceEffects() {
        List<VoiceEffect> voices = new ArrayList<>();
        voices.add(new VoiceEffect("Normal", 1.0f, 1.0f, R.drawable.normal));
        voices.add(new VoiceEffect("Chipmunk", 1.3f, 1.5f, R.drawable.chipmunk));
        voices.add(new VoiceEffect("Monster", 0.7f, 0.8f, R.drawable.monster));
        voices.add(new VoiceEffect("Fast", 1.5f, 1.2f, R.drawable.fast));
        voices.add(new VoiceEffect("Slow", 0.6f, 0.7f, R.drawable.slow));
        voices.add(new VoiceEffect("Alien", 1.1f, 1.4f, R.drawable.alien));
        voices.add(new VoiceEffect("Demon", 0.5f, 0.6f, R.drawable.demon));
        voices.add(new VoiceEffect("Robot", 1.0f, 1.0f, R.drawable.robot));
        voices.add(new VoiceEffect("Echo", 1.0f, 1.0f, R.drawable.echo));
        VoiceEffectAdapter adapter = new VoiceEffectAdapter(this, voices, new VoiceEffectAdapter.OnEffectClickListener() {
            @Override
            public void onEffectClick(VoiceEffect voiceEffect) {
                selectedEffect = voiceEffect;
                playEffect(voiceEffect.getName());
            }
        });
        this.binding.effectList.setLayoutManager(new GridLayoutManager(this, 3));
        this.binding.effectList.setAdapter(adapter);
    }


    private void playEffect(String effectName) {
        if (this.originalPlayer != null) {
            this.originalPlayer.stop();
            this.originalPlayer.release();
            this.originalPlayer = null;
        }
        if (this.audioTrack != null) {
            this.audioTrack.stop();
            this.audioTrack.release();
            this.audioTrack = null;
        }
        if (effectName.equals("Echo")) {
            playEcho();
        } else {
            playEffectSimple(effectName);
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void playEffectSimple(String effectName) {
        if (audioFilePath == null) return;

        float pitch = 1f, speed = 1f;

        switch (effectName) {
            case "Normal": pitch = 1f; speed = 1f; break;
            case "Chipmunk": pitch = 1.8f; speed = 1.5f; break;
            case "Monster": pitch = 0.7f; speed = 0.8f; break;
            case "Fast": speed = 1.5f; break;
            case "Slow": speed = 0.7f; break;
            case "Demon": pitch = 0.6f; break;
            case "Robot": pitch = 1f; break;
            case "Alien": pitch = 1.4f; break;
        }

        try {
            originalPlayer = new MediaPlayer();
            originalPlayer.setDataSource(audioFilePath);
            originalPlayer.prepare();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                originalPlayer.setPlaybackParams(
                        originalPlayer.getPlaybackParams().setPitch(pitch).setSpeed(speed)
                );
            }

            // Set button to "playing" immediately
            runOnUiThread(() -> binding.playPause.setImageResource(R.drawable.play_mini));

            originalPlayer.setOnCompletionListener(mp ->
                    runOnUiThread(() -> binding.playPause.setImageResource(R.drawable.pause_mimi))
            );
            originalPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to play effect", Toast.LENGTH_SHORT).show();
        }
    }



    private void playEcho() {
        new Thread(() -> {
            try {
                short[] pcmData = decodeWavToPCM(audioFilePath);
                if (pcmData == null) return;

                float[] buffer = new float[pcmData.length];
                for (int i = 0; i < pcmData.length; i++)
                    buffer[i] = pcmData[i] / 32768f;

                float[] echoBuffer = new float[buffer.length];
                float decay = 0.6f;
                int delaySamples = sampleRate / 4;
                for (int i = 0; i < buffer.length; i++) {
                    float delayed = i >= delaySamples ? echoBuffer[i - delaySamples] : 0;
                    echoBuffer[i] = buffer[i] + delayed * decay;
                }

                for (int i = 0; i < buffer.length; i++)
                    pcmData[i] = (short) (Math.max(-1f, Math.min(1f, echoBuffer[i])) * 32767);

                int minBuffer = AudioTrack.getMinBufferSize(sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        minBuffer,
                        AudioTrack.MODE_STREAM);

                // Update button when playback starts
                runOnUiThread(() -> binding.playPause.setImageResource(R.drawable.play_mini));

                audioTrack.play();

                int chunkSize = 2048;
                for (int i = 0; i < pcmData.length; i += chunkSize) {
                    int lenChunk = Math.min(chunkSize, pcmData.length - i);
                    audioTrack.write(pcmData, i, lenChunk);
                }

                // Update button after playback ends
                runOnUiThread(() -> binding.playPause.setImageResource(R.drawable.pause_mimi));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private short[] decodeWavToPCM(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = new FileInputStream(file);
        fis.read(new byte[44]);
        int dataSize = (int) (file.length() - 44);
        byte[] byteData = new byte[dataSize];
        fis.read(byteData);
        fis.close();
        short[] pcm = new short[(dataSize / 2)];
        for (int i = 0; i < pcm.length; i++) {
            pcm[i] = (short) ((byteData[i * 2] & 255) | (byteData[(i * 2) + 1] << 8));
        }
        return pcm;
    }

    /* access modifiers changed from: private */
    public void saveRecording() {
        VoiceEffect effect;
        if (this.audioFilePath == null) {
            Toast.makeText(this, "Nothing to save!", Toast.LENGTH_SHORT).show();
            return;
        }
        int durationSec = 0;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this.audioFilePath);
            String dur = retriever.extractMetadata(9);
            if (dur != null) {
                durationSec = Integer.parseInt(dur) / 1000;
            }
            retriever.release();
        } catch (Exception e) {
        }
        String finalDuration = String.format("%02d:%02d", new Object[]{Integer.valueOf(durationSec / 60), Integer.valueOf(durationSec % 60)});
        if (this.selectedEffect != null) {
            effect = this.selectedEffect;
        } else {
            effect = new VoiceEffect("Normal", 1.0f, 1.0f, R.drawable.emoji);
        }
        SoundModel sound = new SoundModel("" + System.currentTimeMillis(), effect.getIconRes(), effect.getName(), finalDuration, false, -1);
        sound.setFilePath(this.audioFilePath);
        List<SoundModel> history = HistoryStorage.loadHistory(this);
        history.add(0, sound);
        HistoryStorage.saveHistory(this, history);
        Toast.makeText(this, "Saved to history!", Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.originalPlayer != null) {
            this.originalPlayer.release();
            this.originalPlayer = null;
        }
        if (this.audioTrack != null) {
            this.audioTrack.release();
            this.audioTrack = null;
        }
    }
}
