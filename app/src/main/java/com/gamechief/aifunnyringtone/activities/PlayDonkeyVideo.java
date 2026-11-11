package com.gamechief.aifunnyringtone.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.YouTubeVideoAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityPlayDonkeyVideoBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayDonkeyVideo
        extends ParentActivity {

    private ActivityPlayDonkeyVideoBinding binding;

    private YouTubeVideoAdapter adapter;

    private ArrayList<String> youtubeVideos;

    private int currentPosition = 0;

    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    // =========================================================
    // S3 FILE
    // =========================================================

    private static final String VIDEO_LIST_URL =
            "https://funnyringtoneapp.s3.us-east-1.amazonaws.com/fdk_links.txt";

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityPlayDonkeyVideoBinding
                        .inflate(
                                getLayoutInflater()
                        );

        setContentView(
                binding.getRoot()
        );

        // =====================================================
        // WINDOW INSETS
        // =====================================================

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // =====================================================
        // STATUS BAR
        // =====================================================

        UltimateBarX.statusBarOnly(this)
                .fitWindow(false)
                .transparent()
                .colorRes(R.color.black)
                .light(false)
                .apply();

        // =====================================================
        // BANNER
        // =====================================================

        AdsController.getInstance()
                .loadAdaptiveBannerAd(
                        this,
                        binding.adView
                );

        // =====================================================
        // BACK
        // =====================================================

        binding.imageView7.setOnClickListener(
                v -> onBackPressed()
        );

        // =====================================================
        // TITLE
        // =====================================================

        String donkeyName =
                getIntent().getStringExtra(
                        "donkeyName"
                );

        if (donkeyName != null &&
                !donkeyName.isEmpty()) {

            binding.text.setText(
                    donkeyName
            );

        } else {

            binding.text.setText(
                    "Donkey Dancing"
            );
        }

        // =====================================================
        // INITIALIZE LIST
        // =====================================================

        youtubeVideos =
                new ArrayList<>();

        // =====================================================
        // LOAD VIDEOS FROM S3
        // =====================================================

        loadVideosFromS3();
    }

    // =========================================================
    // LOAD VIDEOS FROM S3
    // =========================================================

    private void loadVideosFromS3() {

        executor.execute(() -> {

            ArrayList<String> videos =
                    new ArrayList<>();

            HttpURLConnection connection =
                    null;

            try {

                URL url =
                        new URL(
                                VIDEO_LIST_URL
                        );

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod(
                        "GET"
                );

                connection.setConnectTimeout(
                        10000
                );

                connection.setReadTimeout(
                        10000
                );

                connection.setDoInput(
                        true
                );

                connection.connect();

                int responseCode =
                        connection.getResponseCode();

                if (responseCode ==
                        HttpURLConnection.HTTP_OK) {

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            connection.getInputStream()
                                    )
                            );

                    String line;

                    while ((line =
                            reader.readLine()) != null) {

                        line =
                                line.trim();

                        if (!line.isEmpty()) {

                            videos.add(line);
                        }
                    }

                    reader.close();
                }

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (connection != null) {

                    connection.disconnect();
                }
            }

            // =================================================
            // UPDATE UI
            // =================================================

            mainHandler.post(() -> {

                if (isFinishing()) {
                    return;
                }

                if (android.os.Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

                    if (isDestroyed()) {
                        return;
                    }
                }

                youtubeVideos.clear();

                youtubeVideos.addAll(
                        videos
                );

                // =============================================
                // NO VIDEOS
                // =============================================

                if (youtubeVideos.isEmpty()) {

                    return;
                }

                // =============================================
                // SETUP VIEWPAGER
                // =============================================

                setupVideoPager();
            });
        });
    }

    // =========================================================
    // SETUP VIDEO PAGER
    // =========================================================

    private void setupVideoPager() {

        // =====================================================
        // ADAPTER
        // =====================================================

        adapter =
                new YouTubeVideoAdapter(
                        youtubeVideos,
                        getLifecycle()
                );

        // =====================================================
        // VIEWPAGER
        // =====================================================

        binding.viewPager.setOrientation(
                ViewPager2.ORIENTATION_VERTICAL
        );

        binding.viewPager.setAdapter(
                adapter
        );

        /*
         * Keep one page before and after
         * the current page.
         */

        binding.viewPager.setOffscreenPageLimit(
                1
        );

        // =====================================================
        // PAGE CHANGE CALLBACK
        // =====================================================

        pageChangeCallback =
                new ViewPager2.OnPageChangeCallback() {

                    @Override
                    public void onPageSelected(
                            int position) {

                        super.onPageSelected(
                                position
                        );

                        currentPosition =
                                position;

                        // =====================================
                        // PAUSE ALL
                        // =====================================

                        adapter.pauseAll();

                        // =====================================
                        // PLAY CURRENT
                        // =====================================

                        binding.viewPager.post(
                                () -> {

                                    if (adapter != null) {

                                        adapter.playVideoAt(
                                                position
                                        );
                                    }
                                }
                        );
                    }
                };

        binding.viewPager.registerOnPageChangeCallback(
                pageChangeCallback
        );

        // =====================================================
        // PLAY FIRST VIDEO
        // =====================================================

        binding.viewPager.postDelayed(
                () -> {

                    if (adapter != null &&
                            !youtubeVideos.isEmpty()) {

                        adapter.playVideoAt(
                                0
                        );
                    }

                },
                1000
        );
    }

    // =========================================================
    // ACTIVITY PAUSE
    // =========================================================

    @Override
    protected void onPause() {

        if (adapter != null) {

            adapter.pauseAll();
        }

        super.onPause();
    }

    // =========================================================
    // ACTIVITY RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (adapter != null &&
                !youtubeVideos.isEmpty()) {

            binding.viewPager.postDelayed(
                    () -> {

                        if (adapter != null) {

                            adapter.playVideoAt(
                                    currentPosition
                            );
                        }

                    },
                    300
            );
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        // =====================================================
        // REMOVE CALLBACK
        // =====================================================

        if (binding != null) {

            if (pageChangeCallback != null) {

                binding.viewPager
                        .unregisterOnPageChangeCallback(
                                pageChangeCallback
                        );

                pageChangeCallback =
                        null;
            }

            // ================================================
            // REMOVE ADAPTER
            // ================================================

            binding.viewPager.setAdapter(
                    null
            );
        }

        // =====================================================
        // SHUTDOWN NETWORK THREAD
        // =====================================================

        executor.shutdownNow();

        // =====================================================
        // REMOVE PENDING CALLBACKS
        // =====================================================

        mainHandler.removeCallbacksAndMessages(
                null
        );

        super.onDestroy();
    }
}