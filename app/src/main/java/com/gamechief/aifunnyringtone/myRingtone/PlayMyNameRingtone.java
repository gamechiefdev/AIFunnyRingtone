package com.gamechief.aifunnyringtone.myRingtone;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.adsCondition.SharedPreferenceManager;
import com.gamechief.aifunnyringtone.databinding.ActivityPlayMyNameRingtoneBinding;
import com.gamechief.aifunnyringtone.models.SavedRingtone;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.Locale;

public class PlayMyNameRingtone extends ParentActivity {
    ActivityPlayMyNameRingtoneBinding binding;
    String promptText = "";
    TextToSpeech tts;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityPlayMyNameRingtoneBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(v -> onBackPressed());
        this.promptText = getIntent().getStringExtra("selectedPrompt");
        this.binding.getPromptTxt.setText(this.promptText);
        this.tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                this.tts.setLanguage(Locale.US);
                this.tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        runOnUiThread(() -> binding.playPrompt.setImageResource(R.drawable.ic_pause_myname));
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> binding.playPrompt.setImageResource(R.drawable.ic_play_myname));
                    }

                    @Override
                    public void onError(String utteranceId) {
                        runOnUiThread(() -> binding.playPrompt.setImageResource(R.drawable.ic_play_myname));
                    }
                });
            }
        });
        this.binding.playPrompt.setOnClickListener(v -> {
            if (this.promptText == null || this.promptText.isEmpty()) {
                Toast.makeText(this, "No text to read", Toast.LENGTH_SHORT).show();
            } else {
                if (this.tts.isSpeaking()) {
                    this.tts.stop();
                    this.binding.playPrompt.setImageResource(R.drawable.ic_play_myname);
                } else {
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "promptTTS");
                    this.tts.speak(this.promptText, TextToSpeech.QUEUE_FLUSH, params, "promptTTS");
                }
            }
        });
        this.binding.savePromptRingtone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PlayMyNameRingtone.this.promptText == null || PlayMyNameRingtone.this.promptText.isEmpty()) {
                    Toast.makeText(PlayMyNameRingtone.this, "Nothing to save", Toast.LENGTH_SHORT).show();
                    return;
                }
                new SharedPreferenceManager(PlayMyNameRingtone.this, "my_ringtone_prefs").addRingtone(new SavedRingtone(PlayMyNameRingtone.this.promptText));
                Toast.makeText(PlayMyNameRingtone.this, "Ringtone saved", Toast.LENGTH_SHORT).show();
                PlayMyNameRingtone.this.startActivity(new Intent(PlayMyNameRingtone.this, SaveNameRingtone.class));
            }
        });
    }

    public void onDestroy() {
        if (this.tts != null) {
            this.tts.stop();
            this.tts.shutdown();
        }
        super.onDestroy();
    }
}
