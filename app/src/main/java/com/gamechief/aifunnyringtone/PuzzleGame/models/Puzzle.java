package com.gamechief.aifunnyringtone.PuzzleGame.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import com.gamechief.aifunnyringtone.PuzzleGame.cutter.CutMap;
import com.gamechief.aifunnyringtone.PuzzleGame.cutter.ImageCutter;

import java.io.File;
import java.util.ArrayList;

public class Puzzle {
    Activity mActivity;
    public Bitmap mSourceImage = null;
    ArrayList<PuzzlePiece> puzzlePieceArrayList;

    public ArrayList<PuzzlePiece> createPuzzlePieces(Activity aActivity, int imageResId, int width, int height, ImageView imageView, String path, int horizontalResolution, int verticalResolution) {
        this.mActivity = aActivity;
        this.puzzlePieceArrayList = new ArrayList<>();
        getDisplaySize(imageResId, width, height, imageView);
        deleteDirectories(path);
        
        if (this.mSourceImage != null) {
            CutMap cutMap = new CutMap(horizontalResolution, verticalResolution);
            drawOrderedPuzzlePieces(new ImageCutter(this.mSourceImage, cutMap), cutMap);
//            this.mSourceImage = null;
        }
        return this.puzzlePieceArrayList;
    }

    private void getDisplaySize(int imageResId, int widthFinal, int heightFinal, ImageView imageView) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap original = BitmapFactory.decodeResource(
                    this.mActivity.getResources(),
                    imageResId,
                    options
            );

            if (original != null) {
                this.mSourceImage = Bitmap.createScaledBitmap(
                        original,
                        widthFinal,
                        heightFinal,
                        true
                );

                // ✅ SHOW IMAGE IN FRAME
                if (imageView != null) {
                    imageView.post(() -> imageView.setImageBitmap(this.mSourceImage));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void deleteDirectories(String aPath) {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + aPath);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
            return;
        }
        dir.delete();
        dir.mkdir();
    }

    private void drawOrderedPuzzlePieces(ImageCutter imageCutter, CutMap cutMap) {
        PuzzlePiece[][] puzzlePieces = imageCutter.cutImage();
        for (int i = 0; i < cutMap.getHorizontalResolution(); i++) {
            for (int j = 0; j < cutMap.getVerticalResolution(); j++) {
                this.puzzlePieceArrayList.add(puzzlePieces[i][j]);
            }
        }
    }
}
