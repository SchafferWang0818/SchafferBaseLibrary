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
//        inflateContent(R.layout.activity_test_fragment);
        inflateContent(R.layout.test_clock);
        setActivityTitle("我的时钟");
        setLeftIconVisible(View.GONE,View.GONE);
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
