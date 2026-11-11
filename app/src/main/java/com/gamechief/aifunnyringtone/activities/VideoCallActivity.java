package com.gamechief.aifunnyringtone.activities;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityVideoCallBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.Arrays;

public class VideoCallActivity extends ParentActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    /* access modifiers changed from: private */
    public Handler backgroundHandler;
    private HandlerThread backgroundThread;

    ActivityVideoCallBinding binding;
    /* access modifiers changed from: private */
    public CameraCaptureSession cameraCaptureSession;
    /* access modifiers changed from: private */
    public CameraDevice cameraDevice;
    private String cameraId;
    private CameraManager cameraManager;
    /* access modifiers changed from: private */
    public Handler handler;
    /* access modifiers changed from: private */
    public boolean isFrontCamera = false;
    /* access modifiers changed from: private */
    public MediaPlayer mediaPlayer;
    /* access modifiers changed from: private */
    public int number;
    /* access modifiers changed from: private */
    public Runnable runnable;
    int selectedPosition;
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        public void onOpened(CameraDevice camera) {
            if (VideoCallActivity.this.cameraDevice != null) {
                VideoCallActivity.this.closeCamera();
            }
            VideoCallActivity.this.cameraDevice = camera;
            VideoCallActivity.this.startPreview();
        }

        public void onDisconnected(CameraDevice camera) {
            VideoCallActivity.this.cameraDevice.close();
            VideoCallActivity.this.cameraDevice = null;
        }

        public void onError(CameraDevice camera, int error) {
            VideoCallActivity.this.cameraDevice.close();
            VideoCallActivity.this.cameraDevice = null;
            VideoCallActivity.this.finish();
        }
    };
    private TextureView textureView;
    private TextView timerText;
    String videoPath;
    int videoResourceId;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.textureView = (TextureView) findViewById(R.id.textureView2);
        this.timerText = (TextView) findViewById(R.id.timerText);
        this.videoResourceId = getIntent().getIntExtra("videoResourceId", 0);
        this.selectedPosition = getIntent().getIntExtra("position", 0);
        this.videoPath = getIntent().getStringExtra("videoPath");
        getWindow().setFlags(512, 512);
        if (!(this.videoResourceId == 0 || this.videoPath == null)) {
            playVideo(this.videoResourceId, this.videoPath);
        }
        this.number = getIntent().getIntExtra("timer_number", 0);
        playVideo(this.videoResourceId, this.videoPath);
        startTimer();
        this.binding.CameraRotation.setOnClickListener(new View.OnClickListener() {
            private boolean isCameraSwitching = false;

            public void onClick(View v) {
                if (!this.isCameraSwitching) {
                    this.isCameraSwitching = true;
                    VideoCallActivity.this.isFrontCamera = true ^ VideoCallActivity.this.isFrontCamera;
                    VideoCallActivity.this.closeCamera();
                    if (ActivityCompat.checkSelfPermission(VideoCallActivity.this, "android.permission.CAMERA") == 0) {
                        VideoCallActivity.this.openCamera();
                        new Handler().postDelayed( () -> {
                            isCameraSwitching = false; // Clear the flag to allow subsequent clicks
                        },1000);
                    }
                }
            }

            /* access modifiers changed from: package-private */
            /* renamed from: lambda$onClick$0$com-gamechief-funnyringtone-activities-VideoCallActivity$1  reason: not valid java name */
            public /* synthetic */ void m233lambda$onClick$0$comgamechieffunnyringtoneactivitiesVideoCallActivity$1() {
                this.isCameraSwitching = false;
            }
        });
        this.binding.EndCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                VideoCallActivity.this.decline(v);
            }
        });
        this.cameraManager = (CameraManager) getSystemService("camera");
        this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (ActivityCompat.checkSelfPermission(VideoCallActivity.this, "android.permission.CAMERA") == 0) {
                    VideoCallActivity.this.openCamera();
                    VideoCallActivity.this.startPreview();
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }


    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 200);
            return;
        }
        releaseMediaPlayer();
        startBackgroundThread();
        openCamera();
        if (!(this.videoResourceId == 0 || this.videoPath == null)) {
            playVideo(this.videoResourceId, this.videoPath);
        }
        startTimer();
    }

    private void startBackgroundThread() {
        this.backgroundThread = new HandlerThread("CameraBackgroundThread");
        this.backgroundThread.start();
        this.backgroundHandler = new Handler(this.backgroundThread.getLooper());
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        closeCamera();
        if (this.handler != null) {
            this.handler.removeCallbacks(this.runnable);
        }
        this.binding.videoView4.setOnPreparedListener((MediaPlayer.OnPreparedListener) null);
        this.binding.videoView4.setOnCompletionListener((MediaPlayer.OnCompletionListener) null);
        this.binding.videoView4.setOnErrorListener((MediaPlayer.OnErrorListener) null);
        this.binding.textureView2.setSurfaceTextureListener((TextureView.SurfaceTextureListener) null);
        closeCamera();
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
        }
        stopBackgroundThread();
    }

    private void stopBackgroundThread() {
        if (this.backgroundThread != null) {
            this.backgroundThread.quitSafely();
            try {
                this.backgroundThread.join();
                this.backgroundThread = null;
                this.backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        if (this.backgroundThread != null) {
            this.backgroundThread.quitSafely();
            try {
                this.backgroundThread.join();
                this.backgroundThread = null;
                this.backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void openCamera() {
        try {
            if (this.cameraDevice != null) {
                closeCamera();
            }
            String[] cameraIds = this.cameraManager.getCameraIdList();
            int length = cameraIds.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String id = cameraIds[i];
                Integer lensFacing = (Integer) this.cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING);
                if (!this.isFrontCamera || lensFacing == null || lensFacing.intValue() != 0) {
                    if (!this.isFrontCamera && lensFacing != null && lensFacing.intValue() == 1) {
                        this.cameraId = id;
                        break;
                    }
                    i++;
                } else {
                    this.cameraId = id;
                    break;
                }
            }
            if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 200);
                return;
            }
            this.cameraManager.openCamera(this.cameraId, this.stateCallback, this.backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void closeCamera() {
        if (this.cameraCaptureSession != null) {
            this.cameraCaptureSession.close();
            this.cameraCaptureSession = null;
        }
        if (this.cameraDevice != null) {
            this.cameraDevice.close();
            this.cameraDevice = null;
        }
    }

    private void releaseMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
        }
        this.mediaPlayer = null;
    }

    /* access modifiers changed from: private */
    public void startPreview() {
        if (this.cameraDevice != null && this.textureView.isAvailable()) {
            try {
                SurfaceTexture surfaceTexture = this.textureView.getSurfaceTexture();
                if (surfaceTexture != null) {
                    surfaceTexture.setDefaultBufferSize(this.textureView.getWidth(), this.textureView.getHeight());
                    final Surface previewSurface = new Surface(surfaceTexture);
                    this.cameraDevice.createCaptureSession(Arrays.asList(new Surface[]{previewSurface}), new CameraCaptureSession.StateCallback() {
                        public void onConfigured(CameraCaptureSession session) {
                            VideoCallActivity.this.cameraCaptureSession = session;
                            try {
                                CaptureRequest.Builder previewRequestBuilder = VideoCallActivity.this.cameraDevice.createCaptureRequest(1);
                                previewRequestBuilder.addTarget(previewSurface);
                                if (VideoCallActivity.this.cameraDevice != null) {
                                    VideoCallActivity.this.cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), (CameraCaptureSession.CaptureCallback) null, VideoCallActivity.this.backgroundHandler);
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onConfigureFailed(CameraCaptureSession session) {
                            Toast.makeText(VideoCallActivity.this, "Failed to configure camera capture session", 0).show();
                        }
                    }, this.backgroundHandler);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void playVideo(int videoResourceId2, String videoPath2) {
        if (this.binding.videoView4 != null) {
            try {
                if (this.mediaPlayer != null) {
                    this.mediaPlayer.stop();
                    this.mediaPlayer.reset();
                }
                if (videoPath2 != null) {
                    this.binding.videoView4.setVideoURI(Uri.parse(videoPath2));
                    this.binding.videoView4.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            VideoCallActivity.this.mediaPlayer = mp;
                            VideoCallActivity.this.mediaPlayer.setLooping(true);
                            VideoCallActivity.this.mediaPlayer.start();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startTimer() {
        this.handler = new Handler();
        this.runnable = new Runnable() {
            public void run() {
                if (VideoCallActivity.this.number < 10) {
                    VideoCallActivity.this.binding.timerText.setText("00:0" + VideoCallActivity.this.number);
                } else if (VideoCallActivity.this.number < 60) {
                    VideoCallActivity.this.binding.timerText.setText("00:" + VideoCallActivity.this.number);
                } else if (VideoCallActivity.this.number % 60 < 10) {
                    VideoCallActivity.this.binding.timerText.setText((VideoCallActivity.this.number / 60) + ":0" + (VideoCallActivity.this.number % 60));
                } else {
                    VideoCallActivity.this.binding.timerText.setText((VideoCallActivity.this.number / 60) + ":" + (VideoCallActivity.this.number % 60));
                }
                VideoCallActivity videoCallActivity = VideoCallActivity.this;
                videoCallActivity.number = videoCallActivity.number + 1;
                VideoCallActivity.this.handler.postDelayed(VideoCallActivity.this.runnable, 1000);
            }
        };
        this.handler.post(this.runnable);
    }

    public void decline(View view) {
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer = null;
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
}
