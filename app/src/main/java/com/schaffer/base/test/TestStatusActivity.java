package com.schaffer.base.test;

import android.view.View;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseActivity;

/**
 * @author Schaffer
 * @date 2017/7/6
 */

public class TestStatusActivity extends BaseActivity<TestStatusActivity, TestStatusPresenter> {
    @Override
    protected void inflateView() {
        setLeftIconVisible(View.GONE, View.GONE);

//        testFor(R.layout.test_clock, "我的时钟");
        testFor(R.layout.test_gcard_view, "图层效果");
    }

    private void testFor(int test_gcard_view, String title) {
        inflateContent(test_gcard_view);
        setActivityTitle(title);
    }


    @Override
    protected TestStatusPresenter initPresenter() {
        return new TestStatusPresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void refreshData() {

    }
}
