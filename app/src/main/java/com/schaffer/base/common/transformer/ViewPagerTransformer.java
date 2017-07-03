package com.schaffer.base.common.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by SchafferWang on 2017/7/3.
 * 使用当前类可以使ViewPager当前页面放大,其他页面缩小
 */

public class ViewPagerTransformer implements ViewPager.PageTransformer {
    private static final float SCAL_MIN = 0.8f;
    private static final float SCAL_MAX = 1.2f;
    private static final float SCAL_NORMAL = 1.0f;

    /**
     * 默认显示的当前位置为0,左侧一个的位置为-1,右侧一个的位置为1,存在渐变的情况
     *
     * @param page     某个页面
     * @param position 某个页面的位置
     */
    @Override
    public void transformPage(View page, float position) {

        if (position < -1||position > 1) {
            page.setScaleY(SCAL_MIN);
        } else{
            page.setScaleY(Math.max(SCAL_MIN,1-Math.abs(position)));
        }


    }
}
