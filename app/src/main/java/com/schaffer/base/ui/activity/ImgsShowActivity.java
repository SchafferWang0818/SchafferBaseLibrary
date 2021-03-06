package com.schaffer.base.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.schaffer.base.common.adapter.ImgsPathShowSingleViewAdapter;
import com.schaffer.base.common.adapter.ImgsResShowSingleViewAdapter;
import com.schaffer.base.common.base.BaseEmptyActivity;
import com.schaffer.base.common.transformer.GlideCircleTransformer;
import com.schaffer.base.common.utils.ImageUtils;
import com.schaffer.base.presenter.ImgsShowPresenter;
import com.schaffer.base.ui.dialog.ImgSaveDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SchafferWang on 2017/7/18.
 * 用于图片显示
 */

public class ImgsShowActivity extends BaseEmptyActivity<ImgsShowActivity, ImgsShowPresenter> {


    private ViewPager pager;
    private int[] img_res;
    private String[] paths;
    private int startIndex;
    private boolean localResouce = false;
    private int length;
    private ImgsPathShowSingleViewAdapter pathAdapter;
    private ImgsResShowSingleViewAdapter resAdapter;
    public static final String INTENT_DATA_IMG_PATHS = "img_paths";
    public static final String INTENT_DATA_IMG_RES = "img_resIds";
    public static final String INTENT_DATA_IMG_CURRENT_INDEX = "img_current";
    @Override
    protected void inflateView() {
        pager = new ViewPager(this);
        inflateContent(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0 && pager.getAdapter() != null) {
                    setActivityTitle((position + 1) + " / " + pager.getAdapter().getCount());
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected ImgsShowPresenter initPresenter() {
        return new ImgsShowPresenter();
    }

    /**
     * 初始化ViewPager数据:
     * 1. 从网页中获取的图片url或者从app中resouce获得的图片resId
     * 2. 页数
     * 3. 当前页的位置
     */
    @Override
    protected void initData() {
        List<ImageView> pages = new ArrayList<>();
        Intent startIntent = getIntent();
        paths = startIntent.getStringArrayExtra(INTENT_DATA_IMG_PATHS);
        startIndex = startIntent.getIntExtra(INTENT_DATA_IMG_CURRENT_INDEX, 0);
        if (paths.length != 0) {
            localResouce = false;
            length = paths.length;
            for (int i = 0; i < length; i++) {
                final ImageView view = new ImageView(this);
                Glide.with(this).load(paths[i].trim())
                        /*.fit()*/
//                        .asGif()//当前加载的图片不是一个正确的 Gif 格式，则会去显示 error() 配置的图片。
//                        .asBitmap()//显示GIF第一帧
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .priority(Priority.IMMEDIATE)
                        .transform(new GlideCircleTransformer(this))
                        .crossFade()//动画  默认 300ms
//                        .dontAnimate() //禁用动画效果。
                        /*.placeholder(R.drawable.placeholder)
                        .error(R.drawable.imagenotfound)*/
                        .into(view);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showMenuDialog(view);
                        return true;
                    }
                });
                pages.add(view);
                pathAdapter = new ImgsPathShowSingleViewAdapter(this, null, pages);
                pager.setAdapter(pathAdapter);
            }
        } else {
            localResouce = true;
            img_res = startIntent.getIntArrayExtra(INTENT_DATA_IMG_RES);
            if (img_res.length != 0) {
                length = img_res.length;
                for (int i = 0; i < length; i++) {
                    final ImageView view = new ImageView(this);
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showMenuDialog(view);
                            return true;
                        }
                    });
                    pages.add(view);
                    resAdapter = new ImgsResShowSingleViewAdapter(this, null, pages, img_res);
                    pager.setAdapter(resAdapter);
                }
            }
        }
    }

    @Override
    protected void refreshData() {

    }

    private void saveBitmapToLocation(Bitmap bitmap) {
        ImageUtils.saveBitmap2Png(bitmap);
        showSnackbar("图片已保存", Snackbar.LENGTH_SHORT);
    }

    private void showMenuDialog(ImageView view) {
        ImgSaveDialog dialog = new ImgSaveDialog(this) {
            @Override
            protected void onPicSave() {
                super.onPicSave();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
                        view.setDrawingCacheEnabled(false);
                        saveBitmapToLocation(bitmap);
                    }
                }, 2000);
            }
        };
        dialog.show();
    }

}
