package com.schaffer.base.test;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.schaffer.base.R;

public class TestFingerPrintActivity extends AppCompatActivity {

    protected TextView mTvState;
    private FingerprintManager mManager;
    private FingerprintManagerCompat mManagerCompat;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.test_finger_print);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mManagerCompat = FingerprintManagerCompat.from(this);
            mManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);

            mTvState.setText("点击此处验证指纹");
        } else {
            mTvState.setText("当前版本号低于Android6.0");
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        mTvState.setText("解锁失败>>>" + msg.arg1 + "->" + msg.obj.toString());
                        break;
                    case 1:
                        mTvState.setText("指纹解锁帮助");
                        break;
                    case 2:
                        mTvState.setText("指纹解锁成功!");
                        break;
                    case 3:
                        mTvState.setText("指纹解锁失败...");
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }

            }
        };
    }


    public void onFingerPrint(View view) {
        if (mManagerCompat.isHardwareDetected()) {
            if (mManagerCompat.hasEnrolledFingerprints()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(this, "请按下指纹", Toast.LENGTH_SHORT).show();
                    mManager.authenticate(null, new CancellationSignal(), 0, mCallback, mHandler);
                    mTvState.setText("正在获取中....");
                }
            } else {
                mTvState.setText("当前设备没有指纹");
            }
        } else {
            mTvState.setText("不支持指纹解锁");
        }
    }

    private void initView() {
        mTvState = (TextView) findViewById(R.id.finger_print_tv_state);
        mTvState.setTextSize(30);
    }


    android.hardware.fingerprint.FingerprintManager.AuthenticationCallback mCallback = new android.hardware.fingerprint.FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            Message msg = Message.obtain();
            msg.arg1 = errMsgId;
            msg.obj = errString;
            msg.what = 0;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
            mHandler.sendEmptyMessage(1);
        }

//        @Override
//        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
//            super.onAuthenticationSucceeded(result);
//            mHandler.sendEmptyMessage(2);
//        }


        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            mHandler.sendEmptyMessage(2);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            mHandler.sendEmptyMessage(3);
        }
    };
}
