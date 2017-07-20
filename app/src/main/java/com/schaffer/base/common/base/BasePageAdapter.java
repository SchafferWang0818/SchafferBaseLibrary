package com.schaffer.base.common.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by a7352 on 2017/7/3.
 */

public class BasePageAdapter extends PagerAdapter {

    protected final Context context;
    protected final List<String> titles;
    protected final List<? extends View> views;

    public BasePageAdapter(Context context, List<String> titles, List<? extends View> views) {
        this.context = context;
        this.titles = titles;
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(views.get(position));

    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles == null) return "";
        return TextUtils.isEmpty(titles.get(position)) ? "" : titles.get(position);
    }
}
