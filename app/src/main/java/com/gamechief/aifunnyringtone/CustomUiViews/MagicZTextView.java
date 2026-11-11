package com.gamechief.aifunnyringtone.CustomUiViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.gamechief.aifunnyringtone.R;

public class MagicZTextView extends AppCompatTextView {
    private int bgColor;
    private boolean isDrawBackground = false;
    private Integer strokeColor;
    private Paint.Join strokeJoin;
    private float strokeMiter;
    private float strokeWidth;

    public MagicZTextView(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public MagicZTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MagicZTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MagicTextView);
            if (a.hasValue(R.styleable.MagicTextView_strokeColor)) {
                float strokeWidth2 = (float) a.getDimensionPixelSize(R.styleable.MagicTextView_strokeWidth, 1);
                int strokeColor2 = a.getColor(R.styleable.MagicTextView_strokeColor, -16777216);
                float strokeMiter2 = (float) a.getDimensionPixelSize(R.styleable.MagicTextView_strokeMiter, 10);
                Paint.Join strokeJoin2 = null;
                switch (a.getInt(R.styleable.MagicTextView_strokeJoinStyle, 0)) {
                    case 0:
                        strokeJoin2 = Paint.Join.MITER;
                        break;
                    case 1:
                        strokeJoin2 = Paint.Join.BEVEL;
                        break;
                    case 2:
                        strokeJoin2 = Paint.Join.ROUND;
                        break;
                }
                setStroke(strokeWidth2, strokeColor2, strokeJoin2, strokeMiter2);
            }
        }
    }

    public void setStroke(float width, int color, Paint.Join join, float miter) {
        this.strokeWidth = width;
        this.strokeColor = Integer.valueOf(color);
        this.strokeJoin = join;
        this.strokeMiter = miter;
    }

    public void setDrawBackground(boolean value) {
        this.isDrawBackground = value;
    }

    public void setBgColor(int colorId) {
        this.bgColor = colorId;
    }

    public void setStroke(float width, int color) {
        setStroke(width, color, Paint.Join.ROUND, 10.0f);
        invalidate();
    }

    public Integer getStrokeColor() {
        return this.strokeColor;
    }

    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    public Paint.Join getStrokeJoin() {
        return this.strokeJoin;
    }

    public float getStrokeMiter() {
        return this.strokeMiter;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int restoreColor = getCurrentTextColor();
        if (this.strokeColor != null) {
            TextPaint paint = getPaint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(this.strokeJoin);
            paint.setStrokeMiter(this.strokeMiter);
            setTextColor(this.strokeColor.intValue());
            paint.setStrokeWidth(this.strokeWidth);
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
        }
        setTextColor(restoreColor);
        super.onDraw(canvas);
    }

    public static class Shadow {
        int color;
        float dx;
        float dy;
        float r;

        public Shadow() {
        }

        public Shadow(float r2, float dx2, float dy2, int color2) {
            this.r = r2;
            this.dx = dx2;
            this.dy = dy2;
            this.color = color2;
        }

        public float getR() {
            return this.r;
        }

        public float getDx() {
            return this.dx;
        }

        public float getDy() {
            return this.dy;
        }

        public int getColor() {
            return this.color;
        }
    }
}
