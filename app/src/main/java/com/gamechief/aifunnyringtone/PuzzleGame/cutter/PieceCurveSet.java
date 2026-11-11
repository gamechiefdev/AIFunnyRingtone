package com.gamechief.aifunnyringtone.PuzzleGame.cutter;

public class PieceCurveSet {
    public final HorizontalCurve bottom;
    public final VerticalCurve left;
    public final VerticalCurve right;
    public final HorizontalCurve top;

    public PieceCurveSet(HorizontalCurve top2, VerticalCurve left2, VerticalCurve right2, HorizontalCurve bottom2) {
        this.top = top2;
        this.left = left2;
        this.right = right2;
        this.bottom = bottom2;
    }
}
