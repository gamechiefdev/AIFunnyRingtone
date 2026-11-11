package com.gamechief.aifunnyringtone.PuzzleGame.models;

import android.graphics.Bitmap;

public class Pieces {
    private Bitmap originalResource;
    private int pX = 0;
    private int pY = 0;
    private int position = 0;

    public int getpX() {
        return this.pX;
    }

    public void setpX(int pX2) {
        this.pX = pX2;
    }

    public int getpY() {
        return this.pY;
    }

    public void setpY(int pY2) {
        this.pY = pY2;
    }

    public Bitmap getOriginalResource() {
        return this.originalResource;
    }

    public void setOriginalResource(Bitmap originalResource2) {
        this.originalResource = originalResource2;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position2) {
        this.position = position2;
    }
}
