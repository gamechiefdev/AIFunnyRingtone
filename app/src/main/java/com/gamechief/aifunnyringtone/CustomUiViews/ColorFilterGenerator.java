package com.gamechief.aifunnyringtone.CustomUiViews;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class ColorFilterGenerator {
    private static double[] DELTA_INDEX = {FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 0.01d, 0.02d, 0.04d, 0.05d, 0.06d, 0.07d, 0.08d, 0.1d, 0.11d, 0.12d, 0.14d, 0.15d, 0.16d, 0.17d, 0.18d, 0.2d, 0.21d, 0.22d, 0.24d, 0.25d, 0.27d, 0.28d, 0.3d, 0.32d, 0.34d, 0.36d, 0.38d, 0.4d, 0.42d, 0.44d, 0.46d, 0.48d, 0.5d, 0.53d, 0.56d, 0.59d, 0.62d, 0.65d, 0.68d, 0.71d, 0.74d, 0.77d, 0.8d, 0.83d, 0.86d, 0.89d, 0.92d, 0.95d, 0.98d, 1.0d, 1.06d, 1.12d, 1.18d, 1.24d, 1.3d, 1.36d, 1.42d, 1.48d, 1.54d, 1.6d, 1.66d, 1.72d, 1.78d, 1.84d, 1.9d, 1.96d, 2.0d, 2.12d, 2.25d, 2.37d, 2.5d, 2.62d, 2.75d, 2.87d, 3.0d, 3.2d, 3.4d, 3.6d, 3.8d, 4.0d, 4.3d, 4.7d, 4.9d, 5.0d, 5.5d, 6.0d, 6.5d, 6.8d, 7.0d, 7.3d, 7.5d, 7.8d, 8.0d, 8.4d, 8.7d, 9.0d, 9.4d, 9.6d, 9.8d, 10.0d};

    public static void adjustHue(ColorMatrix cm, float value) {
        float value2 = (cleanValue(value, 180.0f) / 180.0f) * 3.1415927f;
        if (value2 != 0.0f) {
            float cosVal = (float) Math.cos((double) value2);
            float sinVal = (float) Math.sin((double) value2);
            float[] mat = {((1.0f - 0.213f) * cosVal) + 0.213f + ((-0.213f) * sinVal), ((-0.715f) * cosVal) + 0.715f + ((-0.715f) * sinVal), ((-0.072f) * cosVal) + 0.072f + ((1.0f - 0.072f) * sinVal), 0.0f, 0.0f, ((-0.213f) * cosVal) + 0.213f + (0.143f * sinVal), ((1.0f - 0.715f) * cosVal) + 0.715f + (0.14f * sinVal), ((-0.072f) * cosVal) + 0.072f + (-0.283f * sinVal), 0.0f, 0.0f, ((-0.213f) * cosVal) + 0.213f + ((-(1.0f - 0.213f)) * sinVal), ((-0.715f) * cosVal) + 0.715f + (sinVal * 0.715f), 0.072f + ((1.0f - 0.072f) * cosVal) + (sinVal * 0.072f), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
            cm.postConcat(new ColorMatrix(mat));
        }
    }

    public static void adjustBrightness(ColorMatrix cm, float value) {
        float value2 = cleanValue(value, 100.0f);
        if (value2 != 0.0f) {
            cm.postConcat(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, value2, 0.0f, 1.0f, 0.0f, 0.0f, value2, 0.0f, 0.0f, 1.0f, 0.0f, value2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f}));
        }
    }

    public static void adjustContrast(ColorMatrix cm, int value) {
        float x;
        float x2;
        int value2 = (int) cleanValue((float) value, 100.0f);
        if (value2 != 0) {
            if (value2 < 0) {
                x = ((((float) value2) / 100.0f) * 127.0f) + 127.0f;
            } else {
                float x3 = (float) (value2 % 1);
                if (x3 == 0.0f) {
                    x2 = (float) DELTA_INDEX[value2];
                } else {
                    x2 = (((float) DELTA_INDEX[value2 << 0]) * (1.0f - x3)) + (((float) DELTA_INDEX[(value2 << 0) + 1]) * x3);
                }
                x = (x2 * 127.0f) + 127.0f;
            }
            cm.postConcat(new ColorMatrix(new float[]{x / 127.0f, 0.0f, 0.0f, 0.0f, (127.0f - x) * 0.5f, 0.0f, x / 127.0f, 0.0f, 0.0f, (127.0f - x) * 0.5f, 0.0f, 0.0f, x / 127.0f, 0.0f, (127.0f - x) * 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f}));
        }
    }

    public static void adjustSaturation(ColorMatrix cm, float value) {
        float value2 = cleanValue(value, 100.0f);
        if (value2 != 0.0f) {
            float x = (value2 > 0.0f ? (3.0f * value2) / 100.0f : value2 / 100.0f) + 1.0f;
            cm.postConcat(new ColorMatrix(new float[]{((1.0f - x) * 0.3086f) + x, (1.0f - x) * 0.6094f, (1.0f - x) * 0.082f, 0.0f, 0.0f, (1.0f - x) * 0.3086f, ((1.0f - x) * 0.6094f) + x, (1.0f - x) * 0.082f, 0.0f, 0.0f, (1.0f - x) * 0.3086f, (1.0f - x) * 0.6094f, ((1.0f - x) * 0.082f) + x, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f}));
        }
    }

    protected static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public static ColorFilter adjustColor(int brightness, int contrast, int saturation, int hue) {
        ColorMatrix cm = new ColorMatrix();
        adjustHue(cm, (float) hue);
        adjustContrast(cm, contrast);
        adjustBrightness(cm, (float) brightness);
        adjustSaturation(cm, (float) saturation);
        return new ColorMatrixColorFilter(cm);
    }
}
