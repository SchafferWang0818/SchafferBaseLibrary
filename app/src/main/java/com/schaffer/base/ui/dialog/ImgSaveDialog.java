package com.schaffer.base.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseDialog;

/**
 * Created by a7352 on 2017/7/19.
 */

public class ImgSaveDialog extends BaseDialog implements View.OnClickListener {


    protected View rootView;
    protected TextView mTvSave;
    protected TextView mTvCancel;

    public ImgSaveDialog(Context context) {
        super(context);
        view = View.inflate(context, R.layout.dialog_img_save_menu, null);
        setLayoutConfig(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true, true);
        initView(view);
    }

    public void initView(View view) {
        mTvSave = (TextView) view.findViewById(R.id.dialog_img_menu_tv_save);
        mTvSave.setOnClickListener(ImgSaveDialog.this);
        mTvCancel = (TextView) view.findViewById(R.id.dialog_img_menu_tv_cancel);
        mTvCancel.setOnClickListener(ImgSaveDialog.this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_img_menu_tv_save) {
            onPicSave();
        } else if (view.getId() == R.id.dialog_img_menu_tv_cancel) {
            onCancel();
        }
    }

    protected void onCancel() {

    }

    protected void onPicSave() {
        dismiss();
    }

}
