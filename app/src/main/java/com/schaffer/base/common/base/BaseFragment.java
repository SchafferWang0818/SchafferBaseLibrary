package com.schaffer.base.common.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schaffer.base.R;
import com.schaffer.base.common.utils.LTUtils;
import com.schaffer.base.widget.ProgressDialogs;

/**
 * Created by a7352 on 2017/5/13.
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {

	private String tag;
	protected Activity activity;
	private View mRootView;
	private ProgressDialogs mProgressDialogs;
	private boolean mIsFirst = true;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		tag = getClass().getSimpleName();
		this.activity = activity;
	}

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
			mProgressDialogs = new ProgressDialogs(activity);
			mRootView = initView(inflater, container);
		}
		return mRootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && mIsFirst) {
			initData();
			mIsFirst = false;
			return;
		}
		if (isVisibleToUser) {
			refreshData();
		}
	}

	protected abstract View initView(LayoutInflater inflater, ViewGroup container);

	protected abstract void initData();

	protected abstract void refreshData();

	@Override
	public void showLog(String msg) {
		LTUtils.w(tag, msg);
	}

	@Override
	public void showLog(int resId) {
		showLog(getString(resId));
	}

	@Override
	public void showToast(String msg) {
		showLog(msg);
		LTUtils.showToastShort(activity, msg);
	}

	@Override
	public void showToast(int resId) {
		showToast(getString(resId));
	}

	@Override
	public void showLoading(String text) {
		if (mProgressDialogs != null) {
			mProgressDialogs.showDialog(text);
		}
	}

	@Override
	public void showLoading() {
		showLoading("");
	}

	@Override
	public void dismissLoading() {
		if (mProgressDialogs != null) {
			mProgressDialogs.closeDialog();
		}
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
		if (TextUtils.isEmpty(telephone)) return;
		StringBuffer sb = new StringBuffer().append(getString(R.string.call));
		new AlertDialog.Builder(activity).setMessage(sb.toString())
				.setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
						if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
							return;
						}
						startActivity(intent);
					}
				}).setNegativeButton(getString(R.string.cancel), null).create().show();
	}
}
