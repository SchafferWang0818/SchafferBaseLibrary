package com.schaffer.base.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.schaffer.base.common.constants.DayNight;

/**
 * Created by a7352 on 2017/7/26.
 */

public class DayNightHelper {

    private final static String FILE_NAME = "settings";
    private final static String MODE = "day_night_mode";

    private SharedPreferences mSharedPreferences;

    public DayNightHelper(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存模式设置
     *
     * @param mode
     * @return
     */
    public boolean setMode(DayNight mode) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(MODE, mode.getName());
        return editor.commit();
    }

    /**
     * 夜间模式
     *
     * @return
     */
    public boolean isNight() {
        String mode = mSharedPreferences.getString(MODE, DayNight.DAY.getName());
        if (DayNight.NIGHT.getName().equals(mode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 日间模式
     *
     * @return
     */
    public boolean isDay() {
        String mode = mSharedPreferences.getString(MODE, DayNight.DAY.getName());
        if (DayNight.DAY.getName().equals(mode)) {
            return true;
        } else {
            return false;
        }
    }
}