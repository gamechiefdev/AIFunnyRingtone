package com.gamechief.aifunnyringtone.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import com.bumptech.glide.Glide;
import com.gamechief.aifunnyringtone.R;

import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityWallpaperPreviewBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperPreview extends ParentActivity {
    ActivityWallpaperPreviewBinding binding;
    int wallpaper;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityWallpaperPreviewBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        showSeldomIntersAd(this);
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        if (Build.VERSION.SDK_INT <= 28 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }
        this.wallpaper = getIntent().getIntExtra("wallpaper", -1);
        Log.d("getWallpaper", "onCardClick: " + this.wallpaper);
        Glide.with((FragmentActivity) this).load(Integer.valueOf(this.wallpaper)).into(this.binding.getImage);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WallpaperPreview.this.onBackPressed();
            }
        });
        binding.setWall.setOnClickListener(view -> {
            Dialog dialog = new Dialog(WallpaperPreview.this);
            dialog.setContentView(R.layout.dialog_set_wall);
            dialog.setCancelable(true);

            RadioGroup wallpaperOptions = dialog.findViewById(R.id.wallpaperOptions);
            ImageView cancelBtn = dialog.findViewById(R.id.cancelBtn);
            ImageView applyBtn = dialog.findViewById(R.id.applyBtn);

            cancelBtn.setOnClickListener(v -> dialog.dismiss());

            applyBtn.setOnClickListener(v -> {
                int selectedId = wallpaperOptions.getCheckedRadioButtonId();
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                BitmapDrawable drawable = (BitmapDrawable) binding.getImage.getDrawable();

                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (selectedId == R.id.lockScreen) {
                                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                Toast.makeText(this, "Lock screen wallpaper set", Toast.LENGTH_SHORT).show();
                            } else if (selectedId == R.id.homeScreen) {
                                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                                Toast.makeText(this, "Home screen wallpaper set", Toast.LENGTH_SHORT).show();
                            } else if (selectedId == R.id.both) {
                                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK | WallpaperManager.FLAG_SYSTEM);
                                Toast.makeText(this, "Both screen wallpaper set", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            wallpaperManager.setBitmap(bitmap);
                            Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.show();
        });


        binding.download.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101
                );
                return;
            }

            Bitmap bitmap = ((BitmapDrawable) binding.getImage.getDrawable()).getBitmap();
            saveImageToGallery(bitmap);
        });



    }



    private String saveImageToGallery(Bitmap bitmap) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Funny Wallpaper");
        if (folder.exists()) {
            saveImageToFile(folder, bitmap);
        } else if (folder.mkdirs()) {
            saveImageToFile(folder, bitmap);
        } else {
            Toast.makeText(this, "Failed to create folder", 0).show();
        }
        return "Funny Wallpaper";
    }

    private void saveImageToFile(File folder, Bitmap bitmap) {
        File imageFile = new File(folder, "Insta_" + System.currentTimeMillis() + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            scanImageFile(imageFile);
            Toast.makeText(this, "Saved Image to gallery", 0).show();
            this.binding.getImage.setDrawingCacheEnabled(false);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image: " + e.getMessage(), 0).show();
        }
    }

    private void scanImageFile(File imageFile) {
        MediaScannerConnection.scanFile (
                this,
                new String[]{imageFile.getAbsolutePath ()},
                null,
                (path, uri) -> {
                    // Image scanning complete
                }
        );
    }




    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 101 || grantResults.length <= 0 || grantResults[0] != 0) {
            Toast.makeText(this, "Storage permission denied", 0).show();
        }
    }
}
