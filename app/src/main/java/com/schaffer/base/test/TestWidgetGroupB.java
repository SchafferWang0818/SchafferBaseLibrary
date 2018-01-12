package com.schaffer.base.test;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.FrameLayout;

/**
 * Created by AndroidSchaffer on 2018/1/10.
 */

public class TestWidgetGroupB extends FrameLayout {
    public static final String TAG = "SchafferGroupB";
    private VelocityTracker vTracker;

    public TestWidgetGroupB(@NonNull Context context) {
        this(context, null);
    }

    public TestWidgetGroupB(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestWidgetGroupB(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        vTracker = VelocityTracker.obtain();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        vTracker.addMovement(ev);
        vTracker.computeCurrentVelocity(1000);
        float xVelocity = vTracker.getXVelocity();
        float yVelocity = vTracker.getYVelocity();
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                Log.d(TAG, ">>>onIntercept ACTION_DOWN "+intercepted);
                break;
            case MotionEvent.ACTION_MOVE:
                intercepted = Math.abs(xVelocity) > Math.abs(yVelocity);
                Log.d(TAG, ">>>onIntercept ACTION_MOVE "+intercepted);
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                Log.d(TAG, ">>>onIntercept ACTION_UP "+intercepted);
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean doThis = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doThis = false;
                Log.d(TAG, ">>> ACTION_DOWN "+doThis);
                break;
            case MotionEvent.ACTION_MOVE:
                doThis = false;
                Log.d(TAG, ">>> ACTION_MOVE "+doThis);
                break;
            case MotionEvent.ACTION_UP:
                doThis = false;
                Log.d(TAG, ">>> ACTION_UP "+doThis);
                break;
            case MotionEvent.ACTION_CANCEL:
                doThis = false;
                Log.d(TAG, ">>> ACTION_CANCEL "+doThis);
                break;
        }
        return doThis;
    }
}
