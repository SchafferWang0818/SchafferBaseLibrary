package com.schaffer.base.common.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by AndroidSchaffer on 2017/11/13.
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {
    protected List<T> mData;
    private SparseArray<View> mViews;
    protected Context mContext;

    public BasePagerAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
        mViews = new SparseArray<View>(data.size());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        if (view == null) {
            view = newView(position);
            mViews.put(position, view);
        }
        container.addView(view);
        return view;
    }

    public abstract View newView(int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    public T getItem(int position) {
        return mData.get(position);
    }
}
