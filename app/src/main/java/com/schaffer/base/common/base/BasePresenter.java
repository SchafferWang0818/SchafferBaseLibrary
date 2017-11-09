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
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        this.mView = view;
    }

    public void detach() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
        compositeSubscription = null;
        mView = null;
    }

    public void showLog(String content) {
        LTUtils.w(tag, content);
    }

    public void showToast(String content) {
        if (mView != null) {
            mView.showToast(content);
        }
    }

    protected void loading() {
        if (mView != null) {
            mView.showLoading("加载中...");
        }
    }

    protected void dismissLoad() {
        if (mView != null) {
            mView.dismissLoading();
        }
    }

//    protected boolean onResponse(BaseBean bean) {
//        if (mView != null) mView.dismissLoading();
//        if (bean.getErrcode() != 0) {
//            if (mView != null) {
//                if (bean.getErrstr().contains("必要参数")) {
//                    mView.showLog(bean.getErrstr());
//                } else {
//                    mView.showToast(bean.getErrstr());
//                }
//            }
//        }
//
//        return bean.getErrcode() == 0;
//    }

    protected void onFailed(Throwable e) {
        if (mView != null) {
            mView.dismissLoading();
            mView.showLog(e.getMessage() + "-->\n\t\t" + e.getLocalizedMessage());
        }
        e.printStackTrace();
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

    public void example() {
//        compositeSubscription.add(ApiModel.getInstance().cancelTrip(stroke_id, cause)
//                .compose(new SchedulerTransformer<BaseResponse>())
//                .subscribe(new Subscriber<BaseResponse>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(BaseResponse response) {
//                        if (response.getSucceed() == 1) {
//
//                        } else {
//
//                        }
//                    }
//                }));
    }

}
