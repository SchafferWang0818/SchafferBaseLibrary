package com.schaffer.base.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.schaffer.base.R;

/**
 * Created by AndroidSchaffer on 2017/11/1.
 * 失败
 */

public class TestViewDragGroup extends FrameLayout {


    private ViewDragHelper helper;

    public TestViewDragGroup(@NonNull Context context) {
        this(context, null);
    }

    public TestViewDragGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestViewDragGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        helper = ViewDragHelper.create(this, callback);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            return child.getId() == R.id.drag_main;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return /*super.clampViewPositionVertical(child, top, dy)*/top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left/*super.clampViewPositionHorizontal(child, left, dx)*/;
        }

    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return helper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        helper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        /*super.computeScroll();*/
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
