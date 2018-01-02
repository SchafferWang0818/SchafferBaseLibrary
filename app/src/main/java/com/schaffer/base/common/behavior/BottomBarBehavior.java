package com.schaffer.base.common.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by AndroidSchaffer on 2018/1/2.
 */

public class BottomBarBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private int defaultDependencyTop = -1;

    public BottomBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        Log.d("TAG", "dependency.getTop() : " + dependency.getTop());
        if (defaultDependencyTop == -1) {
            defaultDependencyTop = dependency.getTop();
        }
        child.setTranslationY(-dependency.getTop() + defaultDependencyTop);
        return true;
    }
}