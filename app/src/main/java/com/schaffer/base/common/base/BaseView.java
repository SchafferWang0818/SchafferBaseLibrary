package com.schaffer.base.common.base;

/**
 * Created by SchafferWang on 2017/5/13.
 */

public interface BaseView {

	void showLoading(String text);

	void showLoading();

	void dismissLoading();

	void onSucceed();

	void onFailed();

	void onFailed(Throwable throwable);

	void showLog(String msg);

	void showLog(int resId);

	void showToast(String msg);

	void showToast(int resId);


}
