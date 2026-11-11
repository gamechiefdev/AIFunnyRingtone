package com.gamechief.aifunnyringtone.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.facebook.ads.AdError;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityGenerateNowBinding;
import com.gamechief.aifunnyringtone.models.ApiClient;

import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import net.lingala.zip4j.util.InternalZipConstants;

public class GenerateNow extends ParentActivity {
    private static final int MAX_RETRIES = 3;
    private static final String TAG = "ApiClient";
    /* access modifiers changed from: private */
    public ApiClient apiClient;
    /* access modifiers changed from: private */
    public ActivityGenerateNowBinding binding;
    /* access modifiers changed from: private */
    public String prompt;
    /* access modifiers changed from: private */
    public int retryCount = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityGenerateNowBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.binding.reTrying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.reTrying.setVisibility(View.GONE);
                binding.lottieView.setVisibility(View.VISIBLE);
                binding.lottieView.playAnimation();
                retryCount = 0;
                generateSoundWithRetry(prompt);
            }
        });
        this.binding.lottieView.setAnimation(R.raw.transfer);
        this.binding.lottieView.playAnimation();
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Log.d(TAG, "Intent extra: " + key + " = " + extras.get(key));
                }
            } else {
                Log.d(TAG, "Intent extras bundle is null");
            }
        }
        this.prompt = getIntent().getStringExtra("prompt_text");
        Log.d(TAG, "Received prompt_text: " + this.prompt);
        if (this.prompt == null || this.prompt.isEmpty()) {
            Log.e(TAG, "Prompt is null or empty! Cannot generate sound.");
            this.binding.txtPromt.setText("Error: prompt is missing!");
            this.binding.lottieView.cancelAnimation();
            return;
        }
        this.binding.txtPromt.setText("Generating: " + this.prompt);
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            this.binding.lottieView.cancelAnimation();
            this.binding.txtPromt.setText("No internet connection");
            return;
        }
        this.apiClient = new ApiClient(this);
        generateSoundWithRetry(this.prompt);
    }






    /* access modifiers changed from: private */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnected();
    }

    /* access modifiers changed from: private */
    public void generateSoundWithRetry(final String promptText) {
        this.retryCount++;
        Log.d(TAG, "Generating sound (attempt " + this.retryCount + ")");
        this.apiClient.generateSfx(promptText, new ApiClient.SfxCallback() {
            public void onSuccess(String audioUrl) {
                Log.d(GenerateNow.TAG, "onSuccess: audioUrl = " + audioUrl);
                GenerateNow.this.binding.lottieView.cancelAnimation();
                HistoryStorage.saveRecent(GenerateNow.this, new SoundModel(promptText, audioUrl));
                Log.d(GenerateNow.TAG, "Saved generated sound to history");
                Intent intent = new Intent(GenerateNow.this, PlayAiSound.class);
                intent.putExtra("soundUrl", audioUrl);
                intent.putExtra("prompt_text", promptText);
                GenerateNow.this.startActivity(intent);
                GenerateNow.this.finish();
                GenerateNow.this.overridePendingTransition(17432576, 17432577);
            }

            public void onProcessing(String trackId) {
                Log.d(GenerateNow.TAG, "onProcessing: trackId = " + trackId);
                GenerateNow.this.apiClient.fetchSfx(trackId, this);
            }

            public void onProgress(int attempt, int maxAttempts) {
                Log.d(GenerateNow.TAG, "onProgress: attempt " + attempt + InternalZipConstants.ZIP_FILE_SEPARATOR + maxAttempts);
                GenerateNow.this.binding.txtPromt.setText("Processing... " + attempt + InternalZipConstants.ZIP_FILE_SEPARATOR + maxAttempts);
            }

            public void onError(VolleyError error) {
                Log.e(GenerateNow.TAG, "onError generating sound", error);
                if (!GenerateNow.this.isNetworkAvailable()) {
                    GenerateNow.this.binding.txtPromt.setText("No internet connection");
                    GenerateNow.this.binding.lottieView.cancelAnimation();
                    GenerateNow.this.binding.reTrying.setVisibility(View.VISIBLE);
                } else if (!(error instanceof TimeoutError) || GenerateNow.this.retryCount >= 3) {
                    GenerateNow.this.binding.lottieView.cancelAnimation();
                    GenerateNow.this.binding.txtPromt.setText("Error generating sound: " + (error != null ? error.getMessage() : "Unknown"));
                    GenerateNow.this.binding.reTrying.setVisibility(View.VISIBLE);
                } else {
                    int delayMs = GenerateNow.this.retryCount * AdError.SERVER_ERROR_CODE;
                    Log.d(GenerateNow.TAG, "Timeout occurred, retrying... (" + GenerateNow.this.retryCount + ")");
                    GenerateNow.this.binding.txtPromt.setText("Server busy, retrying... (" + GenerateNow.this.retryCount + ")");
                    new Handler().postDelayed(() -> {
                        generateSoundWithRetry(prompt);
                    }, delayMs);                }
            }
        });
    }
}
