package com.gamechief.aifunnyringtone.StickerPack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StickerUtils {
    public static Bitmap resizeSticker(Bitmap original) {
        if (original == null) return null;
        if (original.getWidth() == 512 && original.getHeight() == 512) {
            return original;
        }
        Bitmap scaled = Bitmap.createScaledBitmap(original, 512, 512, true);
        if (scaled != original) {
            original.recycle();
        }
        return scaled;
    }

    public static Bitmap resizeTrayIcon(Bitmap original) {
        if (original == null) return null;
        if (original.getWidth() == 96 && original.getHeight() == 96) {
            return original;
        }
        Bitmap scaled = Bitmap.createScaledBitmap(original, 96, 96, true);
        if (scaled != original) {
            original.recycle();
        }
        return scaled;
    }

    public static File saveBitmapAsWebP(Context context, Bitmap bitmap, String fileName) throws IOException {
        if (bitmap == null) throw new IOException("Bitmap is null");
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            file.delete();
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            if (Build.VERSION.SDK_INT >= 30) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out);
            } else {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out);
            }
            out.flush();
        } finally {
            bitmap.recycle();
        }
        return file;
    }

    public static Bitmap loadBitmapFromAssets(Context context, String assetPath, int reqWidth, int reqHeight) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream is = context.getAssets().open(assetPath)) {
            BitmapFactory.decodeStream(is, null, options);
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565; // Save memory

        try (InputStream is = context.getAssets().open(assetPath)) {
            Bitmap result = BitmapFactory.decodeStream(is, null, options);
            if (result == null) {
                throw new IOException("Failed to decode bitmap from " + assetPath);
            }
            return result;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static File prepareSticker(Context context, String assetPath, String outputFileName) throws IOException {
        return saveBitmapAsWebP(context, resizeSticker(loadBitmapFromAssets(context, assetPath, 512, 512)), outputFileName);
    }

    public static File prepareTrayIcon(Context context, String assetPath, String outputFileName) throws IOException {
        return saveBitmapAsWebP(context, resizeTrayIcon(loadBitmapFromAssets(context, assetPath, 96, 96)), outputFileName);
    }

    public static File resizeAndSaveImage(Context context, String folderName, String assetFileName, int width, int height, File outFile) throws IOException {
        Bitmap sampled = loadBitmapFromAssets(context, "stickers/" + assetFileName, width, height);
        Bitmap scaled = Bitmap.createScaledBitmap(sampled, width, height, true);
        if (scaled != sampled) {
            sampled.recycle();
        }
        try (FileOutputStream out = new FileOutputStream(outFile)) {
            scaled.compress(Bitmap.CompressFormat.WEBP, 100, out);
            out.flush();
            return outFile;
        } finally {
            scaled.recycle();
        }
    }

    public static String[] listAssetFiles(Context context, String path) throws IOException {
        return context.getAssets().list(path);
    }
}
