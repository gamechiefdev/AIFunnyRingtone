package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private Context context;
    private ArrayList<Integer> videos;

    public VideoAdapter(Context context, ArrayList<Integer> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);

        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {

        Uri uri = Uri.parse("android.resource://"
                + context.getPackageName()
                + "/"
                + videos.get(position));

        holder.playPause.setVisibility(View.GONE);
        holder.videoView.setVideoURI(uri);

        holder.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            
            // Scaling logic for "Center Crop" effect
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
            float scale = videoRatio / screenRatio;

            if (scale >= 1f) {
                holder.videoView.setScaleX(scale);
            } else {
                holder.videoView.setScaleY(1f / scale);
            }
            
            holder.playPause.setVisibility(View.GONE);
            holder.videoView.start();
        });

        holder.itemView.setOnClickListener(v -> {
            if (holder.videoView.isPlaying()) {
                holder.videoView.pause();
                holder.playPause.setImageResource(R.drawable.pause_donkey);
                holder.playPause.setVisibility(View.VISIBLE);
            } else {
                holder.videoView.start();
                holder.playPause.setImageResource(R.drawable.play_donkey);
                holder.playPause.setVisibility(View.VISIBLE);
                
                // Hide pause icon after a short delay to mimic professional video players
                holder.playPause.postDelayed(() -> {
                    if (holder.videoView.isPlaying()) {
                        holder.playPause.setVisibility(View.GONE);
                    }
                }, 800);
            }
        });

//        holder.fav.setOnClickListener(v -> {
//            // Handle favorite click
//        });

        holder.share.setOnClickListener(v -> {
            shareVideo(videos.get(position), position);
        });
    }

    private void shareVideo(int resId, int position) {
        File videoFile = copyRawResourceToCache(resId, "donkey_video_" + position + ".mp4");
        if (videoFile != null) {
            Uri videoUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider",
                    videoFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/mp4");
            shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share Video"));
        } else {
            Toast.makeText(context, "Failed to share video", Toast.LENGTH_SHORT).show();
        }
    }

    private File copyRawResourceToCache(int resId, String fileName) {
        File cacheDir = new File(context.getCacheDir(), "videos");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        File outFile = new File(cacheDir, fileName);
        try (InputStream is = context.getResources().openRawResource(resId);
             FileOutputStream os = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            return outFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        ImageView playPause, fav, share;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            playPause = itemView.findViewById(R.id.playPauseVideo);
//            fav = itemView.findViewById(R.id.favDonkey);
            share = itemView.findViewById(R.id.shareDonkey);
        }
    }
}
