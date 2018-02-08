package com.schaffer.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/2/5
 * Project : fentu_android_new
 * Package : com.billliao.fentu.widget
 * Description :
 */

public class LoadScrollView extends ScrollView {


    private OnScrollToBottomListener onScrollToBottomListener;

    public LoadScrollView(Context context) {
        super(context);
    }

    public LoadScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY > 0 && onScrollToBottomListener != null) {
            onScrollToBottomListener.onScrollBottomListener(clampedY);
        }
    }

    public void setOnScrollToBottomListener(OnScrollToBottomListener listener) {
        onScrollToBottomListener = listener;
    }

    public interface OnScrollToBottomListener {
        void onScrollBottomListener(boolean isBottom);
    }
}
