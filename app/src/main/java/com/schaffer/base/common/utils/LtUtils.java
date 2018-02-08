package com.schaffer.base.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.schaffer.base.BuildConfig;


/**
 * @author SchafferWang
 * @date 2016/12/8
 */

public class LtUtils {

    private static Toast toast;
    private static boolean isDebug = true;
    private static String mTag = "Schaffer";

    private LtUtils() {
    }

    private static void showToast(Context context, String msg, int duration, int gravity) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), msg, duration);
        } else {
            toast.setText(msg);
            toast.setDuration(duration);
        }
        if (gravity == Gravity.CENTER) {
            toast.setGravity(gravity, 0, 0);
        }
        toast.show();
    }

    public static void showToastShort(Context context, String message) {
        showToast(context.getApplicationContext(), message, Toast.LENGTH_SHORT, 0);
    }

    public static void showToastShort(Context context, int resId) {
        showToast(context.getApplicationContext(), context.getString(resId), Toast.LENGTH_SHORT, 0);
    }

    public static void showToastShortCenter(Context context, String message) {
        w(message);
        showToast(context.getApplicationContext(), message, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    public static void w(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.w("Schaffer->" + tag, content);
        }
    }

    public static void w(String content) {
        if (BuildConfig.DEBUG) {
            Log.w("Schaffer->", content);
        }
    }

    public static void d(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.d("Schaffer->" + tag, content);
        }
    }

    public static void d(String content) {
        if (BuildConfig.DEBUG) {
            Log.d("Schaffer->", content);
        }
    }


    public static void e() {
        e("");
    }

    public static void e(String msg) {
        if (!BuildConfig.DEBUG && !isDebug) {
            return;
        }
        e(null, msg);
    }

    public static void e(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        String finalTag = getWithTag(tag);
        StackTraceElement stackTraceElement = getTagetStackTraceElement();
        Log.e(finalTag, "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")" + stackTraceElement.getMethodName() + "::" + msg);
    }

    private static String getWithTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return mTag;
    }

    private static StackTraceElement getTagetStackTraceElement() {
        StackTraceElement tagetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(LtUtils.class.getName());
            if (shouldTrace && !isLogMethod) {
                tagetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return tagetStackTrace;
    }
}