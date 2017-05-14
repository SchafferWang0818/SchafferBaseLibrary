package com.schaffer.base.common.base;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;


public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected List<T> datas;
    protected Context mContext;
    protected String tag;

    public BaseListAdapter(List<T> datas, Context mContext) {
        this.datas = datas;
        tag = getClass().getSimpleName();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
