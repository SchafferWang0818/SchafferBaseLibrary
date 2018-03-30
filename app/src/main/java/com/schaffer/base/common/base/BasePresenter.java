package com.schaffer.base.common.base;

import android.os.Build;

import com.schaffer.base.common.utils.AppUtils;
import com.schaffer.base.common.utils.EncryptUtils;
import com.schaffer.base.common.utils.LtUtils;
import com.schaffer.base.common.utils.StringUtils;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
        LtUtils.w(tag, content);
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
    public <T extends OkHttpRequestBuilder> T addHeader(T builder) {
        final String s = StringUtils.generateShortUuid();
        final long t = System.currentTimeMillis() / 1000;
        String sign_str = String.valueOf(t) + "Constants.WITHDRAW_INFO" + s;
        final String sign = EncryptUtils.encryptMD5ToString(sign_str.toUpperCase());
        return (T) builder.addHeader("v", AppUtils.getVersionName(BaseApplication.getInstance()))
                .addHeader("sv", Build.MANUFACTURER + " " + Build.VERSION.SDK)
                .addHeader("sign", sign)
                .addHeader("t", String.valueOf(t))
                .addHeader("s", s);
    }

    protected void onFailed(Throwable t) {
        try {
            if (t == null) {
                return;
            }
            if (mView != null) {
                mView.dismissLoading();
                mView.showLog(t.getMessage() + "-->\n\t\t" + t.getLocalizedMessage());
            }
            t.printStackTrace();
            if (t instanceof SocketTimeoutException && mView != null) {
                mView.showToast("网络状况好像不太好");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mView.showToast("网络状况好像不太好");
        }
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

    public <T> void query(QueryBuilder<T> queryBuilder, MoreCurdsSubscriber<T> subscriber) {
        try {
            queryBuilder.rx().list().observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T, K> void insertOrReplace(AbstractDao<T, K> dao, T bean, SimpleCurdSubscriber<T> subscriber) {
        try {
            dao.rx().insertOrReplace(bean).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T, K> void insert(AbstractDao<T, K> dao, List<T> beans, MoreCurdsSubscriber<T> subscriber) {
        if (beans == null || beans.size() <= 0) {
            return;
        }
        try {
            dao.rx().insertInTx(beans).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public <T, K> void delete(AbstractDao<T, K> dao, T bean, SimpleCurdSubscriber<Void> subscriber) {
        try {
            dao.rx().delete(bean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public <T, K> void deleteByKey(AbstractDao<T, K> dao, K key, SimpleCurdSubscriber<Void> subscriber) {
        try {
            dao.rx().deleteByKey(key).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T, K> void delete(AbstractDao<T, K> dao, List<T> beans, Subscriber<Void> subscriber) {
        if (beans == null || beans.size() <= 0) {
            return;
        }
        try {
            dao.rx().deleteInTx(beans).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T, K> void deleteAll(AbstractDao<T, K> dao) {
        try {
            dao.rx().deleteAll().observeOn(AndroidSchedulers.mainThread());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T, K> void update(AbstractDao<T, K> dao, T bean, SimpleCurdSubscriber<T> subscriber) {
        try {
            dao.rx().update(bean).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract static class SimpleCurdSubscriber<T> extends Subscriber<T> {

        private final BasePresenter mPresenter;

        public SimpleCurdSubscriber(BasePresenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mPresenter.onFailed(e);
        }
    }

    public abstract static class MoreCurdsSubscriber<T> extends Subscriber<Iterable<T>> {

        private final BasePresenter mPresenter;

        public MoreCurdsSubscriber(BasePresenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mPresenter.onFailed(e);
        }

        @Override
        public void onNext(Iterable<T> ts) {
            Iterator<T> iterator = ts.iterator();
            List<T> data = new ArrayList<>();
            while (iterator.hasNext()) {
                data.add(iterator.next());
            }
            onNext(data);
        }

        public abstract void onNext(List<T> data);
    }


}
