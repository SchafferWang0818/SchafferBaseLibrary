package com.schaffer.base.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtil {

    /**
     * http://mp.weixin.qq.com/s/zXsSBlhIbtOUled5ridNHw
     *
     * @param context
     * @param state
     * @param color
     */
    public static void setStatusBarState(Context context, StatusState state, @ColorInt int color) {
        Activity activity = (AppCompatActivity) context;
        Window window = activity.getWindow();
        switch (state) {
            case STATE_FULL_SCREEN:
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //方式二
                //activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                break;
            case STATE_NOT_SHOW:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                break;
            case STATE_PLACEHOLDER_THEME_COLOR:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(color);
                    /*白底黑字*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /*FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS*/
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                } else {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    /*填充新的布局作为StatusBar*/
                    ViewGroup systemContent = activity.findViewById(android.R.id.content);
                    View statusBarView = new View(context);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
                    statusBarView.setBackgroundColor(color);
                    systemContent.getChildAt(0).setFitsSystemWindows(true);
                    systemContent.addView(statusBarView, 0, lp);
                }
                break;
            case STATE_SHOW_STATUS_TEXT:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    ViewGroup contentView = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
                    contentView.getChildAt(0).setFitsSystemWindows(false);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /*FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS*/
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                break;

            case STATE_MULTI_FRAGMENT_STATE:
                /*Activity中设置状态栏*/
                /*View statusBarView = */
                setActivityStickyStatusBar(context, activity, window);

                //Fragment设置statusBar的颜色
                //mActivity.mStatusBarView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                break;
        }
    }

    public static View setActivityStickyStatusBar(Context context, Activity activity, Window window) {
        /*干掉StatusBar*/
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        View mStatusBarView = new View(context);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int statusBarHeight = getStatusBarHeight(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth, statusBarHeight);
        mStatusBarView.setLayoutParams(params);
        mStatusBarView.requestLayout();

        ViewGroup systemContent = activity.findViewById(android.R.id.content);
        ViewGroup userContent = (ViewGroup) systemContent.getChildAt(0);
        /*干掉了ActionBar*/
        userContent.setFitsSystemWindows(false);
        userContent.addView(mStatusBarView, 0);
        return mStatusBarView;
    }


    public static int getStatusBarHeight(Context context) {

        /*旧方法*/
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        /*新方法*/
        if (result == 0) {
            Rect rect = new Rect();
            Window window = ((AppCompatActivity) context).getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            result = rect.top;
        }
        return result;
    }

    public enum StatusState {

        /*占位,颜色*/
        STATE_PLACEHOLDER_THEME_COLOR,
        /*不占位,单独显示文字*/
        STATE_SHOW_STATUS_TEXT,
        /*不占位,单独不显示状态栏*/
        STATE_NOT_SHOW,
        /*不占位,全屏显示*/
        STATE_FULL_SCREEN,
        /*Activity的Fragment设置的多个状态栏时*/
        STATE_MULTI_FRAGMENT_STATE
    }
}
