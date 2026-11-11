package com.gamechief.aifunnyringtone.PuzzleGame.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import androidx.recyclerview.widget.RecyclerView;

public class AnimationUtils {
    private static int counter = 0;

    public static void scaleXY(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0.0f);
        holder.itemView.setScaleY(0.0f);
        PropertyValuesHolder propx = PropertyValuesHolder.ofFloat("scaleX", new float[]{1.0f});
        PropertyValuesHolder propy = PropertyValuesHolder.ofFloat("scaleY", new float[]{1.0f});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, new PropertyValuesHolder[]{propx, propy});
        animator.setDuration(800);
        animator.start();
    }

    public static void scaleX(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0.0f);
        PropertyValuesHolder propx = PropertyValuesHolder.ofFloat("scaleX", new float[]{1.0f});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, new PropertyValuesHolder[]{propx});
        animator.setDuration(800);
        animator.start();
    }

    public static void scaleY(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleY(0.0f);
        PropertyValuesHolder propy = PropertyValuesHolder.ofFloat("scaleY", new float[]{1.0f});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, new PropertyValuesHolder[]{propy});
        animator.setDuration(800);
        animator.start();
    }

    public static void animate(RecyclerView.ViewHolder holder, boolean goesDown) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", new float[]{goesDown ? 300.0f : -300.0f, 0.0f});
        animatorTranslateY.setDuration(1000);
        animatorTranslateY.start();
    }
}
