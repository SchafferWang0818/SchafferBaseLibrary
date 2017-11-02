package com.schaffer.base.test;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseEmptyActivity;

/**
 * @author AndroidSchaffer
 * @date 2017/11/2
 */

public class TestCoordinateActivity extends BaseEmptyActivity<TestCoordinateActivity, TestCoordinatePresenter> {

    @Override
    protected void inflateView() {
        setContentView(R.layout.test_coordinate);
    }

    @Override
    protected TestCoordinatePresenter initPresenter() {
        return new TestCoordinatePresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void refreshData() {

    }
}
