package com.schaffer.base.common.transformer;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by EZ on 2016/12/24.
 */

public class ClickableSpanTransformer extends ClickableSpan {

    private View.OnClickListener mListener;

    public ClickableSpanTransformer(View.OnClickListener l) {
        mListener = l;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor("#ff7f00"));//文本颜色
        ds.setUnderlineText(false);//是否有下划线
//        ds.bgColor = Color.WHITE;//背景颜色
    }

    @Override
    public void onClick(View v) {
        if (null != mListener) {
            mListener.onClick(v);
        }
    }

}
