package com.schaffer.base.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schaffer.base.common.utils.LTUtils;

/**
 * Created by a7352 on 2017/7/5.
 */

public class TestBaseFragment extends Fragment {

    private Activity activity;
    private String simpleName;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        simpleName = getClass().getSimpleName();
        this.activity = activity;
        LTUtils.w(simpleName + "#onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LTUtils.w(simpleName + "#onCreate()");
    }

    /**
     * 可以获取到保存的变量内容
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(activity);
        textView.setText(getClass().getSimpleName());
        if (savedInstanceState!=null){
            textView.setText(savedInstanceState.getString("name"));
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        LTUtils.w(simpleName + "#onCreateView()");
        return textView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LTUtils.w(simpleName + "#onViewCreated()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LTUtils.w(simpleName + "#onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        LTUtils.w(simpleName + "#onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        LTUtils.w(simpleName + "#onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LTUtils.w(simpleName + "#onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        LTUtils.w(simpleName + "#onPause()");
    }

    /**
     * 保存Fragment由于Activity状态的变化
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("name", simpleName + "保存的内容");
        super.onSaveInstanceState(outState);
        LTUtils.w(simpleName + "#onSaveInstanceState()");
    }

    @Override
    public void onStop() {
        super.onStop();
        LTUtils.w(simpleName + "#onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LTUtils.w(simpleName + "#onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LTUtils.w(simpleName + "#onDestroy()");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        LTUtils.w(simpleName + "#onDetach()");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LTUtils.w(simpleName + "#onHiddenChanged()->" + hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LTUtils.w(simpleName + "#setUserVisibleHint()->" + isVisibleToUser);
    }
}
