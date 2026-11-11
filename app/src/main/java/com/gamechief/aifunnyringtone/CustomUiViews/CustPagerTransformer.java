package com.gamechief.aifunnyringtone.CustomUiViews;

import android.content.Context;
import android.view.View;
import androidx.viewpager2.widget.ViewPager2;

public class CustPagerTransformer implements ViewPager2.PageTransformer {
    private final int maxTranslateOffsetX;
    private ViewPager2 viewPager;

    public CustPagerTransformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, 180.0f);
    }

    public void transformPage(View page, float position) {
        if (this.viewPager == null) {
            this.viewPager = (ViewPager2) page.getParent();
        }
        float offsetRate = (((float) (((page.getMeasuredWidth() / 2) + (page.getLeft() - this.viewPager.getScrollX())) - (this.viewPager.getMeasuredWidth() / 2))) * 0.38f) / ((float) this.viewPager.getMeasuredWidth());
        float scaleFactor = 1.0f - Math.abs(offsetRate);
        if (scaleFactor > 0.0f) {
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setTranslationX(((float) (-this.maxTranslateOffsetX)) * offsetRate);
        }
    }

    private int dp2px(Context context, float dipValue) {
        return (int) ((dipValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
