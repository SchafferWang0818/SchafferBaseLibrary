package com.schaffer.base.common.anim;

import android.view.View;

/**
 * Created by AndroidSchaffer on 2017/10/23.
 *
 * @author SchafferWang
 */

public class ObjectAnimatorWrapper {
    private View mTarget;

    public static final String VALUE_WIDTH = "width";
    public static final String VALUE_HEIGHT = "height";

    public ObjectAnimatorWrapper(View target) {
        this.mTarget = target;
    }

    public int getWidth() {
        return mTarget.getLayoutParams().width;
    }

    public void setWidth(int width) {
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }

    public int getHeight() {
        return mTarget.getLayoutParams().height;
    }

    public void setHeight(int height) {
        mTarget.getLayoutParams().height = height;
        mTarget.requestLayout();
    }


}
