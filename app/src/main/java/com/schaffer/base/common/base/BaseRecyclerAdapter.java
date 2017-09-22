package com.schaffer.base.common.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.schaffer.base.common.recycler.RecyclerViewHolder;

import java.util.List;

/**
 * Created by SchafferWang on 2016/11/8.
 */

public abstract class BaseRecyclerAdapter<T>  extends RecyclerView.Adapter<RecyclerViewHolder>{

    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;


    public BaseRecyclerAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        this.mDatas = datas;
        this.mLayoutId = layoutId;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = RecyclerViewHolder.get(mContext,parent,mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
//        holder.updatePosition(position);
        convert(holder, mDatas.get(position));
    }

    public abstract void convert(RecyclerViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setData(List<T> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }
}
