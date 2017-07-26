package com.schaffer.base.test;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseActivity;

import java.util.List;

/**
 * Created by a7352 on 2017/7/6.
 */

public class TestStatusActivity extends BaseActivity<TestStatusActivity, TestStatusPresenter> {
    @Override
    protected void inflateView(List<TextView> textViews, List<? extends ViewGroup> viewGroups, List<RecyclerView> recyclerViews, List<? extends AdapterView<?>> adapterViews) {
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
