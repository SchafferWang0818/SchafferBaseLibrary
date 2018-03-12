package com.schaffer.base.test;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/3/12
 * Project : SchafferBaseLibrary
 * Package : com.schaffer.base.test
 * Description :
 */

public class TestViewGroup extends FrameLayout {


    public TestViewGroup(@NonNull Context context) {
        super(context);
    }

    public TestViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        return super.onInterceptTouchEvent(ev);
    }
}
