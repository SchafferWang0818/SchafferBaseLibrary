package com.schaffer.base.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseFragment;

/**
 * Created by AndroidSchaffer on 2017/11/2.
 */

public class TestCoordinateFragment extends BaseFragment<TestCoordinateFragment, TestCoordinatePresenter1> {


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
//        return inflater.inflate(R.layout.test_coordinate_fragment, container,false);
        return inflater.inflate(R.layout.activity_base_design, container,false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void refreshData() {

    }
}
