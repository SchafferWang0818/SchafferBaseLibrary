package com.schaffer.base.wxapi;

import android.view.View;
import android.view.WindowManager;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseActivity;
import com.schaffer.base.presenter.WXEntryPresenter;

/**
 * Created by AndroidSchaffer on 2017/8/30.
 */

public class WXEntryActivity extends BaseActivity<WXEntryActivity, WXEntryPresenter> /*implements IWXAPIEventHandler */{
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void inflateView() {
        setToolbar(View.GONE);
        inflateContent(R.layout.layout_transparent);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.dimAmount = 0.8f;
        getWindow().setAttributes(attributes);
    }

    @Override
    protected WXEntryPresenter initPresenter() {
        if (mPresenter == null) {
            return new WXEntryPresenter();
        } else {
            return mPresenter;
        }
    }

    @Override
    protected void initData() {
//        MyApplication.getInstance().mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void refreshData() {

    }

//    @Override
//    public void onReq(BaseReq baseReq) {
//
//    }
//
//    @Override
//    public void onResp(BaseResp baseResp) {
//        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) { //登录授权
//            switch (baseResp.errCode) {
//                case BaseResp.ErrCode.ERR_OK://同意授权
//
//                    final String code = ((SendAuth.Resp) baseResp).code;
//                    if (!TextUtils.isEmpty(code)) {
//                        if (((SendAuth.Resp) baseResp).state.equals("xigua_login")) {
////                            showToast("登录成功");
//                            if (mPresenter == null) {
//                                mPresenter = new WXEntryPresenter();
//                            }
//                            mPresenter.sendWechatCallback(code,MyApplication.getInstance().getRegistrationID());
//                        } else if (((SendAuth.Resp) baseResp).state.equals("wechat_withdraw")) {
//                            EventBus.getDefault().post(new WechatWithdrawEvent(((SendAuth.Resp) baseResp).code));
//                            finish();
//                        }
//                    }
//                    break;
//
//                case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                    showToast("用户拒绝授权");
//                    finish();
//                    break;
//                case BaseResp.ErrCode.ERR_USER_CANCEL:
//                    showToast("用户取消授权");
//                    finish();
//                    break;
//                default:
//                    break;
//            }
//        } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//分享
//            switch (baseResp.errCode) {
//                case BaseResp.ErrCode.ERR_OK:
//                    showToast("分享成功");
//                    break;
//                case BaseResp.ErrCode.ERR_USER_CANCEL:
//                    showToast("取消分享");
//                    break;
//                case BaseResp.ErrCode.ERR_SENT_FAILED:
//                    showToast("分享失败");
//                    break;
//            }
//            finish();
//        }
//    }

}
