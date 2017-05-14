package com.schaffer.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by SchafferWang on 2017/4/26 0026.
 */

public class WrapperGridView extends GridView {


    public WrapperGridView(Context context) {
        super(context);
    }

    public WrapperGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }
}
