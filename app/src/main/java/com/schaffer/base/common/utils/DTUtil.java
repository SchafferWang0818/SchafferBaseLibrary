package com.schaffer.base.common.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by SchafferWang on 2016/11/1.
 */

public class DTUtil {

    private DTUtil() {
    }


    /**
     * 获取当前年份
     *
     * @return 年
     */
    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.YEAR);
    }


    /**
     * 获取当前月份
     *
     * @return 月
     */
    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前月份的日期号码
     *
     * @return 日期
     */
    public static int getCurrentDay() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return 当前小时
     */
    public static int getCurrentHour() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 当前分钟
     *
     * @return
     */
    public static int getCurrentMinute() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.MINUTE);
    }


    public static String getCurrentDate(String regular) {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(regular);
        return sf.format(d);
    }


    public static String formatDateToString(Date date, String regular) {
        if (!TextUtils.isEmpty(regular)) {
            SimpleDateFormat format = new SimpleDateFormat(regular);
            return format.format(date);
        } else {
            return "";
        }
    }


    // date要转换的date类型的时间
    public static long formatDateToTimeStamp(Date date) {
        return date.getTime() / 1000;
    }


    public static Date formatStringToDate(String strTime, String regular) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(regular);
        return formatter.parse(strTime);
    }

    public static long formatStringToTimeStamp(String strTime, String regular) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(regular);
        return formatter.parse(strTime).getTime() / 1000;
    }

    public static String formatTimeStampToString(long timeStamp, String regular) {
        Date date = new Date(timeStamp * 1000);
        if (!TextUtils.isEmpty(regular)) {
            SimpleDateFormat format = new SimpleDateFormat(regular);
            return format.format(date);
        } else {
            return "";
        }
    }

    /**
     * 时间戳转Date
     *
     * @return Date
     */
    public static Date formatTimeStampToDate(int timeStamp) {
        return new Date(timeStamp * 1000);
    }


    /**
     * 得到剩余时间
     *
     * @param start_time 时间戳
     * @return 剩余时间字符串
     */
    public static String getDuring(long start_time) {
        Date date = new Date();
        long restTime = start_time - formatDateToTimeStamp(date);
        long timeStamp = restTime;//秒
        if (timeStamp < 60 && timeStamp >= 0) {//60秒之内
            return "即将";
        } else if (timeStamp >= 60 && timeStamp < 3600) {//一个小时之内
            return "" + timeStamp / 60 + "分钟后";
        } else if (timeStamp >= 3600 && timeStamp < 3600 * 24) {//一天之内
            return timeStamp / 3600 + "小时后";
        } else if (timeStamp >= 3600 * 24 && timeStamp < 3600 * 24 * 30) {//一个月之内
            return timeStamp / 3600 / 24 + "天后";
        } else if (timeStamp >= 3600 * 24 * 30 && timeStamp < 3600 * 24 * 30 * 12) {//一年之内
            return timeStamp / 3600 / 24 / 30 + "个月后";
        } else if (timeStamp >= 3600 * 24 * 30 * 12) {//n年后
            return timeStamp / 3600 / 24 / 30 / 12 + "年后";
        } else {
            if (timeStamp < 0) {
                return "即将过期";
            } else {
                return "很久以后";
            }
        }
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     *
     * @param timeStamp
     * @return
     */
    public static String getBefore(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }
    }


}
