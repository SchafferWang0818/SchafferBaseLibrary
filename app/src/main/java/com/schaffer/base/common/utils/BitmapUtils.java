package com.schaffer.base.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * @author SchafferWang
 * @date 2017/10/27
 * {@link BitmapUtils#handleBitmapEffects(Bitmap, float, float, float)}:通过设置色调,饱和度,亮度改变图像矩阵
 */

public class BitmapUtils {

    /**
     * 通过设置色调,饱和度,亮度改变图像矩阵
     * <p>
     * 来自《Android 群英传》
     *
     * @param bm         原图
     * @param hue        色调
     * @param saturation 饱和度
     * @param lum        亮度
     * @return 改变后的图像
     */
    public static Bitmap handleBitmapEffects(Bitmap bm, float hue, float saturation, float lum) {

        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        //色调调整
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, hue * 180);
        hueMatrix.setRotate(1, hue * 180);
        hueMatrix.setRotate(2, hue * 180);
        //饱和度调整
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);
        //亮度
        ColorMatrix lunMatrix = new ColorMatrix();
        lunMatrix.setScale(lum, lum, lum, 1);
        //整合
        ColorMatrix matrix = new ColorMatrix();
        matrix.postConcat(hueMatrix);
        matrix.postConcat(saturationMatrix);
        matrix.postConcat(lunMatrix);
        //绘制
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

}
