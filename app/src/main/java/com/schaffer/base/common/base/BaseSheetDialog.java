package com.schaffer.base.common.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.schaffer.base.common.utils.LtUtils;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/1/16
 * Project : com.schaffer.base.common.base
 * Description :底部滑出的选项对话框
 */

public abstract class BaseSheetDialog extends BottomSheetDialog {

    private final View mRootView;
    private final Context context;
    private final Window window;
    private final int windowWidth;
    private String tag = getClass().getSimpleName().toString();

    public BaseSheetDialog(@NonNull Context context, @LayoutRes int resId) {
        super(context);
        this.context = context;
        window = getWindow();
        windowWidth = ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getWidth();
        mRootView = LayoutInflater.from(context).inflate(resId, null);
        initView(mRootView);
        setContentView(mRootView);
    }

    /**
     * 根据填充的View 进行设置
     *
     * @param rootView
     */
    protected abstract void initView(View rootView);

    public void showToast(String content) {
        showLog(content);
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public void showLog(String content) {
        LtUtils.d(tag, content);
    }

}
