package com.schaffer.base.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.IPickerViewData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SchafferWang on 2017/4/20 0020.
 */

public class STUtil {


    private static STUtil mSelectTimeUtils;

    ArrayList<String> item1s = new ArrayList<>();
    ArrayList<ArrayList<String>> item2s = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<IPickerViewData>>> item3s = new ArrayList<>();

    public static STUtil getInstance() {
        if (mSelectTimeUtils == null) {
            mSelectTimeUtils = new STUtil();
        }
        return mSelectTimeUtils;
    }

    public ArrayList<String> set1Items() {
        Date date = new Date();
        int month = date.getMonth() + 1;
        int day = date.getDate();
        int year = date.getYear() + 1900;
        int minutes = date.getMinutes();
        int hour = date.getHours();
        ArrayList<String> dateList = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            if (i == 0) {
                dateList.add("今天");
            } else if (i == 1) {
                dateList.add("明天");
            } else if (i == 2) {
                dateList.add("后天");
            } else {
                if (day + i < 10) {
                    if (month < 10) {
                        dateList.add("0" + month + "月0" + (day + i) + "日");
                    } else {
                        dateList.add(month + "月0" + (day + i) + "日");
                    }
                } else {
                    if (getCurrentMonthLastDay() < day + i) {//转到下一个月
                        if (month < 9) {
                            if (day + i - getCurrentMonthLastDay() < 10) {
                                dateList.add("0" + (month + 1) + "月0" + (day + i - getCurrentMonthLastDay()) + "日");
                            } else {
                                dateList.add("0" + (month + 1) + "月" + (day + i - getCurrentMonthLastDay()) + "日");
                            }
                        } else {
                            if (day + i - getCurrentMonthLastDay() < 10) {
                                dateList.add((month + 1) + "月0" + (day + i - getCurrentMonthLastDay()) + "日");
                            } else {
                                dateList.add((month + 1) + "月" + (day + i - getCurrentMonthLastDay()) + "日");
                            }
                        }
                    } else {
                        if (month < 10) {
                            dateList.add("0" + month + "月" + (day + i) + "日");
                        } else {
                            dateList.add(month + "月" + (day + i) + "日");
                        }
                    }

                }
            }
        }
        return dateList;
    }

    public ArrayList<String> set2Items() {
        ArrayList<String> hoursList = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            if (i < 5) {
                hoursList.add("凌晨0" + i + "时");
            } else if (i >= 5 && i < 11) {
                if (i < 10) {
                    hoursList.add("早上0" + i + "时");
                } else {
                    hoursList.add("早上" + i + "时");
                }
            } else if (i >= 11 && i < 15) {
                hoursList.add("中午" + i + "时");
            } else if (i >= 15 && i < 18) {
                hoursList.add("下午" + i + "时");
            } else if (i >= 18 && i <= 23) {
                hoursList.add("晚上" + i + "时");
            }
        }
        return hoursList;
    }

    public ArrayList<IPickerViewData> set3Items1() {
        ArrayList<IPickerViewData> minuteList = new ArrayList<>();
        for (int i = 0; i <= 55; i += 5) {
            if (i < 10) {
                minuteList.add(new PickerViewData("0" + i + "分"));
            } else {
                minuteList.add(new PickerViewData(i + "分"));
            }
        }
        return minuteList;
    }

    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    public void chooseTime(final Context context, final TextView mTvTime) {
        final Date currentDate = new Date();
        item1s = set1Items();//7天

        ArrayList<String> currentDayHours = new ArrayList<>();//当天
        int hours = currentDate.getHours();
        int min = currentDate.getMinutes();
        int a = hours;
        if (min > 55) {//当前分钟数没有可选项,跳过当前小时
            a = hours + 1;
        }
        Log.i("SelectDateTimeUtil", "-->" + hours + ":" + min);
        for (int i = a; i <= 23; i++) {
            if (i < 5) {
                currentDayHours.add("凌晨0" + i + "时");
            } else if (i >= 5 && i < 11) {
                if (i < 10) {
                    currentDayHours.add("早上0" + i + "时");
                } else {
                    currentDayHours.add("早上" + i + "时");
                }
            } else if (i >= 11 && i < 15) {
                currentDayHours.add("中午" + i + "时");
            } else if (i >= 15 && i < 18) {
                currentDayHours.add("下午" + i + "时");
            } else if (i >= 18 && i <= 23) {
                currentDayHours.add("晚上" + i + "时");
            }
        }
        item2s.add(currentDayHours);
        for (int i = 0; i <= 5; i++) {//剩余6天
            item2s.add(set2Items());
        }

        ArrayList<ArrayList<IPickerViewData>> currentDayHourMinutes = new ArrayList<>();//分钟

        if (currentDate.getMinutes() < 55) {//当前小时没有可选内容,就跳过当前小时
            ArrayList<IPickerViewData> currentHourMinutes = new ArrayList<>();//当前小时内
            int i1 = currentDate.getMinutes() / 5 + 1;

            if (currentDate.getMinutes() >= 50) {
                if (currentDate.getMinutes() < 55) {
                    currentHourMinutes.add(new PickerViewData("55分"));
                }
            } else {
                for (int i = i1 * 5 + 5; i <= 55; i += 5) {//5-10分钟后
                    if (i < 10) {
                        currentHourMinutes.add(new PickerViewData("0" + i + "分"));
                    } else {
                        currentHourMinutes.add(new PickerViewData(i + "分"));
                    }
                }
            }
            currentDayHourMinutes.add(currentHourMinutes);
        }


        for (int i = a/*hours*/; i <= 23; i++) {//从当前小时或者下一小时开始
            currentDayHourMinutes.add(set3Items1());
        }
        item3s.add(currentDayHourMinutes);

        for (int i = 1; i <= 6; i++) {
            ArrayList<ArrayList<IPickerViewData>> lists = new ArrayList<>();
            for (int j = 1; j <= 24; j++) {
                lists.add(set3Items1());
            }
            item3s.add(lists);
        }

        final OptionsPickerView pvOptions = new OptionsPickerView(context);
        pvOptions.setPicker(item1s, item2s, item3s, true);
        pvOptions.setTitle("选择时间");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                //返回的分别是三个级别的选中位置
                showChooseTime(context, mTvTime, options1, options2, options3);

            }
        });
        pvOptions.show();
    }

    private void showChooseTime(Context context, TextView mTvTime, int options1, int options2, int options3) {
        Date currentDate = new Date();
        int year = currentDate.getYear() + 1900;
        int month = currentDate.getMonth() + 1;
        int day = currentDate.getDate();
        String chooseDate = null;
        String chooseD = item1s.get(options1);
        String chooseHour = item2s.get(options1).get(options2);
        String chooseMinute = item3s.get(options1).get(options2).get(options3).getPickerViewText();
        if (chooseD.contains("今天")) {
            if (day < 10) {
                if (month < 10) {
                    chooseDate = year + "年0" + month + "月0" + day + "日 ";
                } else {
                    chooseDate = year + "年" + month + "月0" + day + "日 ";
                }
            } else {
                if (month < 10) {
                    chooseDate = year + "年0" + month + "月" + day + "日 ";
                } else {
                    chooseDate = year + "年" + month + "月" + day + "日 ";
                }
            }
        } else if (chooseD.contains("明天")) {
            if (day < 9) {
                if (month < 10) {
                    chooseDate = year + "年0" + month + "月0" + (day + 1) + "日 ";

                } else {
                    chooseDate = year + "年" + month + "月0" + (day + 1) + "日 ";
                }
            } else {//是否超出当前月天数
                if (getCurrentMonthLastDay() < day + 1) {
                    if (month < 9) {
                        if (day + 1 - getCurrentMonthLastDay() < 10) {
                            chooseDate = year + "年0" + (month + 1) + "月0" + (day + 1 - getCurrentMonthLastDay()) + "日 ";
                        } else {
                            chooseDate = year + "年0" + (month + 1) + "月" + (day + 1 - getCurrentMonthLastDay()) + "日 ";
                        }
                    } else {
                        if (day + 1 - getCurrentMonthLastDay() < 10) {
                            chooseDate = year + "年" + (month + 1) + "月0" + (day + 1 - getCurrentMonthLastDay()) + "日 ";
                        } else {
                            chooseDate = year + "年" + (month + 1) + "月" + (day + 1 - getCurrentMonthLastDay()) + "日 ";
                        }
                    }
                } else {
                    if (month < 10) {
                        chooseDate = year + "年0" + month + "月" + (day + 1) + "日 ";
                    } else {
                        chooseDate = year + "年" + month + "月" + (day + 1) + "日 ";
                    }
                }
            }
        } else if (chooseD.contains("后天")) {
            if (day < 8) {
                if (month < 10) {
                    chooseDate = year + "年0" + month + "月0" + (day + 1) + "日 ";
                } else {
                    chooseDate = year + "年" + month + "月0" + (day + 2) + "日 ";
                }
            } else {
                if (getCurrentMonthLastDay() < day + 2) {
                    if (month < 9) {
                        if (day + 2 - getCurrentMonthLastDay() < 10) {
                            chooseDate = year + "年0" + (month + 1) + "月0" + (day + 2 - getCurrentMonthLastDay()) + "日 ";
                        } else {
                            chooseDate = year + "年0" + (month + 1) + "月" + (day + 2 - getCurrentMonthLastDay()) + "日 ";
                        }
                    } else {
                        if (day + 2 - getCurrentMonthLastDay() < 10) {
                            chooseDate = year + "年" + (month + 1) + "月0" + (day + 2 - getCurrentMonthLastDay()) + "日 ";
                        } else {
                            chooseDate = year + "年" + (month + 1) + "月" + (day + 2 - getCurrentMonthLastDay()) + "日 ";
                        }
                    }
                } else {
                    if (month < 10) {
                        chooseDate = year + "年0" + month + "月" + (day + 2) + "日 ";
                    } else {
                        chooseDate = year + "年" + month + "月" + (day + 2) + "日 ";
                    }
                }
            }
        } else {
            chooseDate = year + "年" + chooseD + " ";
        }
        if (TextUtils.isEmpty(chooseMinute.trim())) {//未选中就选择十分钟之后
            chooseMinute = currentDate.getMinutes() + 10 + "";
        }
        String chooseTime = chooseHour.substring(2) + chooseMinute;
        Date choose = null;
        try {
            choose = DTUtil.formatStringToDate(chooseDate + chooseTime, "yyyy年MM月dd日 HH时mm分");

            long l = DTUtil.formatDateToTimeStamp(choose);
            long l1 = DTUtil.formatDateToTimeStamp(new Date());
            if (l < l1) {
                LTUtil.showShort(context, "选择的时间不能早于当前时间");
                return;
            }
            String timePicked = DTUtil.formatDateToString(choose,"yyyy-MM-dd HH:mm");//转换成 yyyy-MM-dd HH:mm
            if (mTvTime == null) {
            } else {
                mTvTime.setText(timePicked);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            item1s.clear();
            item2s.clear();
            item3s.clear();
        }

    }

    private static class PickerViewData implements IPickerViewData {

        private final String data;

        public PickerViewData(String data) {
            this.data = data;
        }

        @Override
        public String getPickerViewText() {
            return data;
        }
    }

}
