package com.schaffer.base.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.schaffer.base.R;

/**
 * Created by a7352 on 2017/7/5.
 */

public class TestFragmentActivity extends AppCompatActivity {

    private android.widget.RadioButton mrb1;
    private android.widget.RadioButton mrb2;
    private android.widget.RadioButton mrb3;
    private android.widget.RadioButton mrb4;
    private android.widget.RadioGroup mrg;
    private android.widget.FrameLayout mgroupframe;
    private Test1 test1;
    private Test2 test2;
    private Test4 test4;
    private Test3 test3;
    private FragmentManager supportFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        initView();
        supportFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            if (supportFragmentManager.getFragment(savedInstanceState, "test1") != null) {
                test1 = (Test1) supportFragmentManager.getFragment(savedInstanceState, "test1");
            }
            if (supportFragmentManager.getFragment(savedInstanceState, "test2") != null) {
                test2 = (Test2) supportFragmentManager.getFragment(savedInstanceState, "test2");
            }
            if (supportFragmentManager.getFragment(savedInstanceState, "test3") != null) {
                test3 = (Test3) supportFragmentManager.getFragment(savedInstanceState, "test3");
            }
            if (supportFragmentManager.getFragment(savedInstanceState, "test4") != null) {
                test4 = (Test4) supportFragmentManager.getFragment(savedInstanceState, "test4");
            }
        }
        initListener();
        mrb1.performClick();
    }

    /**
     * supportFragmentManager.putFragment(outState, "test1", test1);保存时 index=-1造成Fragment Test1{375c1b11} is not currently in the FragmentManager
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (supportFragmentManager == null) return;
//        if (test1 != null && !test1.isDetached()) {
//            supportFragmentManager.putFragment(outState, "test1", test1);
//        }
//        if (test2 != null && !test2.isDetached()) {
//            supportFragmentManager.putFragment(outState, "test2", test2);
//        }
//        if (test3 != null && !test3.isDetached()) {
//            supportFragmentManager.putFragment(outState, "test3", test3);
//        }
//        if (test4 != null && !test4.isDetached()) {
//            supportFragmentManager.putFragment(outState, "test4", test4);
//        }
    }

    /**
     * 都加入回退栈造成点击返回键重叠和Fragment already added的问题
     */
    private void initListener() {
        mrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = supportFragmentManager.beginTransaction();
                switch (checkedId) {
                    case R.id.test_fragment_rb_1:
                        if (test1 == null) {
                            test1 = new Test1();
//                            transaction.addToBackStack("test1");
                        }
                        transaction.replace(R.id.test_fragment_group_frame, test1, "test1");
                        break;
                    case R.id.test_fragment_rb_2:
                        if (test2 == null) {
                            test2 = new Test2();
//                            transaction.addToBackStack("test2");
                        }
                        transaction.replace(R.id.test_fragment_group_frame, test2, "test2");
                        break;
                    case R.id.test_fragment_rb_3:
                        if (test3 == null) {
                            test3 = new Test3();
//                            transaction.addToBackStack("test3");
                        }
                        transaction.replace(R.id.test_fragment_group_frame, test3, "test3");
                        break;
                    case R.id.test_fragment_rb_4:
                        if (test4 == null) {
                            test4 = new Test4();
//                            transaction.addToBackStack("test4");
                        }
                        transaction.replace(R.id.test_fragment_group_frame, test4, "test4");
                        break;
                }
                transaction.commit();
            }
        });
    }

    private void initView() {
        this.mgroupframe = (FrameLayout) findViewById(R.id.test_fragment_group_frame);
        this.mrg = (RadioGroup) findViewById(R.id.test_fragment_rg);
        this.mrb4 = (RadioButton) findViewById(R.id.test_fragment_rb_4);
        this.mrb3 = (RadioButton) findViewById(R.id.test_fragment_rb_3);
        this.mrb2 = (RadioButton) findViewById(R.id.test_fragment_rb_2);
        this.mrb1 = (RadioButton) findViewById(R.id.test_fragment_rb_1);
    }
}
