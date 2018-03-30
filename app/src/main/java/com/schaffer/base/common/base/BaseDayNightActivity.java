package com.schaffer.base.common.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.constants.DayNight;
import com.schaffer.base.helper.DayNightHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndroidSchaffer on 2017/10/12.
 */

public abstract class BaseDayNightActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseEmptyActivity<V,P> {


    protected List<TextView> textViews = new ArrayList<>();
    protected List<? extends ViewGroup> viewGroups = new ArrayList<>();
    protected List<RecyclerView> recyclerViews = new ArrayList<>();
    protected List<? extends AdapterView<?>> adapterViews = new ArrayList<>();

    /**
     * 需要有切换主题需求时使用
     */
    private void initTheme() {
        DayNightHelper helper = new DayNightHelper(this);
        if (helper.isDay()) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme_Night);
        }
    }

    /**
     * 切换主题设置
     */
    private void toggleThemeSetting() {
        DayNightHelper helper = new DayNightHelper(this);
        if (helper.isDay()) {
            helper.setMode(DayNight.NIGHT);
            setTheme(R.style.AppTheme_Night);
        } else {
            helper.setMode(DayNight.DAY);
            setTheme(R.style.AppTheme);
        }
    }

    private void RefreshUIForChangeTheme() {
        TypedValue background = new TypedValue();//背景色
        TypedValue textColor = new TypedValue();//字体颜色
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.clock_background, background, true);
        theme.resolveAttribute(R.attr.clock_textColor, textColor, true);
        if (textViews.size() > 0) {
            for (TextView textView : textViews) {
                textView.setBackgroundResource(background.resourceId);
                textView.setTextColor(textColor.resourceId);
            }
        }

        if (viewGroups.size() > 0) {
            for (ViewGroup viewGroup : viewGroups) {
                viewGroup.setBackgroundResource(background.resourceId);
            }
        }

        if (adapterViews.size() > 0) {
            //todo adapterViews
        }

        if (recyclerViews.size() > 0) {
            //todo recyclerViews
        }
        //RecyclerView 解决方案

//        int childCount = mRecyclerView.getChildCount();
//        for (int childIndex = 0; childIndex < childCount; childIndex++) {
//            ViewGroup childView = (ViewGroup) mRecyclerView.getChildAt(childIndex);
//            childView.setBackgroundResource(background.resourceId);
//            View infoLayout = childView.findViewById(R.id.info_layout);
//            infoLayout.setBackgroundResource(background.resourceId);
//            TextView nickName = (TextView) childView.findViewById(R.id.tv_nickname);
//            nickName.setBackgroundResource(background.resourceId);
//            nickName.setTextColor(resources.getColor(textColor.resourceId));
//            TextView motto = (TextView) childView.findViewById(R.id.tv_motto);
//            motto.setBackgroundResource(background.resourceId);
//            motto.setTextColor(resources.getColor(textColor.resourceId));
//        }
//
//        //让 RecyclerView 缓存在 Pool 中的 Item 失效
//        //那么，如果是ListView，要怎么做呢？这里的思路是通过反射拿到 AbsListView 类中的 RecycleBin 对象，然后同样再用反射去调用 clear 方法
//        Class<RecyclerView> recyclerViewClass = RecyclerView.class;
//        try {
//            Field declaredField = recyclerViewClass.getDeclaredField("mRecycler");
//            declaredField.setAccessible(true);
//            Method declaredMethod = Class.forName(RecyclerView.Recycler.class.getName()).getDeclaredMethod("clear", (Class<?>[]) new Class[0]);
//            declaredMethod.setAccessible(true);
//            declaredMethod.invoke(declaredField.get(mRecyclerView), new Object[0]);
//            RecyclerView.RecycledViewPool recycledViewPool = mRecyclerView.getRecycledViewPool();
//            recycledViewPool.clear();
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        refreshStatusBar();
    }

    /**
     * 刷新 StatusBar
     */
    private void refreshStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            getWindow().setStatusBarColor(getResources().getColor(typedValue.resourceId));
        }
    }

    /**
     * 展示一个切换动画
     */
    private void showAnimation() {
        final View decorView = getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorView);
        if (decorView instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(this);
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorView).addView(view, layoutParam);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorView).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }

    public void changeTheme() {
        showAnimation();
        toggleThemeSetting();
        RefreshUIForChangeTheme();
    }


}
