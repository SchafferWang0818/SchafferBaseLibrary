package com.schaffer.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.schaffer.base.BuildConfig;
import com.schaffer.base.common.utils.LTUtils;

/**
 * 参考Android英雄传做法。 Page 157
 *
 * @author Schaffer
 * @date 2017/11/1
 */

public class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    protected SurfaceHolder mBaseHolder;
    /**是否正在绘制*/
    protected boolean mIsDrawing;
    /** Canvas 始终是一个对象 */
    protected Canvas mCanvas;

    public BaseSurfaceView(Context context) {
        this(context, null);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseSetting();
    }

    private void initBaseSetting() {
        mBaseHolder = getHolder();
        /* implements SurfaceHolder.Callback  */
        mBaseHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        /*不透明*/
       /* mBaseHolder.setFormat(PixelFormat.OPAQUE); */
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        Thread thread = new Thread(new SurfaceRunnable());
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }


    /**
     * 绘制动作
     */
    protected void doDraw() {
        // todo  do something for draw
    }

    private void showLog(String content) {
        if (BuildConfig.DEBUG) {
            LTUtils.d(">>>" + content);
        }
    }


    public class SurfaceRunnable implements Runnable {
        @Override
        public void run() {
            long f = System.currentTimeMillis();
            while (mIsDrawing) {
                draw();
            }
            long t = System.currentTimeMillis();
            if (t - f < 100) {
                try {
                    Thread.sleep(100 - (t - f));
                } catch (InterruptedException e) {
                    String localizedMessage = e.getLocalizedMessage();
                    showLog(localizedMessage);
                }
            }
        }

        public void draw() {
            try {
                mCanvas = mBaseHolder.lockCanvas();
                doDraw();
            } catch (Exception e) {
                showLog(e.getCause() + e.getLocalizedMessage());
            } finally {
                if (mCanvas != null) {
                    mBaseHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }
}
