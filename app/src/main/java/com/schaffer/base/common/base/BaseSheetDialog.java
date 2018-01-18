package com.schaffer.base.common.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/1/16
 * Project : com.schaffer.base.common.base
 * Description :底部滑出的选项对话框
 */

public abstract class BaseSheetDialog extends BottomSheetDialog {

    private final View mRootView;

    public BaseSheetDialog(@NonNull Context context, @LayoutRes int resId) {
        super(context);
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


}
