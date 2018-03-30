package com.schaffer.base.common.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.utils.LtUtils;

/**
 * Created by a7352 on 2017/5/13.
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {

    private String tag;
    protected Activity activity;
    private View mRootView;
    private boolean mIsFirst = true;
    protected P mPresenter;
    private ProgressDialog progress;
    private int mainThemeColor = Color.parseColor("#ff8201");

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        tag = getClass().getSimpleName();
        this.activity = activity;
        mPresenter = initPresenter();
    }

    public void setMainThemeColor(int mainThemeColor) {
        this.mainThemeColor = mainThemeColor;
    }

    protected abstract P initPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
            return mRootView;
        } else {
            mRootView = initView(inflater, container);
        }
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initData();

    protected abstract void refreshData();

    @Override
    public void onResume() {
        if (mPresenter != null) {
            mPresenter.attach((V) this);
        }
        super.onResume();
        if (mIsFirst) {
            mIsFirst = false;
            initData();
        } else {
            refreshData();
        }
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        super.onDestroyView();
    }

    @Override
    public void showLog(String msg) {
        LtUtils.w(tag, msg);
    }

    @Override
    public void showLog(int resId) {
        showLog(getString(resId));
    }

    @Override
    public void showToast(String msg) {
        showLog(msg);
        LtUtils.showToastShort(activity, msg);
    }

    @Override
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showLoading(String text) {
        progress = showProgress(text, false);
    }

    @Override
    public void showLoading() {
        showLoading("");
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }

    @Override
    public void onSucceed() {
        dismissLoading();
    }

    @Override
    public void onFailed() {
        dismissLoading();
    }

    @Override
    public void onFailed(Throwable throwable) {
        dismissLoading();
        showLog(throwable.getMessage() + throwable.getCause());
    }

    public void callPhone(final String telephone) {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).callPhone(telephone);
        }
    }


    public void showSnackbar(String content, int duration) {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).showSnackbar(content, duration);
            return;
        }

        if (duration != Snackbar.LENGTH_SHORT && duration != Snackbar.LENGTH_LONG) {
            return;
        }
        Snackbar make = Snackbar.make(mRootView.getRootView(), content, duration);
        make.getView().setBackgroundColor(Color.parseColor("#ff6d64"));
        ((TextView) make.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        make.show();
    }

    public void showSnackbar(String content) {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).showSnackbar(content, Snackbar.LENGTH_SHORT);
        }
    }


    public void showSnackbar(View view, String content, String action, int clickColor, View.OnClickListener listener) {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).showSnackbar(view, content, action, clickColor, listener);
        }
    }

    public void showSnackbar(String content, String action, int clickColor, View.OnClickListener listener) {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).showSnackbar(null, content, action, clickColor, listener);
        }
    }

    public ProgressDialog showProgress(String content, boolean touchOutside) {
        if (isActivityFromInit()) {
            return ((BaseEmptyActivity) activity).showProgress(content, touchOutside);
        }
        ProgressDialog loadingDialog = new ProgressDialog(activity);
        // 创建自定义样式dialog
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(touchOutside);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setMessage(content);
        loadingDialog.show();
        return loadingDialog;
    }

    public void dismissProgress() {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).dismissProgress();
            return;
        }
        if (progress != null && progress.isShowing()) {
            progress.cancel();
            progress = null;
        }
    }

    /**
     * Activity 是否继承自{@link BaseEmptyActivity}
     *
     * @return true or false
     */
    public boolean isActivityFromInit() {
        if (activity == null) {
            return false;
        }
        return activity.getClass().isAssignableFrom(BaseEmptyActivity.class);
    }

    public void requestPermissions(BaseEmptyActivity.PermissionResultListener listener, String... permissions) {
        if (isActivityFromInit()) {
            ((BaseEmptyActivity) activity).requestPermission(listener, permissions);
        }
    }
}
