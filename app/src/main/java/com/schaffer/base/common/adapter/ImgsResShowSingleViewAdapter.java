package com.schaffer.base.common.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.schaffer.base.common.base.BasePagerSingleViewAdapter;

import java.util.List;

/**
 * Created by a7352 on 2017/7/20.
 */

public class ImgsResShowSingleViewAdapter extends BasePagerSingleViewAdapter {


    private final int[] resIds;

    public ImgsResShowSingleViewAdapter(Context context, List<String> titles, List<? extends View> views, int[] resIds) {
        super(context, titles, views);
        this.resIds = resIds;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ImageView) (views.get(position))).setImageResource(resIds[position]);
        return super.instantiateItem(container, position);
    }
}
