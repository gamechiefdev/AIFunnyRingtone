package com.gamechief.aifunnyringtone.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;


import com.gamechief.aifunnyringtone.databinding.ActivityAcceptVoiceCallBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class AcceptVoiceCall extends ParentActivity {
    int AudioResourceId;
    ActivityAcceptVoiceCallBinding binding;
    Handler handler;
    /* access modifiers changed from: private */
    public MediaPlayer mediaPlayer;
    int number;
    Runnable runnable;
    int selectedPosition;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAcceptVoiceCallBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        int selectedImage = getIntent().getIntExtra("image", 0);
        String selectedText = getIntent().getStringExtra("text");
        this.AudioResourceId = getIntent().getIntExtra("AudioResourceId", 0);
        this.selectedPosition = getIntent().getIntExtra("position", 0);
        this.binding.profileImageCallingAudio.setImageResource(selectedImage);
        this.binding.textnameCalling.setText(selectedText);
        this.number = 0;
        this.number = getIntent().getIntExtra("timer_number", 0);
        playAudio(this.AudioResourceId);
        this.binding.EndCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AcceptVoiceCall.this.decline(v);
            }
        });
    }



    private void playAudio(int audioResource) {
        this.mediaPlayer = MediaPlayer.create(this, audioResource);
        this.mediaPlayer.start();
        startTimer();
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                AcceptVoiceCall.this.mediaPlayer.reset();
            }
        });
    }

    public void decline(View view) {
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }
        stopCall();
        finish();
    }

    private void stopCall() {
        if (this.handler != null) {
            this.handler.removeCallbacks(this.runnable);
        }
        this.binding.timerText.setText("End Call");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    private void startTimer() {
        this.handler = new Handler();
        this.runnable = new Runnable() {
            public void run() {
                if (AcceptVoiceCall.this.number < 10) {
                    AcceptVoiceCall.this.binding.timerText.setText("00:0" + AcceptVoiceCall.this.number);
                } else if (AcceptVoiceCall.this.number < 60) {
                    AcceptVoiceCall.this.binding.timerText.setText("00:" + AcceptVoiceCall.this.number);
                } else if (AcceptVoiceCall.this.number % 60 < 10) {
                    AcceptVoiceCall.this.binding.timerText.setText((AcceptVoiceCall.this.number / 60) + ":0" + (AcceptVoiceCall.this.number % 60));
                } else {
                    AcceptVoiceCall.this.binding.timerText.setText((AcceptVoiceCall.this.number / 60) + ":" + (AcceptVoiceCall.this.number % 60));
                }
                AcceptVoiceCall.this.number++;
                AcceptVoiceCall.this.handler.postDelayed(AcceptVoiceCall.this.runnable, 1000);
            }
        };
        this.handler.post(this.runnable);
    }
}
