package com.gamechief.aifunnyringtone.CustomUiViews;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatImageView;

public class CustomZoomImageView extends AppCompatImageView implements View.OnTouchListener {
    private static final int DRAG = 1;
    private static final int NONE = 0;
    private static final int ZOOM = 2;
    float angle = 0.0f;
    private float d = 0.0f;
    float dx = 0.0f;
    float dy = 0.0f;
    private int mode = 0;
    private float newRot = 0.0f;
    private float oldDist = 1.0f;
    RelativeLayout.LayoutParams parms;
    float scalediff;
    int startheight;
    int startwidth;
    float x = 0.0f;
    float y = 0.0f;

    public CustomZoomImageView(Context context) {
        super(context);
    }

    public CustomZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float spacing(MotionEvent event) {
        float x2 = event.getX(0) - event.getX(1);
        float y2 = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double) ((x2 * x2) + (y2 * y2)));
    }

    private float rotation(MotionEvent event) {
        return (float) Math.toDegrees(Math.atan2((double) (event.getY(0) - event.getY(1)), (double) (event.getX(0) - event.getX(1))));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
        switch (event.getAction() & 255) {
            case 0:
                this.parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                this.startwidth = this.parms.width;
                this.startheight = this.parms.height;
                this.dx = event.getRawX() - ((float) this.parms.leftMargin);
                this.dy = event.getRawY() - ((float) this.parms.topMargin);
                this.mode = 1;
                break;
            case 2:
                if (this.mode != 1) {
                    if (this.mode == 2 && event.getPointerCount() == 2) {
                        this.newRot = rotation(event);
                        this.angle = this.newRot - this.d;
                        this.x = event.getRawX();
                        this.y = event.getRawY();
                        float newDist = spacing(event);
                        if (newDist > 10.0f) {
                            float scale = (newDist / this.oldDist) * view.getScaleX();
                            if (((double) scale) > 0.6d) {
                                this.scalediff = scale;
                                view.setScaleX(scale);
                                view.setScaleY(scale);
                            }
                        }
                        view.animate().rotationBy(this.angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
                        this.x = event.getRawX();
                        this.y = event.getRawY();
                        this.parms.leftMargin = (int) ((this.x - this.dx) + this.scalediff);
                        this.parms.topMargin = (int) ((this.y - this.dy) + this.scalediff);
                        this.parms.rightMargin = 0;
                        this.parms.bottomMargin = 0;
                        this.parms.rightMargin = this.parms.leftMargin + (this.parms.width * 5);
                        this.parms.bottomMargin = this.parms.topMargin + (this.parms.height * 10);
                        view.setLayoutParams(this.parms);
                        break;
                    }
                } else {
                    this.x = event.getRawX();
                    this.y = event.getRawY();
                    this.parms.leftMargin = (int) (this.x - this.dx);
                    this.parms.topMargin = (int) (this.y - this.dy);
                    this.parms.rightMargin = 0;
                    this.parms.bottomMargin = 0;
                    this.parms.rightMargin = this.parms.leftMargin + (this.parms.width * 5);
                    this.parms.bottomMargin = this.parms.topMargin + (this.parms.height * 10);
                    view.setLayoutParams(this.parms);
                    break;
                }
            case 5:
                this.oldDist = spacing(event);
                if (this.oldDist > 10.0f) {
                    this.mode = 2;
                }
                this.d = rotation(event);
                break;
            case 6:
                this.mode = 0;
                break;
        }
        return true;
    }
}
