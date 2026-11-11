package com.gamechief.aifunnyringtone.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * UI-only motion helper for the v6.2 "3D material" look.
 *
 * Pure presentation: it only touches translationZ / translationY / alpha on views that
 * already exist. No data, no listeners, no business logic.
 */
public final class UiMotion {

    /** views that should float get this tag in XML: android:tag="float" */
    public static final String TAG_FLOAT = "float";
    /** views that should pop in a little later get android:tag="float_late" */
    public static final String TAG_FLOAT_LATE = "float_late";

    private UiMotion() {
    }

    /** Walk the tree and animate every tagged view. Safe to call more than once. */
    public static void floatTagged(View root) {
        if (root == null) {
            return;
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                floatTagged(group.getChildAt(i));
            }
        }
        Object tag = root.getTag();
        if (TAG_FLOAT.equals(tag)) {
            floatView(root, 6f, 2600L, 0L);
        } else if (TAG_FLOAT_LATE.equals(tag)) {
            floatView(root, 7.5f, 3100L, 480L);
        }
    }

    /** Gentle up/down floating loop with a soft 3D lift. */
    public static void floatView(View view, float dp, long period, long startDelay) {
        if (view == null) {
            return;
        }
        final float px = dp * view.getResources().getDisplayMetrics().density;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.setTranslationZ(dpToPx(view, 2f));
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -px);
        animator.setDuration(period);
        animator.setStartDelay(startDelay);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /** Small entrance pop used by dialogs, sheets and hero cards. */
    public static void popIn(View view, long startDelay) {
        if (view == null) {
            return;
        }
        view.setAlpha(0f);
        view.setScaleX(0.94f);
        view.setScaleY(0.94f);
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(startDelay)
                .setDuration(280L)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /** Entrance rise (translate + fade) for a whole container. */
    public static void riseIn(View view, long startDelay) {
        if (view == null) {
            return;
        }
        final float px = dpToPx(view, 14f);
        view.setAlpha(0f);
        view.setTranslationY(px);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(startDelay)
                .setDuration(320L)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /** Staggered entrance for the direct children of a container (picker panels, chip rows). */
    public static void riseChildren(ViewGroup parent, long step) {
        if (parent == null) {
            return;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child.getTag() == null) {
                riseIn(child, i * step);
            }
        }
    }

    private static float dpToPx(View view, float dp) {
        return dp * view.getResources().getDisplayMetrics().density;
    }
}
