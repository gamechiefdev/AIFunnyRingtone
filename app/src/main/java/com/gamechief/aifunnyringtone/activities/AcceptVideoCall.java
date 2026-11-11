package com.gamechief.aifunnyringtone.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityAcceptVideoCallBinding;


import net.lingala.zip4j.util.InternalZipConstants;

public class AcceptVideoCall extends ParentActivity {
    ActivityAcceptVideoCallBinding binding;
    float downX;
    ObjectAnimator floatingAnimator;
    Handler handler;
    MediaPlayer mediaPlayer;
    int number;
    Runnable runnable;
    int selectedPosition;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAcceptVideoCallBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.mediaPlayer = MediaPlayer.create(this, R.raw.sound_play);
        this.mediaPlayer.setLooping(true);
        this.mediaPlayer.start();
        this.number = 0;
        int selectedImage = getIntent().getIntExtra("image", 0);
        String selectedText = getIntent().getStringExtra("text");
        this.selectedPosition = getIntent().getIntExtra("position", 0);
        this.binding.profileImageCalling.setImageResource(selectedImage);
        this.binding.textnameCalling.setText(selectedText);
        this.binding.imageView15.setBackground(getResources().getDrawable(selectedImage));
        this.binding.imageView15.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.floatingAnimator = ObjectAnimator.ofFloat(this.binding.swipeButton, "translationY", new float[]{0.0f, 10.0f, 0.0f, -10.0f, 0.0f});
        this.floatingAnimator.setDuration(2000);
        this.floatingAnimator.setRepeatCount(-1);
        this.floatingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.floatingAnimator.start();;
        startMultipleArrowAnimation();
        initSwipe();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSwipe() {
        this.binding.swipeButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    this.downX = event.getRawX();
                    if (this.floatingAnimator != null && this.floatingAnimator.isRunning()) {
                        this.floatingAnimator.cancel();
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    float finalX = this.binding.swipeButton.getTranslationX();

                    if (finalX > this.binding.swipeContainer.getWidth() / 4f) {
                        accept(this.binding.swipeButton);
                    } else if (finalX < -this.binding.swipeContainer.getWidth() / 4f) {
                        rejectCall();
                    }

                    this.binding.swipeButton.animate()
                            .translationX(0f)
                            .setDuration(200)
                            .withEndAction(() -> startFloatingAnimation()) // ✅ FIX
                            .start();

                    return true;

                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getRawX() - this.downX;
                    float maxMove = this.binding.swipeContainer.getWidth() - this.binding.swipeButton.getWidth();

                    if (moveX >= -maxMove && moveX <= maxMove) {
                        this.binding.swipeButton.setTranslationX(moveX);
                    }
                    return true;

                default:
                    return false;
            }
        });
    }

    private void startFloatingAnimation() {
        this.floatingAnimator = ObjectAnimator.ofFloat(
                this.binding.swipeButton,
                "translationY",
                0f, 10f, 0f, -10f, 0f
        );

        this.floatingAnimator.setDuration(2000);
        this.floatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        this.floatingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.floatingAnimator.start();
    }

    private void startMultipleArrowAnimation() {
        ImageView[] arrows = {this.binding.arrow1, this.binding.arrow2, this.binding.arrow3, this.binding.arrow4, this.binding.arrow5};
        for (int i = 0; i < arrows.length; i++) {
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(arrows[i], new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("translationY", new float[]{0.0f, -30.0f}), PropertyValuesHolder.ofFloat("alpha", new float[]{1.0f, 0.5f})});
            animator.setDuration(1000);
            animator.setStartDelay((long) (i * 200));
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(-1);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }

    public void accept(View view) {
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }
        int videoResourceId = 0;
        if (this.selectedPosition == 0) {
            videoResourceId = R.raw.agent_video;
        }
        if (this.selectedPosition == 1) {
            videoResourceId = R.raw.boss_video;
        }
        if (this.selectedPosition == 2) {
            videoResourceId = R.raw.delivery_video;
        }
        if (this.selectedPosition == 3) {
            videoResourceId = R.raw.girlfrien_video;
        }
        if (this.selectedPosition == 4) {
            videoResourceId = R.raw.police_video;
        }
        if (this.selectedPosition == 5) {
            videoResourceId = R.raw.wife_video;
        }
        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.putExtra("videoResourceId", videoResourceId);
        intent.putExtra("position", this.selectedPosition);
        intent.putExtra("videoPath", Uri.parse("android.resource://" + getPackageName() + InternalZipConstants.ZIP_FILE_SEPARATOR + videoResourceId).toString());
        intent.putExtra("timer_number", this.number);
        startActivity(intent);
    }

    private void rejectCall() {
        stopRingtone();
        Toast.makeText(this, "Call Rejected", Toast.LENGTH_LONG).show();
        finish();
    }

    private void stopRingtone() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        stopRingtone();
    }
}
