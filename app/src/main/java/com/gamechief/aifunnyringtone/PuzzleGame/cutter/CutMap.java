package com.gamechief.aifunnyringtone.PuzzleGame.cutter;

import java.lang.reflect.Array;
import java.util.Random;

public class CutMap {
    private final HorizontalCurve[][] horizontalCurveArray;
    private int horizontalResolution;
    private final VerticalCurve[][] verticalCurveArray;
    private int verticalResolution;

    public CutMap(int horizontalResolution2, int verticalResolution2) {
        this.horizontalResolution = horizontalResolution2;
        this.verticalResolution = verticalResolution2;
        int[] iArr = new int[2];
        iArr[1] = verticalResolution2 - 1;
        iArr[0] = horizontalResolution2;
        this.horizontalCurveArray = (HorizontalCurve[][]) Array.newInstance(HorizontalCurve.class, iArr);
        int[] iArr2 = new int[2];
        iArr2[1] = verticalResolution2;
        iArr2[0] = horizontalResolution2 - 1;
        this.verticalCurveArray = (VerticalCurve[][]) Array.newInstance(VerticalCurve.class, iArr2);
        Random random = new Random();
        for (int i = 0; i < horizontalResolution2; i++) {
            for (int j = 0; j < verticalResolution2 - 1; j++) {
                this.horizontalCurveArray[i][j] = random.nextBoolean() ? HorizontalCurve.SLOT : HorizontalCurve.TAB;
            }
        }
        for (int i2 = 0; i2 < horizontalResolution2 - 1; i2++) {
            for (int j2 = 0; j2 < verticalResolution2; j2++) {
                this.verticalCurveArray[i2][j2] = random.nextBoolean() ? VerticalCurve.SLOT : VerticalCurve.TAB;
            }
        }
    }

    public PieceCurveSet getCurveSetForPiece(int xIndex, int yIndex) {
        return new PieceCurveSet(yIndex == 0 ? HorizontalCurve.NONE : this.horizontalCurveArray[xIndex][yIndex - 1], xIndex == 0 ? VerticalCurve.NONE : this.verticalCurveArray[xIndex - 1][yIndex], xIndex == this.verticalResolution + -1 ? VerticalCurve.NONE : this.verticalCurveArray[xIndex][yIndex], yIndex == this.horizontalResolution + -1 ? HorizontalCurve.NONE : this.horizontalCurveArray[xIndex][yIndex]);
    }

    public HorizontalCurve[][] getHorizontalCurveArray() {
        return this.horizontalCurveArray;
    }

    public VerticalCurve[][] getVerticalCurveArray() {
        return this.verticalCurveArray;
    }

    public int getHorizontalResolution() {
        return this.horizontalResolution;
    }

    public int getVerticalResolution() {
        return this.verticalResolution;
    }
}
