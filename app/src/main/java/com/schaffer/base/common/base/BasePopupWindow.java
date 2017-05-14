package com.schaffer.base.common.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.schaffer.base.R;

/**
 * Created by a7352 on 2017/5/13.
 */

public class BasePopupWindow extends PopupWindow {

	public Activity activity;
	public View mParentView;
	private String tag;

	public BasePopupWindow(Context context) {
		activity = (Activity) context;
		tag = getClass().getSimpleName();
//        mParentView = View.inflate(context, layoutId, null);
//        setContentView(mParentView);
//        initRootView(mParentView);
	}


	public void initPop() {
		setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.popup_anim);
	}

	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}
}
