package com.schaffer.base.test;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Created by AndroidSchaffer on 2018/1/10.
 */

public class TestWidgetD extends View {
    public static final String TAG = "SchafferWidgetD";

    private VelocityTracker vTracker;

    public TestWidgetD(@NonNull Context context) {
        this(context,null);
    }

    public TestWidgetD(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TestWidgetD(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        vTracker = VelocityTracker.obtain();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean doThis = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doThis =false;
                Log.d(TAG, ">>> ACTION_DOWN "+doThis);
                break;
            case MotionEvent.ACTION_MOVE:
                doThis =false;
                Log.d(TAG, ">>> ACTION_MOVE "+doThis);
                break;
            case MotionEvent.ACTION_UP:
                doThis =false;
                Log.d(TAG, ">>> ACTION_UP "+doThis);
                break;
            case MotionEvent.ACTION_CANCEL:
                doThis =false;
                Log.d(TAG, ">>> ACTION_CANCEL "+doThis);
                break;
        }
        return doThis;
    }
}
