package com.schaffer.base.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.schaffer.base.R;
import com.schaffer.base.common.utils.ConvertUtils;
import com.schaffer.base.common.utils.ImageUtils;

/**
 * @author AndroidSchaffer
 * @date 2017/10/27
 */

public class DefinedGcardView extends View {

    private Bitmap fb;
    private Bitmap bb;
    private Path mPath;
    private Canvas mCanvas;
    private Paint mPaint;

    public DefinedGcardView(Context context) {
        this(context, null);
    }

    public DefinedGcardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefinedGcardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);

        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mPath = new Path();
        bb = ImageUtils.resizeBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), ConvertUtils.dp2px(300),ConvertUtils.dp2px(300));
        fb = Bitmap.createBitmap(bb.getWidth(), bb.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(fb);
        mCanvas.drawColor(Color.DKGRAY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
//            case MotionEvent.ACTION_UP:
//                break;
        }
        mCanvas.drawPath(mPath, mPaint);
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bb, 0, 0, null);
        canvas.drawBitmap(fb, 0, 0, null);

    }
}
