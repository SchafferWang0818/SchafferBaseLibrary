package com.schaffer.base.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.schaffer.base.common.utils.ConvertUtils;


/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/1/29
 * Project : fentu_android_new
 * Package : com.billliao.fentu.widget
 * Description : 购物车型号颜色选择ViewGroup
 */

public class CartSelectRadioGroup extends RadioGroup {

    private int childMargins = 0;

    public CartSelectRadioGroup(@NonNull Context context) {
        super(context);
        init();
    }

    public CartSelectRadioGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setOrientation(HORIZONTAL);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (!(child instanceof RadioButton)) {
                /*除了*/
                throw new RuntimeException("RadioGroup could not have other children excluding RadioButton" + child);
            }
        }
    }

    public int getChildMargins() {
        return childMargins;
    }

    public void setChildMargins(int childMargins) {
        this.childMargins = childMargins;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton child = (RadioButton) getChildAt(i);
            child.setLines(1);
            child.measure(MeasureSpec.makeMeasureSpec((1 << 30) - 1, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(ConvertUtils.dp2px(35), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int count = getChildCount();
        int paddingLeft = getPaddingLeft();
        int measuredWidth = getMeasuredWidth() - getPaddingRight();
        int paddingTop = getPaddingTop();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (i == 0) {
                child.layout(paddingLeft
                        , paddingTop
                        , paddingLeft + childWidth
                        , paddingTop + childHeight);
            } else {
                View before = getChildAt(i - 1);
                if (before.getRight() + childMargins + childWidth > measuredWidth) {
                    /* 换行 */
                    child.layout(paddingLeft
                            , before.getBottom() + childMargins
                            , paddingLeft + childWidth
                            , before.getBottom() + childMargins + childHeight);
                } else {
                    /* 不换行 */
                    child.layout(before.getRight() + childMargins
                            , before.getTop()
                            , before.getRight() + childMargins + childWidth
                            , before.getTop() + childHeight);
                }
            }

        }
    }


    public int getCheckedChildIndex() {

        int childCount = getChildCount();
        if (childCount == 0) {
            return -1;
        }
        for (int i = 0; i < childCount; i++) {
            if (((RadioButton) getChildAt(i)).isChecked()) {
                return i;
            }
        }
        return -1;
    }

    public int getCheckedChildId() {
        int index = getCheckedChildIndex();
        if (index != -1) {
            return getChildAt(index).getId();
        }
        return -1;
    }


}
