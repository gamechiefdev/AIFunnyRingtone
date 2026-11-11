package com.gamechief.aifunnyringtone.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityVoiceCallBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class VoiceCallActivity extends ParentActivity {
    ActivityVoiceCallBinding binding;
    float downX;
    ObjectAnimator floatingAnimator;
    int icon;
    MediaPlayer mediaPlayer;
    int selectedImage;
    int selectedPosition;
    String selectedText;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityVoiceCallBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.selectedImage = getIntent().getIntExtra("image", 0);
        this.selectedText = getIntent().getStringExtra("text");
        this.selectedPosition = getIntent().getIntExtra("position", 0);
        this.icon = getIntent().getIntExtra("icon", 0);
        this.binding.swipeButton.setImageResource(this.icon);
        this.binding.profileImageCalling.setImageResource(this.selectedImage);
        this.binding.textnameCalling.setText(this.selectedText);
        this.binding.imageView15.setBackground((BitmapDrawable) getResources().getDrawable(this.selectedImage));
        this.mediaPlayer = MediaPlayer.create(this, R.raw.sound_play);
        this.mediaPlayer.setLooping(true);
        this.mediaPlayer.start();
        startFloatingAnimation();
        startMultipleArrowAnimation();
        initSwipe();
    }


    private void initSwipe() {

        binding.swipeButton.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    if (floatingAnimator != null && floatingAnimator.isRunning()) {
                        floatingAnimator.cancel(); // stop floating while dragging
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getRawX() - downX;
                    float maxMove = binding.swipeContainer.getWidth()
                            - binding.swipeButton.getWidth();

                    if (moveX >= -maxMove && moveX <= maxMove) {
                        binding.swipeButton.setTranslationX(moveX);
                    }
                    return true;



                case MotionEvent.ACTION_UP:
                    float finalX = binding.swipeButton.getTranslationX();

                    if (finalX > binding.swipeContainer.getWidth() / 4f) {
                        acceptCall();
                    } else if (finalX < -binding.swipeContainer.getWidth() / 4f) {
                        rejectCall();
                    }

                    binding.swipeButton.animate()
                            .translationX(0)
                            .setDuration(200)
                            .withEndAction(() -> startFloatingAnimation()) // restart floating
                            .start();
                    return true;

            }
            return false;
        });
    }

    private void startFloatingAnimation() {
        floatingAnimator = ObjectAnimator.ofFloat(binding.swipeButton, "translationY", 0f, 10f, 0f, -10f, 0f);
        floatingAnimator.setDuration(2000);
        floatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        floatingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatingAnimator.start();
    }

    private void startMultipleArrowAnimation() {
        ImageView[] arrows = {this.binding.arrow1, this.binding.arrow2, this.binding.arrow3, this.binding.arrow4, this.binding.arrow5};
        for (int i = 0; i < arrows.length; i++) {
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(arrows[i], new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("translationY", new float[]{0.0f, -30.0f}), PropertyValuesHolder.ofFloat("alpha", new float[]{1.0f, 0.5f})});
            animator.setDuration(1000);
            animator.setStartDelay((long) (i * 200));
            animator.setRepeatMode(2);
            animator.setRepeatCount(-1);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }

    private void acceptCall() {
        Intent intent;
        stopRingtone();
        if (this.icon == R.drawable.video_call) {
            int videoResourceId = getVideoByPosition(this.selectedPosition);
            intent = new Intent(this, VideoCallActivity.class);
            intent.putExtra("videoResourceId", videoResourceId);
        } else {
            int audioRes = getAudioByPosition(this.selectedPosition);
            intent = new Intent(this, AcceptVoiceCall.class);
            intent.putExtra("AudioResourceId", audioRes);
        }
        intent.putExtra("image", this.selectedImage);
        intent.putExtra("text", this.selectedText);
        intent.putExtra("position", this.selectedPosition);
        startActivity(intent);
        finish();
    }

    private void rejectCall() {
        stopRingtone();
        Toast.makeText(this, "Call Rejected", 0).show();
        finish();
    }

    private int getAudioByPosition(int position) {
        switch (position) {
            case 0:
                return R.raw.agent;
            case 1:
                return R.raw.boss;
            case 2:
                return R.raw.delivery;
            case 3:
                return R.raw.girlfrien;
            case 4:
                return R.raw.police;
            case 5:
                return R.raw.wife;
            default:
                return R.raw.agent;
        }
    }

    private int getVideoByPosition(int position) {
        switch (position) {
            case 0:
                return R.raw.agent_video;
            case 1:
                return R.raw.boss_video;
            case 2:
                return R.raw.delivery_video;
            case 3:
                return R.raw.girlfrien_video;
            case 4:
                return R.raw.police_video;
            case 5:
                return R.raw.wife_video;
            default:
                return R.raw.agent_video;
        }
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
