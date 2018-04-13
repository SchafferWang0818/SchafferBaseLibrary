package com.schaffer.base.common.transformer;


import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SchedulerTransformer<T> implements Observable.Transformer<T, T> {

        @Override
    public Observable<T> call(Observable<T> observable) {
//        Observable.Transformer函数
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    @Override
//    public ObservableSource<T> apply(io.reactivex.Observable<T> upstream) {
//        return upstream.subscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .unsubscribeOn(io.reactivex.schedulers.Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
}