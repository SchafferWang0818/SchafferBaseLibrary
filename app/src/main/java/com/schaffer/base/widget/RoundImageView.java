package com.schaffer.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by AndroidSchaffer on 2017/9/28.
 */

public class RoundImageView extends ImageView {

    float width, height;
    int roundPx = 20;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (width > roundPx && height > roundPx) {
            Path path = new Path();
            path.moveTo(roundPx, 0);
            path.lineTo(width - roundPx, 0);
            path.quadTo(width, 0, width, roundPx);
            path.lineTo(width, height - roundPx);
            path.quadTo(width, height, width - roundPx, height);
            path.lineTo(roundPx, height);
            path.quadTo(0, height, 0, height - roundPx);
            path.lineTo(0, roundPx);
            path.quadTo(0, 0, roundPx, 0);
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
