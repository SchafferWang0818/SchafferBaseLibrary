package com.schaffer.base.test;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseFragment;
import com.schaffer.base.common.glide.BannerGlideLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndroidSchaffer on 2017/11/2.
 */

public class TestCoordinateFragment extends BaseFragment<TestCoordinateFragment, TestCoordinatePresenter1> {

    String title;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        return testCoordinateFragment(inflater, container);
//        return inflater.inflate(R.layout.test_coordinate_fragment_normal, container,false);
//        return inflater.inflate(R.layout.activity_base_design, container,false);
    }

    /**
     * 重点内容是{@link android.support.v4.app.Fragment#setHasOptionsMenu(boolean)}设置Fragment的Toolbar的menu
     *
     * @param inflater
     * @param container
     * @return
     */
    @NonNull
    private View testCoordinateFragment(LayoutInflater inflater, ViewGroup container) {
        View inflate = inflater.inflate(R.layout.test_coordinate_fragment2, container, false);
        AppBarLayout viewById = (AppBarLayout) inflate.findViewById(R.id.test_coordinate_fragment_abl_appbar);
        final CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) inflate.findViewById(R.id.test_coordinate_fragment_ctl_collapse);
        final TabLayout tab = (TabLayout) inflate.findViewById(R.id.test_coordinate_fragment_tl_tab);
        final Banner pager = (Banner) inflate.findViewById(R.id.test_coordinate_fragment_vp_page);
        setBanner(pager);
        title = "首页";

        tab.setVisibility(View.GONE);
        final Toolbar tb = (Toolbar) inflate.findViewById(R.id.test_coordinate_fragment_tb_toolbar);
        tb.setTitle(title);
        ctl.setTitle(title);
        viewById.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset != 0) {//滑动时
                    if (tab.getVisibility() != View.VISIBLE) {
                        tab.setVisibility(View.VISIBLE);
                        pager.stopAutoPlay();
                        tb.setTitle("");
                        ctl.setTitle("");
                    }
                    tab.setAlpha(((float) (Math.abs(verticalOffset)) / appBarLayout.getTotalScrollRange()));
                } else {
                    tab.setVisibility(View.GONE);
                    pager.startAutoPlay();
                    tb.setTitle(title);
                    ctl.setTitle(title);
                }

            }
        });
        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("TAG", "mmp");
                        Toast.makeText(activity, "this", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        setHasOptionsMenu(true);
        ((AppCompatActivity) activity).setSupportActionBar(tb);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });
        return inflate;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_test_fragment_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void refreshData() {

    }

    public void setBanner(Banner banner) {
        List<String> images = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1509705156022&di=8e5b2e45c96ec6a0e5591ef2426115aa&imgtype=0&src=http%3A%2F%2Fupload.bbs.csuboy.com%2FDay_110418%2F47_139202_e1774290311f4fb.jpg");
            titles.add("i->" + i);
        }
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new BannerGlideLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.CubeOut);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

}
