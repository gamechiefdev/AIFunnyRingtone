package com.gamechief.aifunnyringtone.fragments;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import androidx.core.app.ActivityCompat;

import com.gamechief.aifunnyringtone.models.VoiceEffect;


public class VoiceEffectEngine {
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private final Context context;
    private float[] echoBuffer;
    private int echoIndex = 0;
    private boolean isRunning = false;
    private Thread processingThread;
    private final int sampleRate = 44100;
    private VoiceEffect selectedEffect;

    public VoiceEffectEngine(Context context2) {
        this.context = context2.getApplicationContext();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setEffect(VoiceEffect effect) {
        this.selectedEffect = effect;
    }

    public void start() {
        if (!this.isRunning && ActivityCompat.checkSelfPermission(this.context, "android.permission.RECORD_AUDIO") == 0) {
            int bufferSize = Math.max(AudioRecord.getMinBufferSize(44100, 16, 2), 14700);
            this.echoBuffer = new float[44100];
            AudioManager am = (AudioManager) this.context.getSystemService("audio");
            am.setMode(3);
            am.setSpeakerphoneOn(true);
            this.audioRecord = new AudioRecord.Builder().setAudioSource(7).setAudioFormat(new AudioFormat.Builder().setSampleRate(44100).setEncoding(2).setChannelMask(16).build()).setBufferSizeInBytes(bufferSize).build();
            enableAudioEffects(this.audioRecord);
            this.audioTrack = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(1).setContentType(2).build()).setAudioFormat(new AudioFormat.Builder().setSampleRate(44100).setEncoding(2).setChannelMask(4).build()).setTransferMode(1).setBufferSizeInBytes(bufferSize).build();
            this.audioTrack.setVolume(AudioTrack.getMaxVolume());
            this.isRunning = true;
            this.audioRecord.startRecording();
            this.audioTrack.play();
            this.processingThread = new Thread(() ->process( bufferSize));
            this.processingThread.start();
        }
    }

    public void process (int bufferSize) {
        short[] shortBuffer = new short[bufferSize];
        float[] floatBuffer = new float[bufferSize];
        while (this.isRunning) {
            int read = this.audioRecord.read(shortBuffer, 0, shortBuffer.length);
            if (read > 0) {
                for (int i = 0; i < read; i++) {
                    floatBuffer[i] = clamp((((float) shortBuffer[i]) / 32768.0f) * 1.8f);
                }
                if (this.selectedEffect != null && !"Normal".equals(this.selectedEffect.getName())) {
                    applyEffect(floatBuffer, read, selectedEffect);
                }
                for (int i2 = 0; i2 < read; i2++) {
                    shortBuffer[i2] = (short) ((int) (floatBuffer[i2] * 32767.0f));
                }
                this.audioTrack.write(shortBuffer, 0, read);
            }
        }
    }

    public void stop() {
        this.isRunning = false;
        try {
            if (this.processingThread != null) {
                this.processingThread.interrupt();
            }
            if (this.audioRecord != null) {
                this.audioRecord.stop();
                this.audioRecord.release();
            }
            if (this.audioTrack != null) {
                this.audioTrack.stop();
                this.audioTrack.release();
            }
        } catch (Exception e) {
        }
    }

    private void enableAudioEffects(AudioRecord record) {
        AutomaticGainControl agc;
        NoiseSuppressor ns;
        AcousticEchoCanceler aec;
        int sessionId = record.getAudioSessionId();
        if (AcousticEchoCanceler.isAvailable() && (aec = AcousticEchoCanceler.create(sessionId)) != null) {
            aec.setEnabled(true);
        }
        if (NoiseSuppressor.isAvailable() && (ns = NoiseSuppressor.create(sessionId)) != null) {
            ns.setEnabled(true);
        }
        if (AutomaticGainControl.isAvailable() && (agc = AutomaticGainControl.create(sessionId)) != null) {
            agc.setEnabled(true);
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyEffect(float[] buffer, int len, VoiceEffect effect) {
        switch (effect.getName()) {
            case "Chipmunk": safePitch(buffer, len, 1.2f); break;
            case "Monster": safePitch(buffer, len, 0.8f); monsterPost(buffer, len); break;
            case "Demon": safePitch(buffer, len, 0.9f); demonPost(buffer, len); break;
            case "Fast": safePitch(buffer, len, 1.2f); break;
            case "Slow": safePitch(buffer, len, 0.8f); break;
            case "Robot": robot(buffer, len); break;
            case "Echo": applyEcho(buffer, len); break;
            case "Alien": alien(buffer, len); break;
        }
    }

    private void safePitch(float[] buffer, int len, float factor) {
        float[] out = new float[len];
        for (int i = 0; i < len; i++) {
            float src = ((float) i) / factor;
            if (src >= ((float) (len - 1))) {
                out[i] = buffer[len - 1];
            } else {
                int i1 = (int) src;
                float f = src - ((float) i1);
                out[i] = (buffer[i1] * (1.0f - f)) + (buffer[i1 + 1] * f);
            }
        }
        System.arraycopy(out, 0, buffer, 0, len);
    }

    private void monsterPost(float[] buf, int len) {
        for (int i = 0; i < len; i++) {
            buf[i] = clamp((float) Math.tanh((double) (1.1f * ((buf[i] * 0.7f) + (buf[((i - 4) + len) % len] * 0.8f)))));
        }
    }

    private void demonPost(float[] buf, int len) {
        for (int i = 0; i < len; i++) {
            buf[i] = clamp(buf[i] * ((0.25f * ((float) Math.sin((((double) i) * 18.84955592153876d) / 44100.0d))) + 1.0f));
        }
    }

    private void robot(float[] buf, int len) {
        for (int i = 0; i < len; i++) {
            buf[i] = clamp(buf[i] * ((float) Math.sin(((((double) 10.0f) * 6.283185307179586d) * ((double) i)) / 44100.0d)));
        }
    }

    private void applyEcho(float[] buffer, int len) {
        for (int i = 0; i < len; i++) {
            float in = buffer[i];
            float delayed = this.echoBuffer[this.echoIndex];
            this.echoBuffer[this.echoIndex] = (delayed * 0.15f) + in;
            buffer[i] = clamp((0.12f * delayed) + in);
            this.echoIndex = (this.echoIndex + 1) % this.echoBuffer.length;
        }
    }

    private void alien(float[] buffer, int len) {
        for (int i = 0; i < len; i++) {
            buffer[i] = clamp(buffer[i] * ((0.15f * ((float) Math.sin((((double) i) * 15.707963267948966d) / 44100.0d))) + 1.0f));
        }
    }

    private float clamp(float f) {
        return Math.max(-1.0f, Math.min(1.0f, f));
    }
}
