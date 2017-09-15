package com.schaffer.base.common.listener;

import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * 保留两位小数点的TextWatcher,用于金额处理
 */

public abstract class DecimalTextWatcher implements TextWatcher {

    private final EditText editText;

    public DecimalTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                editText.setText(s);
                editText.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            editText.setText(s);
            editText.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                editText.setText(s.subSequence(0, 1));
                editText.setSelection(1);
                return;
            }
        }
    }


    /**
     * 随时计算余额
     *
     * @param s              填写内容
     * @param balance        余额
     * @param feePercent     百分比 1% =0.01
     * @param minFee         最少抽成
     * @param feeShow        显示需要多少抽成
     * @param withdrawSubmit 是否允许提现或其他操作的按键集
     */
    public void calculateServiceFee(CharSequence s, float balance, float feePercent, float minFee, TextView feeShow, View... withdrawSubmit) {
        float fee = minFee;
        if (s == null || s.length() == 0) {
            fee = minFee;
            feeShow.setText(new StringBuffer().append("服务费").append(fee).append("元").toString());
            return;
        }
        if (String.valueOf(s).endsWith(".")) {
            return;
        }
        Float aFloat = Float.valueOf(String.valueOf(s));
        if (TextUtils.isEmpty(s) || aFloat <= minFee / feePercent) {
            fee = minFee;
        } else if (aFloat > minFee / feePercent) {
            fee = aFloat * feePercent;
            DecimalFormat df = new DecimalFormat("##0.00");
            fee = Float.valueOf(df.format(fee));
        }
        feeShow.setText(new StringBuffer().append("服务费").append(fee).append("元").toString());
        for (int i = 0; i < withdrawSubmit.length; i++) {
            withdrawSubmit[i].setEnabled(fee + aFloat <= balance);
        }
    }

}
