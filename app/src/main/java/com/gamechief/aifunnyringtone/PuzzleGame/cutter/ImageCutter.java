package com.gamechief.aifunnyringtone.PuzzleGame.cutter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.Pair;
import androidx.core.internal.view.SupportMenu;

import com.gamechief.aifunnyringtone.PuzzleGame.models.PuzzlePiece;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.Queue;

public class ImageCutter {
    private CutMap cutMap;
    private Paint cutterPaint = new Paint();
    private float horizontalScale;
    private int horizontalStep;
    private Bitmap originalImage;
    private String path = "";
    private float verticalScale;
    private int verticalStep;

    @SuppressLint("RestrictedApi")
    public ImageCutter(Bitmap originalImage2, CutMap cutMap2) {
        this.originalImage = originalImage2;
        this.cutMap = cutMap2;
        this.horizontalStep = originalImage2.getWidth() / cutMap2.getHorizontalResolution();
        this.verticalStep = originalImage2.getHeight() / cutMap2.getVerticalResolution();
        
        // Fix: Initialize scales AFTER steps are calculated
        this.horizontalScale = ((float) this.horizontalStep) / 100.0f;
        this.verticalScale = ((float) this.verticalStep) / 100.0f;
        
        this.cutterPaint.setColor(SupportMenu.CATEGORY_MASK);
        this.cutterPaint.setStrokeWidth(2.0f);
        this.cutterPaint.setStyle(Paint.Style.STROKE);
    }

    public PuzzlePiece[][] cutImage() {
        int horizontalResolution = this.cutMap.getHorizontalResolution();
        int verticalResolution = this.cutMap.getVerticalResolution();
        
        PuzzlePiece[][] puzzlePieces = (PuzzlePiece[][]) Array.newInstance(PuzzlePiece.class, horizontalResolution, verticalResolution);
        
        for (int i = 0; i < horizontalResolution; i++) {
            for (int j = 0; j < verticalResolution; j++) {
                PieceCurveSet pieceCurveSet = this.cutMap.getCurveSetForPiece(i, j);

                int left = i * horizontalStep;
                int top = j * verticalStep;

                int width = horizontalStep;
                int height = verticalStep;

// Fix last column & row
                if (i == horizontalResolution - 1) {
                    width = originalImage.getWidth() - left;
                }
                if (j == verticalResolution - 1) {
                    height = originalImage.getHeight() - top;
                }

// ✅ Direct crop (FULL PIECE — no cutting issue)
                Bitmap pieceBitmap = Bitmap.createBitmap(originalImage, left, top, width, height);

// ✅ Create puzzle piece
                puzzlePieces[i][j] = new PuzzlePiece(
                        pieceBitmap,
                        this.path,
                        new Point(left + width / 2, top + height / 2),
                        new Point(width / 2, height / 2),
                        pieceCurveSet,
                        width,
                        height
                );
            }
        }
        return puzzlePieces;
    }

    @SuppressLint("RestrictedApi")
    public Bitmap drawSinglePuzzlePieceMask(int xIndex, int yIndex, PieceCurveSet pieceCurveSet) {
        int currentHorizontalStep;
        int currentVerticalStep;
        Bitmap maskLayer = Bitmap.createBitmap(this.originalImage.getWidth(), this.originalImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(maskLayer);
        Point startPoint = new Point(this.horizontalStep * xIndex, this.verticalStep * yIndex);
        
        if (xIndex == this.cutMap.getHorizontalResolution() - 1) {
            currentHorizontalStep = maskLayer.getWidth() - startPoint.x;
        } else {
            currentHorizontalStep = this.horizontalStep;
        }
        
        if (yIndex == this.cutMap.getVerticalResolution() - 1) {
            currentVerticalStep = maskLayer.getHeight() - startPoint.y;
        } else {
            currentVerticalStep = this.verticalStep;
        }
        
        CurveDrawer.drawPuzzlePiece(canvas, pieceCurveSet, startPoint, currentHorizontalStep, currentVerticalStep, this.cutterPaint);
        
        // Flood fill is slow, but we'll keep it for now and ensure it targets the right area
        floodFill(maskLayer, new Point(startPoint.x + (this.horizontalStep / 2), startPoint.y + (this.verticalStep / 2)), 0, -16711936);
        
        if (xIndex == 0 && yIndex == 0) {
            floodFill(maskLayer, new Point(startPoint.x + this.horizontalStep, startPoint.y + this.verticalStep), SupportMenu.CATEGORY_MASK, 0);
        } else {
            floodFill(maskLayer, startPoint, SupportMenu.CATEGORY_MASK, 0);
        }
        
        return maskLayer;
    }

    private Bitmap maskPuzzlePiece(Bitmap mask) {
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(this.originalImage, 0.0f, 0.0f, null);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(mask, 0.0f, 0.0f, paint);
        return result;
    }

    private Bitmap cutPuzzlePiece(Bitmap original, Rect border) {
        // Ensure bounds are within bitmap
        int left = Math.max(0, border.left);
        int top = Math.max(0, border.top);
        int width = Math.min(border.width(), original.getWidth() - left);
        int height = Math.min(border.height(), original.getHeight() - top);
        return Bitmap.createBitmap(original, left, top, width, height);
    }

    private static void floodFill(Bitmap image, Point node, int targetColor, int replacementColor) {
        if (targetColor == replacementColor) return;
        
        int width = image.getWidth();
        int height = image.getHeight();
        if (node.x < 0 || node.x >= width || node.y < 0 || node.y >= height) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int x = p.x;
            int y = p.y;

            if (image.getPixel(x, y) != targetColor) continue;

            // Fill left
            int left = x;
            while (left > 0 && image.getPixel(left - 1, y) == targetColor) {
                left--;
            }

            // Fill right
            int right = x;
            while (right < width - 1 && image.getPixel(right + 1, y) == targetColor) {
                right++;
            }

            for (int i = left; i <= right; i++) {
                image.setPixel(i, y, replacementColor);
                if (y > 0 && image.getPixel(i, y - 1) == targetColor) {
                    queue.add(new Point(i, y - 1));
                }
                if (y < height - 1 && image.getPixel(i, y + 1) == targetColor) {
                    queue.add(new Point(i, y + 1));
                }
            }
        }
    }
}
