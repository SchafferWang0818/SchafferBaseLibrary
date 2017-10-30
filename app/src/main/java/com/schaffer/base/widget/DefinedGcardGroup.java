package com.schaffer.base.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 创建一个自定义ViewGroup,作为刮奖自定义控件,底层child 自定义,上层绘制覆盖层
 *
 * @author Schaffer
 * @date 2017/10/30
 */

public class DefinedGcardGroup extends FrameLayout {


    private View child;

    public DefinedGcardGroup(@NonNull Context context) {
        this(context, null);
    }

    public DefinedGcardGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefinedGcardGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final int childCount = getChildCount();
        if (childCount > 1) {
            try {
                throw new Exception("DefinedGcardGroup could  has just one child.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        child = getChildAt(0);

    }


}
