package com.schaffer.base.common.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by AndroidSchaffer on 2018/1/2.
 */

public class FABTranslationBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private int defaultDependencyTop = -1;

    public FABTranslationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        Log.d("TAG", "dependency.getTop() : " + dependency.getTop());
        child.setTranslationY(-dependency.getTop() * 3 / 2);
        return true;
    }
}