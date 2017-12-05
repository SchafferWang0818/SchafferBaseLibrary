package com.schaffer.base.common.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * className: ViewHolder
 * description: 自定义ViewHolder
 * author: hong
 * datetime: 2016/3/19 0019 下午 4:35
 */
public class ViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;

    private ViewHolder(Context context, ViewGroup parent, @LayoutRes int layoutId) {
        mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId) {

        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        }
        return (ViewHolder) convertView.getTag();
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
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(@IdRes int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(@IdRes int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @return
     */
    public ViewHolder setImageBitmap(@IdRes int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public void setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
    }

    public void setItemClickListener(View.OnClickListener listener) {
        if (mConvertView != null)
            mConvertView.setOnClickListener(listener);
    }

    public void setVisiblity(@IdRes int viewId, boolean visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setVisiblity(@IdRes int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
    }
}
