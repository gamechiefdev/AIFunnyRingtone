package com.gamechief.aifunnyringtone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class YouTubeVideoAdapter
        extends RecyclerView.Adapter<YouTubeVideoAdapter.VideoViewHolder> {

    private final ArrayList<String> videoIds;
    private final Lifecycle lifecycle;

    private final Map<Integer, VideoViewHolder> holders =
            new HashMap<>();

    public YouTubeVideoAdapter(
            ArrayList<String> videoIds,
            Lifecycle lifecycle) {

        this.videoIds = videoIds;
        this.lifecycle = lifecycle;
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_youtube_video,
                        parent,
                        false
                );

        return new VideoViewHolder(
                view,
                lifecycle
        );
    }

    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull VideoViewHolder holder,
            int position) {

        String videoId = extractVideoId(
                videoIds.get(position)
        );

        holder.loadVideo(videoId);

        holders.put(position, holder);
    }

    // =========================================================
    // RECYCLE VIEW HOLDER
    // =========================================================

    @Override
    public void onViewRecycled(
            @NonNull VideoViewHolder holder) {

        Iterator<Map.Entry<Integer, VideoViewHolder>> iterator =
                holders.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<Integer, VideoViewHolder> entry =
                    iterator.next();

            if (entry.getValue() == holder) {
                iterator.remove();
            }
        }

        holder.pause();

        super.onViewRecycled(holder);
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {
        return videoIds.size();
    }

    // =========================================================
    // PLAY VIDEO AT POSITION
    // =========================================================

    public void playVideoAt(int position) {

        VideoViewHolder holder =
                holders.get(position);

        if (holder != null) {
            holder.play();
        }
    }

    // =========================================================
    // PAUSE VIDEO AT POSITION
    // =========================================================

    public void pauseVideoAt(int position) {

        VideoViewHolder holder =
                holders.get(position);

        if (holder != null) {
            holder.pause();
        }
    }

    // =========================================================
    // PAUSE ALL VIDEOS
    // =========================================================

    public void pauseAll() {

        for (VideoViewHolder holder :
                holders.values()) {

            holder.pause();
        }
    }

    // =========================================================
    // EXTRACT YOUTUBE VIDEO ID
    // =========================================================

    private String extractVideoId(String url) {

        if (url == null) {
            return "";
        }

        url = url.trim();

        if (url.isEmpty()) {
            return "";
        }

        // =====================================================
        // ALREADY A VIDEO ID
        // =====================================================

        if (!url.contains("/")
                && !url.contains("?")
                && !url.contains("=")
                && url.length() <= 20) {

            return url;
        }

        try {

            // =================================================
            // YOUTUBE SHORTS
            // =================================================

            if (url.contains("/shorts/")) {

                String id =
                        url.substring(
                                url.indexOf("/shorts/") + 8
                        );

                if (id.contains("?")) {

                    id = id.substring(
                            0,
                            id.indexOf("?")
                    );
                }

                if (id.contains("&")) {

                    id = id.substring(
                            0,
                            id.indexOf("&")
                    );
                }

                return id.trim();
            }

            // =================================================
            // YOUTU.BE
            // =================================================

            if (url.contains("youtu.be/")) {

                String id =
                        url.substring(
                                url.indexOf("youtu.be/") + 9
                        );

                if (id.contains("?")) {

                    id = id.substring(
                            0,
                            id.indexOf("?")
                    );
                }

                if (id.contains("&")) {

                    id = id.substring(
                            0,
                            id.indexOf("&")
                    );
                }

                return id.trim();
            }

            // =================================================
            // YOUTUBE WATCH URL
            // =================================================

            if (url.contains("v=")) {

                String id =
                        url.substring(
                                url.indexOf("v=") + 2
                        );

                if (id.contains("&")) {

                    id = id.substring(
                            0,
                            id.indexOf("&")
                    );
                }

                if (id.contains("#")) {

                    id = id.substring(
                            0,
                            id.indexOf("#")
                    );
                }

                return id.trim();
            }

            // =================================================
            // YOUTUBE EMBED
            // =================================================

            if (url.contains("/embed/")) {

                String id =
                        url.substring(
                                url.indexOf("/embed/") + 7
                        );

                if (id.contains("?")) {

                    id = id.substring(
                            0,
                            id.indexOf("?")
                    );
                }

                if (id.contains("&")) {

                    id = id.substring(
                            0,
                            id.indexOf("&")
                    );
                }

                return id.trim();
            }

        } catch (Exception ignored) {
        }

        return "";
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class VideoViewHolder
            extends RecyclerView.ViewHolder {

        private final YouTubePlayerView playerView;

        private YouTubePlayer youtubePlayer;

        private String currentVideoId = "";

        private boolean playerReady = false;

        private boolean shouldPlay = false;

        public VideoViewHolder(
                @NonNull View itemView,
                Lifecycle lifecycle) {

            super(itemView);

            playerView =
                    itemView.findViewById(
                            R.id.youtubePlayerView
                    );

            // =================================================
            // IMPORTANT
            // =================================================
            //
            // We manually call initialize().
            // Therefore automatic initialization
            // MUST be disabled.
            //

            playerView.setEnableAutomaticInitialization(
                    false
            );

            // =================================================
            // LIFECYCLE
            // =================================================

            if (lifecycle != null) {

                lifecycle.addObserver(
                        playerView
                );
            }

            // =================================================
            // INITIALIZE
            // =================================================

            initializePlayer();
        }

        // =====================================================
        // INITIALIZE PLAYER
        // =====================================================

        private void initializePlayer() {

            playerView.initialize(
                    new AbstractYouTubePlayerListener() {

                        @Override
                        public void onReady(
                                @NonNull YouTubePlayer player) {

                            youtubePlayer =
                                    player;

                            playerReady =
                                    true;

                            // =================================================
                            // DEFAULT YOUTUBE PLAYER UI
                            // =================================================
                            //
                            // Do NOT use DefaultPlayerUiController.
                            //
                            // The YouTubePlayerView will use its
                            // default UI / seekbar.
                            //

                            // =================================================
                            // LOAD VIDEO
                            // =================================================

                            if (!currentVideoId.isEmpty()) {

                                if (shouldPlay) {

                                    player.loadVideo(
                                            currentVideoId,
                                            0
                                    );

                                } else {

                                    player.cueVideo(
                                            currentVideoId,
                                            0
                                    );
                                }
                            }
                        }

                        // =====================================================
                        // PLAYER STATE
                        // =====================================================

                        @Override
                        public void onStateChange(
                                @NonNull YouTubePlayer player,
                                @NonNull PlayerConstants.PlayerState state) {

                            if (state ==
                                    PlayerConstants.PlayerState.ENDED) {

                                shouldPlay =
                                        false;
                            }
                        }
                    },
                    true
            );
        }

        // =====================================================
        // LOAD VIDEO
        // =====================================================

        public void loadVideo(
                String videoId) {

            if (videoId == null ||
                    videoId.isEmpty()) {

                return;
            }

            currentVideoId =
                    videoId;

            shouldPlay =
                    false;

            if (playerReady &&
                    youtubePlayer != null) {

                youtubePlayer.cueVideo(
                        videoId,
                        0
                );
            }
        }

        // =====================================================
        // PLAY
        // =====================================================

        public void play() {

            shouldPlay =
                    true;

            if (!playerReady ||
                    youtubePlayer == null ||
                    currentVideoId.isEmpty()) {

                return;
            }

            youtubePlayer.loadVideo(
                    currentVideoId,
                    0
            );
        }

        // =====================================================
        // PAUSE
        // =====================================================

        public void pause() {

            shouldPlay =
                    false;

            if (playerReady &&
                    youtubePlayer != null) {

                youtubePlayer.pause();
            }
        }
    }
}