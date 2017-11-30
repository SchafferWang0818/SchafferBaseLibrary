package com.schaffer.base.common.base;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.schaffer.base.R;

/**
 * @author Schaffer
 * @date 2017/10/12
 */

public abstract class BaseAppCompatActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseEmptyActivity<V, P> {

    protected void inflateContent(@LayoutRes int resId) {
        inflateContent(resId, null);
    }

    protected void inflateContent(View inflateView) {
        inflateContent(inflateView, null);
    }

    protected void inflateContent(@LayoutRes int resId, FrameLayout.LayoutParams params) {
        inflateContent(View.inflate(this, resId, null), params);
    }

    protected void inflateContent(View inflateView, FrameLayout.LayoutParams params) {
        if (mFrameContent != null && inflateView != null) {
            mFrameContent.addView(inflateView, params == null ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) : params);
        }
    }
    /**
     * -------------------------------------------------------------标题处理----------------------------------------------------------------------------------------
     */
    public void setToolbar() {
        if (findViewById(R.id.layout_toolbar_tb) == null) return;
        if (this instanceof AppCompatActivity) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.layout_toolbar_tb);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.widget.Toolbar toolbar = (android.widget.Toolbar) findViewById(R.id.layout_toolbar_tb);
                setActionBar(toolbar);
                getActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        if (getTitle() != null) {
            setActivityTitle(getTitle());
        }
        setLeftClick(null);
    }

    protected void setActivityTitle(CharSequence charSequence) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_title)).setText(charSequence);
    }

    public void setToolbar(int visibility) {
        setToolbar();
        findViewById(R.id.layout_toolbar_tb).setVisibility(visibility);
    }


    protected void setLeftIcon(@DrawableRes int resId, View.OnClickListener listener) {
        ((ImageView) findViewById(R.id.layout_toolbar_iv_back)).setImageResource(resId);
        setLeftIconVisible(View.VISIBLE);
        setLeftClick(listener);
    }

    protected void setLeftClick(View.OnClickListener listener) {
        findViewById(R.id.layout_toolbar_iv_back).setOnClickListener(listener != null ? listener : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void setLeftIconVisible(int visible) {
        findViewById(R.id.layout_toolbar_iv_back).setVisibility(visible == View.VISIBLE ? View.VISIBLE : View.GONE);
        findViewById(R.id.layout_toolbar_tv_left).setVisibility(visible == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    protected void setLeftIconVisible(int ivVisible, int tvVisible) {
        findViewById(R.id.layout_toolbar_iv_back).setVisibility(ivVisible);
        findViewById(R.id.layout_toolbar_tv_left).setVisibility(tvVisible);
    }

    protected void setLeftText(String content, View.OnClickListener onClickListener) {
        setLeftIconVisible(View.GONE);
        findViewById(R.id.layout_toolbar_tv_left).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.layout_toolbar_tv_left)).setText(content);
    }

    protected void setLeftText(int spValue, @ColorInt int color, String content, View.OnClickListener onClickListener) {
        setLeftText(content, onClickListener);
        setLeftTextColor(color);
        setLeftTextSize(spValue);
    }

    protected void setLeftTextSize(int spValue) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_left)).setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
    }

    protected void setLeftTextColor(@ColorInt int color) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_left)).setTextColor(color);
    }


    protected void setRightText(String content, View.OnClickListener onClickListener) {
        setRightText(content, View.VISIBLE, onClickListener);
    }

    protected void setRightText(String content, int visibility, View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(content)) {
            ((TextView) findViewById(R.id.layout_toolbar_tv_right)).setText(content);
            findViewById(R.id.layout_toolbar_tv_right).setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            findViewById(R.id.layout_toolbar_iv_right).setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        if (onClickListener != null) {
            findViewById(R.id.layout_toolbar_tv_right).setOnClickListener(onClickListener);
        }
    }

    protected void setRightTextColor(@ColorInt int color) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_right)).setTextColor(color);
    }

    protected void setRightTextColor(String color) {
        if (!color.startsWith("#") && !(color.length() != 4 || color.length() != 5 || color.length() != 7 || color.length() != 9))
            return;
        setRightTextColor(Color.parseColor(color));
    }

    protected void setRightTextSize(int spValue) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_right)).setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
    }


    protected void setRightIcon(@DrawableRes int resId, View.OnClickListener onClickListener) {
        setRightIcon(resId, View.VISIBLE, onClickListener);
    }

    protected void setRightIcon(@DrawableRes int resId, int visibility, View.OnClickListener onClickListener) {
        if (resId != 0) {
            if (onClickListener != null) {
                findViewById(R.id.layout_toolbar_iv_right).setOnClickListener(onClickListener);
            }
            findViewById(R.id.layout_toolbar_tv_right).setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
            findViewById(R.id.layout_toolbar_iv_right).setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            ((ImageView) findViewById(R.id.layout_toolbar_iv_right)).setImageResource(resId);
        }
    }

    protected void setToolbar(int visible, String title, boolean leftBack, boolean rightAllDismiss, boolean rightTextShow, String right, @DrawableRes int rightResId, View.OnClickListener rightClick) {
        setToolbar(visible);
        if (visible == View.GONE) return;
        setActivityTitle(title == null ? "" : title);
        if (leftBack) {
            setLeftClick(null);
        }
        if (rightAllDismiss) return;
        if (rightTextShow) {
            setRightText(right, rightClick);
        } else {
            setRightIcon(rightResId, rightClick);
        }
    }

    public void setToolbarBackground(int color) {
        (findViewById(R.id.layout_toolbar_tb)).setBackgroundColor(color);
    }

    public int getToolbarBackgroundColor() {
        return (findViewById(R.id.layout_toolbar_tb)).getDrawingCacheBackgroundColor();
    }
}
