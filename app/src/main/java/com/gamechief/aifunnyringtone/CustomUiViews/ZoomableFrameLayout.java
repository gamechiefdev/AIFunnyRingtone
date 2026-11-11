package com.gamechief.aifunnyringtone.CustomUiViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

public class ZoomableFrameLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {
    private static final float MAX_ZOOM = 4.0f;
    private static final float MIN_ZOOM = 1.2f;
    private static final String TAG = "ZoomableFrameLayout";
    private float dx = 0.0f;
    private float dy = 0.0f;
    private boolean firstTouch = false;
    private float lastScaleFactor = 0.0f;
    private Mode mode = Mode.NONE;
    private float prevDx = 0.0f;
    private float prevDy = 0.0f;
    private boolean restore = false;
    private float scale = 1.0f;
    private float startX = 0.0f;
    private float startY = 0.0f;
    private long time = System.currentTimeMillis();

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    public ZoomableFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public ZoomableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(firstTouch && (System.currentTimeMillis() - time) <= 300) {
                        //do stuff here for double tap
                        if(restore) {
                            scale = 1.0f;
                            restore = false;
                        } else {
                            scale *= 2.0f;
                            restore = true;
                        }
                        mode = Mode.ZOOM;
                        firstTouch = false;

                    } else {
                        if (scale > MIN_ZOOM) {
                            mode = Mode.DRAG;
                            startX = motionEvent.getX() - prevDx;
                            startY = motionEvent.getY() - prevDy;
                        }
                        firstTouch = true;
                        time = System.currentTimeMillis();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == Mode.DRAG) {
                        dx = motionEvent.getX() - startX;
                        dy = motionEvent.getY() - startY;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = Mode.ZOOM;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = Mode.NONE;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "UP");
                    mode = Mode.NONE;
                    prevDx = dx;
                    prevDy = dy;
                    break;
            }
            scaleDetector.onTouchEvent(motionEvent);

            if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
                getParent().requestDisallowInterceptTouchEvent(true);
                float maxDx = (child().getWidth() - (child().getWidth() / scale)) / 2 * scale;
                float maxDy = (child().getHeight() - (child().getHeight() / scale))/ 2 * scale;
                dx = Math.min(Math.max(dx, -maxDx), maxDx);
                dy = Math.min(Math.max(dy, -maxDy), maxDy);
                Log.i(TAG, "Width: " + child().getWidth() + ", scale " + scale + ", dx " + dx
                        + ", max " + maxDx);
                applyScaleAndTranslation();
            }

            return true;
        });
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleDetector) {
        Log.i(TAG, "onScaleBegin");
        return true;
    }

    public boolean onScale(ScaleGestureDetector scaleDetector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        Log.i(TAG, "onScale" + scaleFactor);
        if (this.lastScaleFactor == 0.0f || Math.signum(scaleFactor) == Math.signum(this.lastScaleFactor)) {
            this.scale *= scaleFactor;
            this.scale = Math.max(MIN_ZOOM, Math.min(this.scale, MAX_ZOOM));
            this.lastScaleFactor = scaleFactor;
            return true;
        }
        this.lastScaleFactor = 0.0f;
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scaleDetector) {
        Log.i(TAG, "onScaleEnd");
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(this.scale);
        child().setScaleY(this.scale);
        child().setTranslationX(this.dx);
        child().setTranslationY(this.dy);
    }

    private View child() {
        return getChildAt(0);
    }
}
