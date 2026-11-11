package com.gamechief.aifunnyringtone.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.MainActivity;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityTrimSoundScreenBinding;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class TrimSoundScreen extends ParentActivity {
    ActivityTrimSoundScreenBinding binding;
    Handler handler = new Handler();
    private int mediaDuration = 0;
    MediaPlayer mediaPlayer;
    private String soundPath;
    /* access modifiers changed from: private */
    public int trimEnd = 0;
    /* access modifiers changed from: private */
    public int trimStart = 0;
    
    private float dXLeft, dXRight;

    private final Runnable updateRunnable = new Runnable() {
        public void run() {
            if (TrimSoundScreen.this.mediaPlayer != null && TrimSoundScreen.this.mediaPlayer.isPlaying()) {
                int current = TrimSoundScreen.this.mediaPlayer.getCurrentPosition();
                if (current >= TrimSoundScreen.this.trimEnd) {
                    TrimSoundScreen.this.mediaPlayer.pause();
                    TrimSoundScreen.this.mediaPlayer.seekTo(TrimSoundScreen.this.trimStart);
                    TrimSoundScreen.this.binding.playerplaybtn.setImageResource(R.drawable.play);
                    return;
                }
                TrimSoundScreen.this.binding.seekbar.setProgress(current);
                TrimSoundScreen.this.updateDurationText();
                TrimSoundScreen.this.handler.postDelayed(this, 200);
            }
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityTrimSoundScreenBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(onClickListener -> onBackPressed());
        this.soundPath = getIntent().getStringExtra("soundFilePath");
        String soundName = getIntent().getStringExtra("soundName");
        
        if (soundName != null) {
            this.binding.name.setText(soundName);
        }

        applyGhostFilter();

        if (this.soundPath != null) {
            if (this.soundPath.startsWith("http")) {
                downloadAndSetup(this.soundPath);
            } else {
                setupControls(this.soundPath);
            }
        }

        this.binding.save.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            exportTrimmedAudio();
        });

        this.binding.getRoot().post(this::updateLottieClipping);
    }

    private void applyGhostFilter() {
        // Set alpha to make sides (inactive parts) highly transparent
        binding.lottieAnimationView.setAlpha(0.2f); 
        
        // Apply grey filter (saturation = 0)
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        binding.lottieAnimationView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void downloadAndSetup(String urlString) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading audio for trimming...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.connect();
                
                File cacheFile = new File(getCacheDir(), "trim_cache_" + System.currentTimeMillis() + ".mp3");
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(cacheFile);

                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    this.soundPath = cacheFile.getAbsolutePath();
                    setupControls(this.soundPath);
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to download audio", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void setupControls(String path) {
        try {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.release();
            }
            this.mediaPlayer = new MediaPlayer();
            if (path.startsWith("content://")) {
                this.mediaPlayer.setDataSource(this, Uri.parse(path));
            } else {
                this.mediaPlayer.setDataSource(path);
            }

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("TrimSound", "MediaPlayer error: " + what + " extra: " + extra);
                return true;
            });

            this.mediaPlayer.prepare();
            this.mediaDuration = this.mediaPlayer.getDuration();
            this.trimStart = 0;
            this.trimEnd = this.mediaDuration;
            this.binding.seekbar.setMax(this.mediaDuration);
            
            this.mediaPlayer.setOnCompletionListener(mp -> {
                binding.playerplaybtn.setImageResource(R.drawable.play);
            });

            setupUIControls();
            setupTrimMarkers();
            updateDurationText();
            
            this.mediaPlayer.seekTo(this.trimStart);
            this.mediaPlayer.start();
            this.binding.playerplaybtn.setImageResource(R.drawable.pause);
            this.handler.post(this.updateRunnable);

            binding.lottieAnimationView.post(() -> {
                // Initialize handles so their centers are at the edges
                float leftX = binding.lottieAnimationView.getX() - (binding.trimLeft.getWidth() / 2f);
                float rightX = binding.lottieAnimationView.getX() + binding.lottieAnimationView.getWidth() - (binding.trimRight.getWidth() / 2f);
                
                binding.trimLeft.setX(leftX);
                binding.trimRight.setX(rightX);
                updateLottieClipping();
            });

        } catch (Exception e) {
            Log.e("TrimSound", "Setup error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Audio cannot play", Toast.LENGTH_LONG).show();
        }
    }


    private void setupUIControls() {
        this.binding.playerplaybtn.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    binding.playerplaybtn.setImageResource(R.drawable.play);
                    return;
                }
                int current = mediaPlayer.getCurrentPosition();
                if (current < trimStart || current >= trimEnd) {
                    mediaPlayer.seekTo(trimStart);
                }
                mediaPlayer.start();
                binding.playerplaybtn.setImageResource(R.drawable.pause);
                handler.post(updateRunnable);
            }
        });
        
        this.binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && TrimSoundScreen.this.mediaPlayer != null) {
                    TrimSoundScreen.this.mediaPlayer.seekTo(progress);
                }
                TrimSoundScreen.this.updateDurationText();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupTrimMarkers() {
        this.binding.trimLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dXLeft = v.getX() - event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dXLeft;
                    float minX = binding.lottieAnimationView.getX() - (v.getWidth() / 2f);
                    float maxX = binding.trimRight.getX() - v.getWidth();
                    newX = Math.max(minX, Math.min(newX, maxX));
                    v.setX(newX);
                    
                    float leftMarkerCenter = newX + (v.getWidth() / 2f);
                    trimStart = (int) (((float) mediaDuration) * ((leftMarkerCenter - binding.lottieAnimationView.getX()) / binding.lottieAnimationView.getWidth()));
                    if (mediaPlayer != null) mediaPlayer.seekTo(trimStart);
                    updateLottieClipping();
                    break;
            }
            return true;
        });

        this.binding.trimRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dXRight = v.getX() - event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dXRight;
                    float maxX = binding.lottieAnimationView.getX() + binding.lottieAnimationView.getWidth() - (v.getWidth() / 2f);
                    float minX = binding.trimLeft.getX() + v.getWidth();
                    newX = Math.max(minX, Math.min(newX, maxX));
                    v.setX(newX);
                    
                    float rightMarkerCenter = newX + (v.getWidth() / 2f);
                    trimEnd = (int) (((float) mediaDuration) * ((rightMarkerCenter - binding.lottieAnimationView.getX()) / binding.lottieAnimationView.getWidth()));
                    if (mediaPlayer != null) mediaPlayer.seekTo(trimEnd);
                    updateLottieClipping();
                    break;
            }
            return true;
        });
    }

    private void updateLottieClipping() {
        if (binding.lottieAnimationViewColored.getWidth() == 0) return;

        float leftMarkerCenter = binding.trimLeft.getX() + (binding.trimLeft.getWidth() / 2f);
        float rightMarkerCenter = binding.trimRight.getX() + (binding.trimRight.getWidth() / 2f);

        // Update clipping for colored Lottie (Active area)
        int clipLeft = (int) (leftMarkerCenter - binding.lottieAnimationViewColored.getX());
        int clipRight = (int) (rightMarkerCenter - binding.lottieAnimationViewColored.getX());
        clipLeft = Math.max(0, clipLeft);
        clipRight = Math.min(binding.lottieAnimationViewColored.getWidth(), clipRight);
        
        Rect activeRect = new Rect(clipLeft, 0, clipRight, binding.lottieAnimationViewColored.getHeight());
        binding.lottieAnimationViewColored.setClipBounds(activeRect);

        // Update Overlays using ClipBounds to avoid requestLayout() shivering
        // Left Overlay: Visible from 0 to clipLeft
        Rect leftRect = new Rect(0, 0, clipLeft, binding.leftOverlay.getHeight());
        binding.leftOverlay.setClipBounds(leftRect);

        // Right Overlay: Visible from clipRight to end
        Rect rightRect = new Rect(clipRight, 0, binding.rightOverlay.getWidth(), binding.rightOverlay.getHeight());
        binding.rightOverlay.setClipBounds(rightRect);
    }

    private void updateDurationText() {
        if (this.mediaPlayer != null) {
            this.binding.runningduration.setText(formatTime(this.mediaPlayer.getCurrentPosition()));
            this.binding.totalduration.setText(formatTime(this.mediaDuration));
        }
    }

    private String formatTime(int millis) {
        return String.format("%02d:%02d", (millis / 1000) / 60, (millis / 1000) % 60);
    }

    private void exportTrimmedAudio() {
        try {
            File inputFile = new File(this.soundPath);
            if (!inputFile.exists()) {
                 Toast.makeText(this, "Source file not found for trimming", Toast.LENGTH_SHORT).show();
                 return;
            }

            File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "trimmed_" + System.currentTimeMillis() + ".mp3");
            FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            
            long fileSize = inputFile.length();
            long startByte = (long) ((((float) this.trimStart) / ((float) this.mediaDuration)) * ((float) fileSize));
            long endByte = (long) ((((float) this.trimEnd) / ((float) this.mediaDuration)) * ((float) fileSize));
            long bytesToRead = endByte - startByte;

            fis.skip(startByte);
            byte[] buffer = new byte[4096];
            long totalRead = 0;
            while (totalRead < bytesToRead) {
                int read = fis.read(buffer, 0, (int) Math.min(buffer.length, bytesToRead - totalRead));
                if (read == -1) break;
                fos.write(buffer, 0, read);
                totalRead += read;
            }
            fis.close();
            fos.close();
            
            HistoryStorage.saveTrimmedSound(this, new SoundModel(this.binding.name.getText().toString(), outputFile.getAbsolutePath()));
            Toast.makeText(this, "Trimmed sound saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("open_history", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Trim failed", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        this.handler.removeCallbacksAndMessages(null);
    }
}
