package com.schaffer.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by SchafferWang on 2017/4/26 0026.
 */

public class WrapperListView extends ListView {


    public WrapperListView(Context context) {
        super(context);
    }

    public WrapperListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }
}
