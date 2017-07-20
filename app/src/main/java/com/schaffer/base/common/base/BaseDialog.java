package com.schaffer.base.common.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

/**
 * Created by SchafferW on 2016/11/25.
 */

public class BaseDialog extends Dialog {

    public Context context;
    public Window window;
    public int windowWidth;
    public Activity activity;
    public View view;
    private String tag;

    public BaseDialog(Context context) {
        super(context);
        this.context = context;
        tag = getClass().getSimpleName();
        window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        activity = (Activity) context;
        windowWidth = activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 测量当前View的高度
     *
     * @param view 填充的View
     */
    public void measureViewHeight(View view) {
        if (view != null) {
            view.measure(0, View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST));
        }
    }


    /**
     * 布局配置处理
     * @param view  inflateView
     * @param width 宽度
     * @param height    高度
     * @param cancel    是否可以取消
     * @param isBottom  是否屏幕底部
     */
    public void setLayoutConfig(View view, int width, int height, boolean cancel, boolean isBottom) {
        setContentView(view);
        if (!isBottom) {
            window.setGravity(Gravity.CENTER);
        } else {
            window.setGravity(Gravity.BOTTOM);
        }
        getWindow().getAttributes().dimAmount = 0.5f;
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setLayout(width, height);
        setCanceledOnTouchOutside(cancel);
    }


}
