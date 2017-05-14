package com.schaffer.base.common.utils;

import android.content.Context;

/**
 * Created by SchafferW on 2016/11/25.
 */

public class DimenUtil {
	public static int dp2px(Context context, int value) {
		return (int) (context.getResources().getDisplayMetrics().density * value + 0.5f);
	}

	public static int sp2px(Context context, int value) {
		return (int) (context.getResources().getDisplayMetrics().scaledDensity * value + 0.5f);
	}
}
