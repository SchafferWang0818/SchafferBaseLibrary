package com.schaffer.base.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  Created by SchafferWang on 2016/12/5 0005.
 */

public class SPUtil {

	public static void saveSetting(Context context, String name, String content) {
		SharedPreferences.Editor user_setting;
		if (name.equals("first")) {
			user_setting = context.getSharedPreferences("first", Context.MODE_PRIVATE).edit();
		} else {
			user_setting = context.getSharedPreferences("user_setting", Context.MODE_PRIVATE).edit();
		}
		user_setting.remove(name);
		user_setting.putString(name, content).apply();

	}

	public static void saveSetting(Context context, String name, int content) {
		SharedPreferences.Editor user_setting = context.getSharedPreferences("user_setting", Context.MODE_PRIVATE).edit();
		user_setting.remove(name);
		user_setting.putInt(name, content).apply();
	}

	public static void removeSetting(Context context) {
		context.getSharedPreferences("user_setting", Context.MODE_PRIVATE).edit().clear().apply();
	}

	public static String getSetting(Context context, String name) {
		if (name.equals("first")) {
			return context.getSharedPreferences("first", Context.MODE_PRIVATE).getString("first", "");
		} else {
			return context.getSharedPreferences("user_setting", Context.MODE_PRIVATE).getString(name, "");
		}
	}

	public static int getIntSetting(Context context, String name) {
		return context.getSharedPreferences("user_setting", Context.MODE_PRIVATE).getInt(name, 0);
	}

}
