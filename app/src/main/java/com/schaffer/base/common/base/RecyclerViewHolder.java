package com.schaffer.base.common.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * className: ViewHolder
 * description: 自定义ViewHolder
 * datetime: 2016/3/19 0019 下午 4:35
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    private RecyclerViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }


    public static RecyclerViewHolder get(Context context, ViewGroup parent, int layoutId) {

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(context, itemView);
        return holder;
    }


    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对应的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public RecyclerViewHolder setVisibility(int viewId, boolean isVisibility) {
        View view = getView(viewId);
        view.setVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public RecyclerViewHolder setVisibility2(int viewId, boolean isVisibility) {
        View view = getView(viewId);
        view.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        return this;
    }

    public RecyclerViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public RecyclerViewHolder setText(int viewId, SpannableStringBuilder builder) {
        TextView view = getView(viewId);
        view.setText(builder);
        return this;
    }


    public RecyclerViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);

        return this;
    }


    public RecyclerViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public RecyclerViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

}
