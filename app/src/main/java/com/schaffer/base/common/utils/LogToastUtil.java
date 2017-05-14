package com.schaffer.base.common.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.schaffer.base.BuildConfig;


/**
 * Created by SchafferWang on 2016/12/8.
 */

public class LogToastUtil {

	private static Toast toast;

	private LogToastUtil() {
	}

	private static void show(Context context, String msg, int duration, int gravity) {
		if (toast == null) {
			toast = Toast.makeText(context.getApplicationContext(), msg, duration);
		} else {
			toast.setText(msg);
			toast.setDuration(duration);
		}
		if (gravity == Gravity.CENTER)
			toast.setGravity(gravity, 0, 0);
		toast.show();
	}

	public static void showShort(Context context, String message) {
		show(context.getApplicationContext(), message, Toast.LENGTH_SHORT, 0);
	}

	public static void showShort(Context context, int resId) {
		show(context.getApplicationContext(), context.getString(resId), Toast.LENGTH_SHORT, 0);
	}

	public static void showShortCenter(Context context, String message) {
		w(message);
		show(context.getApplicationContext(), message, Toast.LENGTH_SHORT, Gravity.CENTER);
	}

	public static void w(String tag, String content) {
		if (BuildConfig.DEBUG) {
			Log.w("Schaffer->" + tag, content);
		}
		Log.w("Schaffer->" + tag, content);
	}

	public static void w(String content) {
		if (BuildConfig.DEBUG) {
			Log.w("Schaffer->", content);
		}
		Log.w("Schaffer->", content);
	}

	public static void d(String tag, String content) {
		if (BuildConfig.DEBUG) {
			Log.d("Schaffer->" + tag, content);
		}
		Log.d("Schaffer->" + tag, content);
	}

	public static void d(String content) {
		if (BuildConfig.DEBUG) {
			Log.d("Schaffer->", content);
		}
		Log.d("Schaffer->", content);
	}
}