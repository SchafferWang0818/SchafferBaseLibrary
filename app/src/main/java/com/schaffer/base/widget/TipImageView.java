package com.schaffer.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.schaffer.base.common.utils.ConvertUtils;


/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/3/3
 * Project : fentu_android_new
 * Package : com.billliao.fentu.widget
 * Description :
 */

public class TipImageView extends android.support.v7.widget.AppCompatImageView {

    private int mTipCount = 0;
    private int mTipColor = Color.parseColor("#ff6e62");
    private int mTipTextSize = ConvertUtils.sp2px(8);
    private Paint mTextPaint;
    private Paint mCirclePaint;
    private String mTipText;
    private Paint mInnerCirclePaint;

    public TipImageView(Context context) {
        this(context, null);
    }

    public TipImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTipPaint();
    }

    private void initTipPaint() {
        mTextPaint = new Paint();
        mTextPaint.setColor(mTipColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTipTextSize);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mTipColor);
        mCirclePaint.setAntiAlias(true);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(Color.WHITE);
        mInnerCirclePaint.setAntiAlias(true);

    }

    public int getTipColor() {
        return mTipColor;
    }

    public void setTipColor(@ColorInt int tipColor) {
        mTipColor = tipColor;
    }

    public int getTipTextSize() {
        return mTipTextSize;
    }

    public void setTipTextSize(int tipTextSize) {
        mTipTextSize = tipTextSize;
    }

    public int getTipCount() {
        return mTipCount;
    }

    public void setTipCount(int tipCount) {
        mTipCount = tipCount;
        if (mTipCount > 0 && mTipCount < 99) {
            mTipText = mTipCount + "";
        } else {
            mTipText = "99+";
        }
        postInvalidate();
    }

    public void tipToZero() {
        setTipCount(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /* 1. 根据数字的宽高 + 4dp = 确定的圆弧半径 */
        if (mTipCount == 0 || TextUtils.isEmpty(mTipText)) {
            return;
        }

        Rect rectF = new Rect();
        mTextPaint.getTextBounds("99+", 0, "99+".length(), rectF);
        int textHeight = rectF.height();
        int textWidth = rectF.width();
       /* float textWidth = mTextPaint.measureText(mTipText);*/
        float innerCircleRadius = textWidth / 2 + ConvertUtils.dp2px(1);
        float outerCircleRadius = textWidth / 2 + ConvertUtils.dp2px(2);

        int measuredWidth = getMeasuredWidth();

        canvas.translate(measuredWidth - outerCircleRadius, outerCircleRadius);
        canvas.drawCircle(0, 0, outerCircleRadius, mCirclePaint);
        canvas.drawCircle(0, 0, innerCircleRadius, mInnerCirclePaint);

        canvas.save();
        canvas.restore();

        Rect rect = new Rect();
        mTextPaint.getTextBounds(mTipText, 0, mTipText.length(), rect);
        int width = rect.width();
        canvas.drawText(mTipText, -width / 2, textHeight / 2, mTextPaint);

        canvas.save();
        canvas.restore();
    }
}
