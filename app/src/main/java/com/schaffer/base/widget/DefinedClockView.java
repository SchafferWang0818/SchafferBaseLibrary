package com.schaffer.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.schaffer.base.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author SchafferWang
 */

public class DefinedClockView extends View {


    private Paint circlePaint;
    private Paint quarterPaint;
    private Paint numPaint1;
    private Paint hoursPaint;
    private Paint minsPaint;
    private Paint centerPaint;
    private Paint secondsPaint;
    private Paint numPaint2;
    private Paint signPaint;
    private String sign = "D & W";
    private int hoursLength;
    private int minsLength;
    private int quarterLength;
    private int secondsLength;
    private float signTextSize;
    private float dateTextSize;
    private float timeTextSize;
    private int signColor;
    private int dateTextColor;
    private int timeTextColor;
    private int dialPlateColor;
    private int hoursHandsColor;
    private int minutesHandsColor;
    private int secondsHandsColor;
    private int quarterColor;
    private float dialPlateRadius;
    private float dialPlateStrokeSize;

    public DefinedClockView(Context context) {
        this(context, null);
    }

    public DefinedClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefinedClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefinedStyle(context, attrs);
        initPaint();
    }


    /**
     * <br>
     * <declare-styleable name="StyleForDefinedClockView">
     * <attr name="sign" format="string" />
     * <attr name="signTextSize" format="dimension|reference" />
     * <attr name="dateTextSize" format="dimension|reference" />
     * <attr name="timeTextSize" format="dimension|reference" />
     * <attr name="hoursHandsLength" format="dimension|reference" />
     * <attr name="minutesHandsLength" format="dimension|reference" />
     * <attr name="secondsHandsLength" format="dimension|reference" />
     * <attr name="quartersHandsLength" format="dimension|reference" />
     * <attr name="dialPlateRadius" format="dimension|reference" />
     * <attr name="dialPlateStrokeSize" format="dimension|reference" />
     * <attr name="signTextColor" format="color|reference" />
     * <attr name="dateTextColor" format="color|reference" />
     * <attr name="timeTextColor" format="color|reference" />
     * <attr name="dialPlateColor" format="color|reference" />
     * <attr name="hoursHandsColor" format="color|reference" />
     * <attr name="minutesHandsColor" format="color|reference" />
     * <attr name="secondsHandsColor" format="color|reference" />
     * <attr name="quarterColor" format="color|reference" />
     * </declare-styleable>
     * </br>
     *
     * @param context
     * @param attrs
     */
    private void initDefinedStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StyleForDefinedClockView);
        sign = ta.getString(R.styleable.StyleForDefinedClockView_sign);
        if (sign == null) {
            sign = "Watch";
        }
        signTextSize = ta.getDimension(R.styleable.StyleForDefinedClockView_signTextSize, sp2px(30));
        dateTextSize = ta.getDimension(R.styleable.StyleForDefinedClockView_dateTextSize, sp2px(20));
        timeTextSize = ta.getDimension(R.styleable.StyleForDefinedClockView_timeTextSize, sp2px(25));
       /* dialPlateRadius = ta.getDimension(R.styleable.StyleForDefinedClockView_dialPlateRadius, dp2px(25));*/
        dialPlateStrokeSize = ta.getDimension(R.styleable.StyleForDefinedClockView_dialPlateStrokeSize, dp2px(4));
        hoursLength = (int) ta.getDimension(R.styleable.StyleForDefinedClockView_hoursHandsLength, dp2px(60));
        minsLength = (int) ta.getDimension(R.styleable.StyleForDefinedClockView_minutesHandsLength, dp2px(100));
        secondsLength = (int) ta.getDimension(R.styleable.StyleForDefinedClockView_secondsHandsLength, dp2px(120));
        quarterLength = (int) ta.getDimension(R.styleable.StyleForDefinedClockView_quartersHandsLength, dp2px(15));


        signColor = ta.getColor(R.styleable.StyleForDefinedClockView_signTextColor, Color.BLACK);
        dateTextColor = ta.getColor(R.styleable.StyleForDefinedClockView_dateTextColor, Color.BLACK);
        timeTextColor = ta.getColor(R.styleable.StyleForDefinedClockView_timeTextColor, Color.BLACK);
        dialPlateColor = ta.getColor(R.styleable.StyleForDefinedClockView_dialPlateColor, Color.BLACK);
        hoursHandsColor = ta.getColor(R.styleable.StyleForDefinedClockView_hoursHandsColor, Color.BLACK);
        minutesHandsColor = ta.getColor(R.styleable.StyleForDefinedClockView_minutesHandsColor, Color.BLACK);
        secondsHandsColor = ta.getColor(R.styleable.StyleForDefinedClockView_secondsHandsColor, Color.BLACK);
        quarterColor = ta.getColor(R.styleable.StyleForDefinedClockView_quarterColor, Color.BLACK);
    }

    /**
     * 一刻钟
     */
    private void initPaint() {
        //圆圈
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(dialPlateColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(dialPlateStrokeSize);
        //刻钟刻度
        quarterPaint = new Paint();
        quarterPaint.setAntiAlias(true);
        quarterPaint.setColor(quarterColor);
        quarterPaint.setStrokeWidth(dp2px(5));
        quarterPaint.setStyle(Paint.Style.STROKE);
        //数字
        numPaint1 = new Paint();
        numPaint1.setAntiAlias(true);
        numPaint1.setColor(timeTextColor);
        numPaint1.setTextSize(timeTextSize);
        numPaint1.setStrokeWidth(dp2px(1));
        numPaint1.setStyle(Paint.Style.STROKE);

        numPaint2 = new Paint();
        numPaint2.setAntiAlias(true);
        numPaint2.setColor(dateTextColor);
        numPaint2.setTextSize(dateTextSize);
        numPaint2.setStrokeWidth(dp2px(1));
        numPaint2.setStyle(Paint.Style.STROKE);

        //sign

        signPaint = new Paint();
        signPaint.setAntiAlias(true);
        signPaint.setColor(signColor);
        signPaint.setTextSize(signTextSize);
        signPaint.setStrokeWidth(dp2px(1));
        signPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //时针
        hoursPaint = new Paint();
        hoursPaint.setAntiAlias(true);
        hoursPaint.setColor(hoursHandsColor);
        hoursPaint.setStrokeWidth(dp2px(6));
        hoursPaint.setStyle(Paint.Style.STROKE);
        //分针
        minsPaint = new Paint();
        minsPaint.setAntiAlias(true);
        minsPaint.setColor(minutesHandsColor);
        minsPaint.setStrokeWidth(dp2px(4));
        minsPaint.setStyle(Paint.Style.STROKE);
        //秒针
        secondsPaint = new Paint();
        secondsPaint.setAntiAlias(true);
        secondsPaint.setColor(secondsHandsColor);
        secondsPaint.setStrokeWidth(dp2px(2));
        secondsPaint.setStyle(Paint.Style.STROKE);
        //中心点
        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setColor(dialPlateColor);
        centerPaint.setStrokeWidth(dp2px(2));
        centerPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        canvas.translate(width / 2, height / 2);
        float r = (width - circlePaint.getStrokeWidth()) / 2;
        canvas.drawCircle(0, 0, r, circlePaint);
        canvas.drawPoint(0, 0, circlePaint);
        canvas.save();
        canvas.restore();

        Rect signR = new Rect();
        signPaint.getTextBounds(sign, 0, sign.length(), signR);
        canvas.drawText(sign, 0, sign.length(), -signR.width() / 2, -signR.height() - dp2px(30), signPaint);
        canvas.save();
        canvas.restore();

        Rect rect1 = new Rect();
        Rect rect2 = new Rect();
        Date d = new Date();
        DateFormat format = new SimpleDateFormat("HH : mm");
        String time = format.format(d);
        DateFormat format1 = new SimpleDateFormat("MM . dd");
        String date = format1.format(d);
        numPaint1.getTextBounds(time, 0, time.length(), rect1);
        numPaint2.getTextBounds(date, 0, date.length(), rect2);
        canvas.drawText(time, 0, time.length(), -rect1.width() / 2, rect1.height() + dp2px(30), numPaint1);
        canvas.drawText(date, 0, date.length(), -rect2.width() / 2, rect1.height() + dp2px(50) + rect2.height(), numPaint2);
        canvas.save();
        canvas.restore();
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(0, -width / 2, 0, -width / 2 + quarterLength, quarterPaint);
            canvas.rotate(90);
        }
        canvas.save();
        canvas.restore();

        for (int i = 0; i < 12; i++) {
            canvas.drawLine(0, -width / 2, 0, -width / 2 + dp2px(7), quarterPaint);
            canvas.rotate(30);
        }
        Calendar instance = Calendar.getInstance();
        int hour = instance.get(Calendar.HOUR);
        int minute = instance.get(Calendar.MINUTE);
        int second = instance.get(Calendar.SECOND);
        canvas.rotate(hour * 30 + minute * 0.5f);
        canvas.drawLine(0, 0, 0, -hoursLength, hoursPaint);
        canvas.save();
        canvas.restore();
        canvas.rotate(minute * 6 - (hour * 30 + minute * 0.5f));
        canvas.drawLine(0, 0, 0, -minsLength, minsPaint);
        canvas.save();
        canvas.restore();
        canvas.rotate(second * 6 - (hour * 30 + minute * 0.5f) - (minute * 6 - (hour * 30 + minute * 0.5f)));
        canvas.drawLine(0, 0, 0, -secondsLength, secondsPaint);
        canvas.save();
        canvas.restore();


        postInvalidateDelayed(1000);
    }


    public float dp2px(int dpValues) {
        return getResources().getDisplayMetrics().density * dpValues;
    }

    public int sp2px(final float spValues) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValues * fontScale + 0.5f);
    }


}
