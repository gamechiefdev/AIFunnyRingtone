package com.gamechief.aifunnyringtone.CustomUiViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.gamechief.aifunnyringtone.Interfaces.OnWaveStuffListener;
import com.gamechief.aifunnyringtone.R;
import java.lang.ref.WeakReference;

public class HeartWaveView extends View {
    public static final int DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#443030d5");
    public static final int DEFAULT_BORDER_COLOR = Color.parseColor("#000000");
    private static final float DEFAULT_BORDER_WIDTH = 5.0f;
    private static final boolean DEFAULT_ENABLE_ANIMATION = false;
    public static final int DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#FF3030d5");
    private static final boolean DEFAULT_HIDE_TEXT = false;
    private static final int DEFAULT_MAX = 1000;
    private static final float DEFAULT_PADDING = 0.0f;
    private static final int DEFAULT_PROGRESS = 405;
    private static final int DEFAULT_SPIKE_COUNT = 5;
    private static final int DEFAULT_STRONG = 50;
    public static final int DEFAULT_TEXT_COLOR = Color.parseColor("#000000");
    /* access modifiers changed from: private */
    public Handler animHandler;
    /* access modifiers changed from: private */
    public boolean isAnimation;
    private boolean isHideText;
    private int mBehindWaveColor;
    private int mBorderColor;
    private Paint mBorderPaint;
    private float mBorderWidth;
    private int mFrontWaveColor;
    private OnWaveStuffListener mListener;
    private int mMax;
    private Path mPathBorder;
    private Path mPathContent;
    private int mProgress;
    private Shape mShape;
    private float mShapePadding;
    private int mSpikes;
    private int mStrong;
    private int mTextColor;
    private Paint mViewPaint;
    private Paint mWavePaint1;
    private Paint mWavePaint2;
    private Point screenSize;
    /* access modifiers changed from: private */
    public float shiftX1;
    /* access modifiers changed from: private */
    public int speed;
    private HandlerThread thread;
    /* access modifiers changed from: private */
    public Handler uiHandler;
    private int waveOffset;
    /* access modifiers changed from: private */
    public float waveVector;

    public enum Shape {
        CIRCLE(1),
        SQUARE(2),
        HEART(3),
        STAR(4);
        
        int value;

        private Shape(int value2) {
            this.value = value2;
        }

        static Shape fromValue(int value2) {
            for (Shape shape : values()) {
                if (shape.value == value2) {
                    return shape;
                }
            }
            return CIRCLE;
        }
    }

    public HeartWaveView(Context context) {
        this(context, (AttributeSet) null);
    }

    public HeartWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.shiftX1 = 0.0f;
        this.waveVector = -0.25f;
        this.waveOffset = 25;
        this.speed = 25;
        this.thread = new HandlerThread("HeartWaveView" + hashCode());
        this.mBorderPaint = new Paint();
        this.mViewPaint = new Paint();
        this.mWavePaint1 = new Paint();
        this.mWavePaint2 = new Paint();
        this.mShapePadding = 0.0f;
        this.mProgress = DEFAULT_PROGRESS;
        this.mMax = 1000;
        this.mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;
        this.mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
        this.mBorderColor = DEFAULT_BORDER_COLOR;
        this.mBorderWidth = DEFAULT_BORDER_WIDTH;
        this.mTextColor = DEFAULT_TEXT_COLOR;
        this.isAnimation = false;
        this.isHideText = false;
        this.mStrong = 50;
        this.mSpikes = 5;
        this.mShape = Shape.CIRCLE;
        this.screenSize = new Point(0, 0);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YPWaveView, defStyleAttr, 0);
        this.mFrontWaveColor = attributes.getColor(R.styleable.YPWaveView_frontColor, DEFAULT_FRONT_WAVE_COLOR);
        this.mBehindWaveColor = attributes.getColor(R.styleable.YPWaveView_behideColor, DEFAULT_BEHIND_WAVE_COLOR);
        this.mBorderColor = attributes.getColor(R.styleable.YPWaveView_borderColor, DEFAULT_BORDER_COLOR);
        this.mTextColor = attributes.getColor(R.styleable.YPWaveView_textColor, DEFAULT_TEXT_COLOR);
        this.mProgress = attributes.getInt(R.styleable.YPWaveView_progress, DEFAULT_PROGRESS);
        this.mMax = attributes.getInt(R.styleable.YPWaveView_max, 1000);
        this.mBorderWidth = attributes.getDimension(R.styleable.YPWaveView_borderWidthSize, DEFAULT_BORDER_WIDTH);
        this.mStrong = attributes.getInt(R.styleable.YPWaveView_strong, 50);
        this.mShape = Shape.fromValue(attributes.getInt(R.styleable.YPWaveView_shapeType, 1));
        this.mShapePadding = attributes.getDimension(R.styleable.YPWaveView_shapePadding, 0.0f);
        this.isAnimation = attributes.getBoolean(R.styleable.YPWaveView_animatorEnable, false);
        this.isHideText = attributes.getBoolean(R.styleable.YPWaveView_textHidden, false);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        this.mBorderPaint.setColor(this.mBorderColor);
        this.mWavePaint1 = new Paint();
        this.mWavePaint1.setStrokeWidth(2.0f);
        this.mWavePaint1.setAntiAlias(true);
        this.mWavePaint1.setColor(this.mBehindWaveColor);
        this.mWavePaint2 = new Paint();
        this.mWavePaint2.setStrokeWidth(2.0f);
        this.mWavePaint2.setAntiAlias(true);
        this.mWavePaint2.setColor(this.mFrontWaveColor);
        this.thread.start();
        this.animHandler = new Handler(this.thread.getLooper());
        this.uiHandler = new UIHandler(new WeakReference(this));
        this.screenSize = new Point(getWidth() / 2, getHeight() / 2);
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setProgress(int progress) {
        if (progress <= this.mMax) {
            if (this.mListener != null) {
                this.mListener.onStuffing(progress, this.mMax);
            }
            this.mProgress = progress;
            createShader();
            Message.obtain(this.uiHandler).sendToTarget();
        }
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void startAnimation() {
        this.isAnimation = true;
        if (getWidth() > 0 && getHeight() > 0) {
            this.animHandler.removeCallbacksAndMessages((Object) null);
            this.animHandler.post(new Runnable() {
                public void run() {
                    HeartWaveView heartWaveView = HeartWaveView.this;
                    heartWaveView.shiftX1 = heartWaveView.shiftX1 + HeartWaveView.this.waveVector;
                    HeartWaveView.this.createShader();
                    Message.obtain(HeartWaveView.this.uiHandler).sendToTarget();
                    if (HeartWaveView.this.isAnimation) {
                        HeartWaveView.this.animHandler.postDelayed(this, (long) HeartWaveView.this.speed);
                    }
                }
            });
        }
    }

    public void stopAnimation() {
        this.isAnimation = false;
    }

    public OnWaveStuffListener getListener() {
        return this.mListener;
    }

    public void setListener(OnWaveStuffListener mListener2) {
        this.mListener = mListener2;
    }

    public void setMax(int max) {
        if (this.mMax != max && max >= this.mProgress) {
            this.mMax = max;
            createShader();
            Message.obtain(this.uiHandler).sendToTarget();
        }
    }

    public int getMax() {
        return this.mMax;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        this.mBorderPaint.setColor(this.mBorderColor);
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setFrontWaveColor(int color) {
        this.mFrontWaveColor = color;
        this.mWavePaint2.setColor(this.mFrontWaveColor);
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setBehindWaveColor(int color) {
        this.mBehindWaveColor = color;
        this.mWavePaint1.setColor(this.mBehindWaveColor);
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setBorderWidth(float width) {
        this.mBorderWidth = width;
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        resetShapes();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setShapePadding(float padding) {
        this.mShapePadding = padding;
        resetShapes();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setAnimationSpeed(int speed2) {
        if (speed2 >= 0) {
            this.speed = speed2;
            Message.obtain(this.uiHandler).sendToTarget();
            return;
        }
        throw new IllegalArgumentException("The speed must be greater than 0.");
    }

    public void setWaveVector(float offset) {
        if (offset < 0.0f || offset > 100.0f) {
            throw new IllegalArgumentException("The vector of wave must be between 0 and 100.");
        }
        this.waveVector = (offset - 50.0f) / 50.0f;
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setHideText(boolean hidden) {
        this.isHideText = hidden;
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setStarSpikes(int count) {
        if (count >= 3) {
            this.mSpikes = count;
            if (Math.min(this.screenSize.x, this.screenSize.y) != 0) {
                resetShapes();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("The number of spikes must be greater than 3.");
    }

    public void setWaveOffset(int offset) {
        this.waveOffset = offset;
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setWaveStrong(int strong) {
        this.mStrong = strong;
        createShader();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    public void setShape(Shape shape) {
        this.mShape = shape;
        resetShapes();
        Message.obtain(this.uiHandler).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenSize = new Point(w, h);
        resetShapes();
        if (this.isAnimation) {
            startAnimation();
        }
    }

    private void resetShapes() {
        HeartWaveView heartWaveView;
        int radius = Math.min(this.screenSize.x, this.screenSize.y);
        int cx = (this.screenSize.x - radius) / 2;
        int cy = (this.screenSize.y - radius) / 2;
        switch (this.mShape.ordinal()) {
            case 0:
                heartWaveView = this;
                heartWaveView.mPathBorder = drawCircle(cx, cy, radius);
                heartWaveView.mPathContent = drawCircle((((int) heartWaveView.mShapePadding) / 2) + cx, (((int) heartWaveView.mShapePadding) / 2) + cy, radius - ((int) heartWaveView.mShapePadding));
                break;
            case 1:
                heartWaveView = this;
                heartWaveView.mPathBorder = drawSquare(cx, cy, radius);
                heartWaveView.mPathContent = drawSquare((((int) heartWaveView.mShapePadding) / 2) + cx, (((int) heartWaveView.mShapePadding) / 2) + cy, radius - ((int) heartWaveView.mShapePadding));
                break;
            case 2:
                heartWaveView = this;
                heartWaveView.mPathBorder = drawHeart(cx, cy, radius);
                heartWaveView.mPathContent = drawHeart((((int) heartWaveView.mShapePadding) / 2) + cx, (((int) heartWaveView.mShapePadding) / 2) + cy, radius - ((int) heartWaveView.mShapePadding));
                break;
            case 3:
                heartWaveView = this;
                heartWaveView.mPathBorder = heartWaveView.drawStart((radius / 2) + cx, (radius / 2) + cy + ((int) this.mBorderWidth), this.mSpikes, (radius / 2) - ((int) this.mBorderWidth), radius / 4);
                heartWaveView.mPathContent = heartWaveView.drawStart((radius / 2) + cx, ((int) heartWaveView.mBorderWidth) + (radius / 2) + cy, heartWaveView.mSpikes, ((radius / 2) - ((int) heartWaveView.mBorderWidth)) - ((int) heartWaveView.mShapePadding), (radius / 4) - ((int) heartWaveView.mShapePadding));
                break;
            default:
                heartWaveView = this;
                break;
        }
        createShader();
        Message.obtain(heartWaveView.uiHandler).sendToTarget();
    }

    private Path drawSquare(int cx, int cy, int radius) {
        Path path = new Path();
        path.moveTo((float) cx, ((float) cy) + (this.mBorderWidth / 2.0f));
        path.lineTo((float) cx, ((float) (radius + cy)) - this.mBorderWidth);
        path.lineTo((float) (radius + cx), ((float) (radius + cy)) - this.mBorderWidth);
        path.lineTo((float) (radius + cx), ((float) cy) + this.mBorderWidth);
        path.lineTo((float) cx, ((float) cy) + this.mBorderWidth);
        path.close();
        return path;
    }

    private Path drawCircle(int cx, int cy, int radius) {
        Path path = new Path();
        path.addCircle((float) ((radius / 2) + cx), (float) ((radius / 2) + cy), ((float) (radius / 2)) - this.mBorderWidth, Path.Direction.CCW);
        path.close();
        return path;
    }

    private Path drawHeart(int cx, int cy, int radius) {
        Path path = new Path();
        path.moveTo((float) ((radius / 2) + cx), (float) ((radius / 5) + cy));
        path.cubicTo((float) (((radius * 5) / 14) + cx), (float) cy, (float) cx, (float) ((radius / 15) + cy), (float) ((radius / 28) + cx), (float) (((radius * 2) / 5) + cy));
        path.cubicTo((float) ((radius / 14) + cx), (float) (((radius * 2) / 3) + cy), (float) (((radius * 3) / 7) + cx), (float) (((radius * 5) / 6) + cy), (float) ((radius / 2) + cx), (float) (((radius * 9) / 10) + cy));
        path.cubicTo((float) (((radius * 4) / 7) + cx), (float) (((radius * 5) / 6) + cy), (float) (((radius * 13) / 14) + cx), (float) (((radius * 2) / 3) + cy), (float) (((radius * 27) / 28) + cx), (float) (((radius * 2) / 5) + cy));
        path.cubicTo((float) (radius + cx), (float) ((radius / 15) + cy), (float) (((radius * 9) / 14) + cx), (float) cy, (float) ((radius / 2) + cx), (float) ((radius / 5) + cy));
        path.close();
        return path;
    }

    private Path drawStart(int cx, int cy, int spikes, int outerRadius, int innerRadius) {
        Path path = new Path();
        double rot = 4.71238898038469d;
        double step = 3.141592653589793d / ((double) spikes);
        path.moveTo((float) cx, (float) (cy - outerRadius));
        for (int i = 0; i < spikes; i++) {
            path.lineTo(((float) cx) + (((float) Math.cos(rot)) * ((float) outerRadius)), ((float) cy) + (((float) Math.sin(rot)) * ((float) outerRadius)));
            double rot2 = rot + step;
            path.lineTo(((float) cx) + (((float) Math.cos(rot2)) * ((float) innerRadius)), ((float) cy) + (((float) Math.sin(rot2)) * ((float) innerRadius)));
            rot = rot2 + step;
        }
        path.lineTo((float) cx, (float) (cy - outerRadius));
        path.close();
        return path;
    }

    /* access modifiers changed from: private */
    public void createShader() {
        if (this.screenSize.x > 0 && this.screenSize.y > 0) {
            int x1 = Math.min(this.screenSize.x, this.screenSize.y);
            double w = 6.283185307179586d / ((double) x1);
            Bitmap bitmap = Bitmap.createBitmap(x1, x1, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            float level = ((((float) (this.mMax - this.mProgress)) / ((float) this.mMax)) * ((float) x1)) + ((((float) this.screenSize.y) / 2.0f) - (((float) x1) / 2.0f));
            int x2 = x1 + 1;
            int y2 = x1 + 1;
            float shiftX2 = this.shiftX1 + ((((float) x1) * (((float) (this.waveOffset - 50)) / 100.0f)) / (((float) x1) / 6.25f));
            int waveLevel = (this.mStrong * (x1 / 20)) / 100;
            int x12 = 0;
            while (x12 < x2) {
                int viewSize = x1;
                double w2 = w;
                float y1 = (float) ((((double) waveLevel) * Math.sin((w2 * ((double) x12)) + ((double) this.shiftX1))) + ((double) level));
                int waveLevel2 = waveLevel;
                int i = x12;
                float y12 = y1;
                int x13 = i;
                canvas.drawLine((float) x12, y12, (float) x12, (float) y2, this.mWavePaint1);
                int waveLevel3 = waveLevel2;
                canvas.drawLine((float) x13, (float) ((((double) waveLevel2) * Math.sin((((double) x13) * w2) + ((double) shiftX2))) + ((double) level)), (float) x13, (float) y2, this.mWavePaint2);
                x12 = x13 + 1;
                x1 = viewSize;
                w = w2;
                waveLevel = waveLevel3;
            }
            int viewSize2 = x1;
            double d = w;
            int i2 = waveLevel;
            this.mViewPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP));
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (this.animHandler != null) {
            this.animHandler.removeCallbacksAndMessages((Object) null);
        }
        if (this.thread != null) {
            this.thread.quit();
        }
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawPath(this.mPathContent, this.mViewPaint);
        if (this.mBorderWidth > 0.0f) {
            canvas.drawPath(this.mPathBorder, this.mBorderPaint);
        }
        if (!this.isHideText) {
            String text = String.valueOf(this.mProgress);
            TextPaint textPaint = new TextPaint();
            TextPaint textPaint2 = new TextPaint();
            textPaint.setColor(this.mTextColor);
            if (this.mShape == Shape.STAR) {
                textPaint.setTextSize((((float) Math.min(this.screenSize.x, this.screenSize.y)) / 2.0f) / 3.0f);
            } else {
                textPaint.setTextSize((((float) Math.min(this.screenSize.x, this.screenSize.y)) / 2.0f) / 2.0f);
            }
            textPaint.setAntiAlias(true);
            canvas.drawText(text, (((float) this.screenSize.x) - textPaint.measureText(text)) / 2.0f, (((float) this.screenSize.y) - (textPaint.descent() + textPaint.ascent())) / 2.0f, textPaint);
            float txtH = (((float) Math.min(this.screenSize.x, this.screenSize.y)) / 2.0f) / DEFAULT_BORDER_WIDTH;
            textPaint2.setTextSize(txtH);
            textPaint2.setColor(-1);
            textPaint2.setAntiAlias(true);
            canvas.drawText("Days", (float) ((int) ((((float) this.screenSize.x) - textPaint2.measureText("Days")) / 2.0f)), (float) ((int) (((((float) this.screenSize.y) - txtH) / 2.0f) + ((((float) Math.min(this.screenSize.x, this.screenSize.y)) / 2.0f) / 2.0f))), textPaint2);
        }
    }

    private static class UIHandler extends Handler {
        private final View mView;

        UIHandler(WeakReference<View> view) {
            super(Looper.getMainLooper());
            this.mView = (View) view.get();
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.mView != null) {
                this.mView.invalidate();
            }
        }
    }
}
