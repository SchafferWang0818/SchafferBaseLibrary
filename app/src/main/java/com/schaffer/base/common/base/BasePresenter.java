package com.schaffer.base.common.base;

import com.schaffer.base.common.utils.LTUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by SchafferWang on 2017/5/13.
 */

public class BasePresenter<V extends BaseView> {


	protected CompositeSubscription compositeSubscription;
	private String tag;
	protected V mView;

	public BasePresenter() {
		compositeSubscription = new CompositeSubscription();
		tag = getClass().getSimpleName();
	}

	public void attach(V view) {
		this.mView = view;
	}

	public void detach() {
		compositeSubscription.unsubscribe();
		compositeSubscription = null;
		mView = null;
	}

	public void showLog(String content) {
		LTUtils.w(tag, content);
	}

//	public abstract class CustomObserver<T> implements Observer<T> {
//
//		@Override
//		public void onCompleted() {
//			mView.hideLoading();
//		}
//
//		@Override
//		public void onError(Throwable e) {
//			mView.hideLoading();
//			LogToastUtil.w("Throwable->", e.getMessage() + e.getCause());
//		}
//
//	}

}
