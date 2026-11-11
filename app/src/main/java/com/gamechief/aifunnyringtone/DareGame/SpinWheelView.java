package com.gamechief.aifunnyringtone.DareGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinWheelView extends View {
    private List<Integer> colors = new ArrayList();
    private List<String> names = new ArrayList();
    private Paint slicePaint;
    private Paint textPaint;

    public SpinWheelView(Context context) {
        super(context);
        init();
    }

    public SpinWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpinWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.slicePaint = new Paint(1);
        this.textPaint = new Paint(1);
        this.textPaint.setColor(-1);
        this.textPaint.setTextSize(28.0f);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setNames(List<String> list) {
        this.names = list;
        generateColors();
        invalidate();
    }

    private void generateColors() {
        this.colors.clear();
        Random r = new Random();
        for (int i = 0; i < this.names.size(); i++) {
            this.colors.add(Integer.valueOf(Color.rgb(r.nextInt(200), r.nextInt(200), r.nextInt(200))));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.names == null) {
            Canvas canvas2 = canvas;
        } else if (this.names.isEmpty()) {
            Canvas canvas3 = canvas;
        } else {
            int size = Math.min(getWidth(), getHeight());
            float f = 2.0f;
            float radius = ((float) size) / 2.0f;
            float centerX = radius;
            float centerY = radius;
            RectF rect = new RectF(0.0f, 0.0f, (float) size, (float) size);
            float sweep = 360.0f / ((float) this.names.size());
            int i = 0;
            float ty = 0.0f;
            while (i < this.names.size()) {
                this.slicePaint.setColor(this.colors.get(i).intValue());
                Canvas canvas4 = canvas;
                canvas4.drawArc(rect, ty, sweep, true, this.slicePaint);
                float angle = (sweep / f) + ty;
                double rad = Math.toRadians((double) angle);
                RectF rect2 = rect;
                float start = ty;
                float tx = (float) (((double) centerX) + (((double) radius) * 0.6d * Math.cos(rad)));
                float sweep2 = sweep;
                float ty2 = (float) (((double) centerY) + (((double) radius) * 0.6d * Math.sin(rad)));
                canvas4.save();
                canvas4.rotate(90.0f + angle, tx, ty2);
                canvas4.drawText(this.names.get(i), tx, ty2, this.textPaint);
                canvas4.restore();
                ty = start + sweep2;
                i++;
                rect = rect2;
                sweep = sweep2;
                f = 2.0f;
            }
        }
    }
}
