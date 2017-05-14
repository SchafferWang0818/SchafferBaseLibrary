package com.schaffer.base.common.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 *  Created by schaffer on 2016/9/12.
 */

public class LUtil {

    private static boolean isDebug = true;
    private static String mTag = "SchafferWang";

    public static void init(String tag,boolean isDebug) {
        LUtil.isDebug = isDebug;
        LUtil.mTag = tag;
    }

    public static void e(){
        e("");
    }

    public static void e(String msg) {
        if (!isDebug)
            return;
        e(null,msg);
    }

    public static void e(String tag,String msg){
        if(!isDebug)
            return;
        String finalTag = getWithTag(tag);
        StackTraceElement stackTraceElement = getTagetStackTraceElement();
        Log.e(finalTag, "("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")"+stackTraceElement.getMethodName()+"::"+msg);
    }

    private static String getWithTag(String tag){
        if(!TextUtils.isEmpty(tag))
            return tag;
        return mTag;
    }


    private static StackTraceElement getTagetStackTraceElement() {
        StackTraceElement tagetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace){
            boolean isLogMethod = stackTraceElement.getClassName().equals(LUtil.class.getName());
            if(shouldTrace && !isLogMethod){
                tagetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return tagetStackTrace;
    }
}
