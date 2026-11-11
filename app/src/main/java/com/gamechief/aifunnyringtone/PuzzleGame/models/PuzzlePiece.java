package com.gamechief.aifunnyringtone.PuzzleGame.models;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.gamechief.aifunnyringtone.PuzzleGame.cutter.PieceCurveSet;

public class PuzzlePiece {
    private Point anchorPoint;
    private Point centerPoint;
    private int height;
    private Bitmap image;
    private String path;
    private PieceCurveSet pieceCurveSet;
    private int width;

    public PuzzlePiece(Bitmap image2, String path2, Point anchorPoint2, Point centerPoint2, PieceCurveSet pieceCurveSet2, int width2, int height2) {
        this.image = image2;
        this.anchorPoint = anchorPoint2;
        this.centerPoint = centerPoint2;
        this.pieceCurveSet = pieceCurveSet2;
        this.width = width2;
        this.height = height2;
        this.path = path2;
    }

    public PuzzlePiece(String path2, Point anchorPoint2, Point centerPoint2, PieceCurveSet pieceCurveSet2, int width2, int height2) {
        this.anchorPoint = anchorPoint2;
        this.centerPoint = centerPoint2;
        this.pieceCurveSet = pieceCurveSet2;
        this.width = width2;
        this.height = height2;
        this.path = path2;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path2) {
        this.path = path2;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public Point getAnchorPoint() {
        return this.anchorPoint;
    }

    public Point getCenterPoint() {
        return this.centerPoint;
    }

    public PieceCurveSet getPieceCurveSet() {
        return this.pieceCurveSet;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
