package com.gamechief.aifunnyringtone.PuzzleGame.cutter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class CurveDrawer {
    private static final Point[] horizontalTabCoords = {new Point(0, 0), new Point(35, 15), new Point(37, 5), new Point(40, 0), new Point(38, -5), new Point(20, -20), new Point(50, -20), new Point(80, -20), new Point(62, -5), new Point(60, 0), new Point(63, 5), new Point(65, 15), new Point(100, 0)};
    private static final Point[] verticalTabCoords = {new Point(0, 0), new Point(15, 35), new Point(5, 37), new Point(0, 40), new Point(-5, 38), new Point(-20, 20), new Point(-20, 50), new Point(-20, 80), new Point(-5, 62), new Point(0, 60), new Point(5, 63), new Point(15, 65), new Point(0, 100)};

    public static void drawPuzzlePiece(Canvas canvas, PieceCurveSet pieceCurveSet, Point startPoint, int horizontalStep, int verticalStep, Paint paint) {
        float horizontalScale = ((float) horizontalStep) / 100.0f;
        float verticalScale = ((float) verticalStep) / 100.0f;
        Canvas canvas2 = canvas;
        Point startPoint2 = startPoint;
        Paint paint2 = paint;
        drawHorizontalCurve(pieceCurveSet.top, canvas2, startPoint2, horizontalScale, verticalScale, paint2);
        drawVerticalCurve(pieceCurveSet.left, canvas2, startPoint2, horizontalScale, verticalScale, paint2);
        Point startPoint3 = startPoint2;
        drawHorizontalCurve(pieceCurveSet.bottom, canvas2, new Point(startPoint3.x, startPoint3.y + verticalStep), horizontalScale, verticalScale, paint2);
        drawVerticalCurve(pieceCurveSet.right, canvas2, new Point(startPoint3.x + horizontalStep, startPoint3.y), horizontalScale, verticalScale, paint2);
    }

    public static void drawHorizontalCurve(HorizontalCurve curve, Canvas canvas, Point startPoint, float horizontalScale, float verticalScale, Paint paint) {
        if (curve != HorizontalCurve.NONE) {
            int sign = curve == HorizontalCurve.TAB ? 1 : -1;
            Path path = new Path();
            path.moveTo((float) startPoint.x, (float) startPoint.y);
            for (int i = 0; i + 2 < horizontalTabCoords.length; i += 2) {
                path.cubicTo(((float) startPoint.x) + (((float) horizontalTabCoords[i].x) * horizontalScale), ((float) startPoint.y) + (((float) (horizontalTabCoords[i].y * sign)) * verticalScale), ((float) startPoint.x) + (((float) horizontalTabCoords[i + 1].x) * horizontalScale), ((float) startPoint.y) + (((float) (horizontalTabCoords[i + 1].y * sign)) * verticalScale), ((float) startPoint.x) + (((float) horizontalTabCoords[i + 2].x) * horizontalScale), ((float) startPoint.y) + (((float) (horizontalTabCoords[i + 2].y * sign)) * verticalScale));
            }
            canvas.drawPath(path, paint);
        }
    }

    public static void drawVerticalCurve(VerticalCurve curve, Canvas canvas, Point startPoint, float horizontalScale, float verticalScale, Paint paint) {
        if (curve != VerticalCurve.NONE) {
            int sign = curve == VerticalCurve.TAB ? 1 : -1;
            Path path = new Path();
            path.moveTo((float) startPoint.x, (float) startPoint.y);
            for (int i = 0; i + 2 < verticalTabCoords.length; i += 2) {
                path.cubicTo(((float) startPoint.x) + (((float) (verticalTabCoords[i].x * sign)) * horizontalScale), ((float) startPoint.y) + (((float) verticalTabCoords[i].y) * verticalScale), ((float) startPoint.x) + (((float) (verticalTabCoords[i + 1].x * sign)) * horizontalScale), ((float) startPoint.y) + (((float) verticalTabCoords[i + 1].y) * verticalScale), ((float) startPoint.x) + (((float) (verticalTabCoords[i + 2].x * sign)) * horizontalScale), ((float) startPoint.y) + (((float) verticalTabCoords[i + 2].y) * verticalScale));
            }
            canvas.drawPath(path, paint);
        }
    }
}
