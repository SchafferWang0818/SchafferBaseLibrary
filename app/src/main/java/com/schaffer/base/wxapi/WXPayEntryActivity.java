package com.schaffer.base.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WXPayEntryActivity extends Activity/* implements IWXAPIEventHandler */{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(com.fentu.xigua.R.layout.pay_result);
//
//        MyApplication.getInstance().mWxApi.handleIntent(getIntent(), this);
    }
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
//		MyApplication.getInstance().mWxApi.handleIntent(intent, this);
	}

//	@Override
//	public void onReq(BaseReq baseReq) {
//
//	}
//
//	@Override
//	public void onResp(BaseResp baseResp) {
//		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			//支付
//			EventBus.getDefault().post(new WechatPayEvent(baseResp));
//			finish();
//		}
//	}

}