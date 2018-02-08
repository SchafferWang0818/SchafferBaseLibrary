package com.schaffer.base.test;

import android.view.View;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseActivity;

/**
 * Created by AndroidSchaffer on 2017/10/10.
 */

public class TestBuglyActivity extends BaseActivity<TestBuglyActivity, TestBuglyPresenter> {


    @Override
    protected void inflateView() {
        inflateContent(R.layout.activity_test_bugly);
    }

    @Override
    protected TestBuglyPresenter initPresenter() {
        return new TestBuglyPresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void refreshData() {

    }


    public void testBugly(View v) {
        int i = 1;
        int j = 0;
        showToast(/*i / j + */"this is fixed bug , j !=0");//
    }
}
