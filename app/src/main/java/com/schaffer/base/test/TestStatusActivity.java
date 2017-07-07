package com.schaffer.base.test;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseActivity;

/**
 * Created by a7352 on 2017/7/6.
 */

public class TestStatusActivity extends BaseActivity<TestStatusActivity, TestStatusPresenter> {
    @Override
    protected void inflateView() {
        inflateContent(R.layout.activity_test_fragment);
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
