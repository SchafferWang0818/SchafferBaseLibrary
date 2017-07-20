package com.schaffer.base.common.base;

import android.Manifest;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.utils.LTUtils;
import com.schaffer.base.widget.ProgressDialogs;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * <pre>
 *     Created by Schaffer on 2017/5/13.
 * </pre>
 */

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {

    private String tag;
    private ProgressDialogs mProgressDialogs;
    protected Handler handler;
    protected boolean mActivityBeShown = false;
    private boolean isFirstInit = true;
    protected P mPresenter;
    private BaseApplication application;
    private FrameLayout mFrameContent;
    protected boolean eventbusEnable = false;//需要用户自己 onCreate()之前设定
    private static final int REQUEST_CODE_PERMISSIONS = 20;
    private static final int REQUEST_CODE_PERMISSION = 19;
    public static final String INTENT_DATA_IMG_PATHS = "img_paths";
    public static final String INTENT_DATA_IMG_RES = "img_resIds";
    public static final String INTENT_DATA_IMG_CURRENT_INDEX = "img_current";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mFrameContent = (FrameLayout) findViewById(R.id.layout_group_content);
        inflateView();
        setToolbar();
        initEventBus();
        mPresenter = initPresenter();
        tag = getClass().getSimpleName();
        mProgressDialogs = new ProgressDialogs(this);
        application = (BaseApplication) this.getApplication();
        application.getActivityManager().pushActivity(this);
    }

    protected void inflateContent(int resId) {
        inflateContent(resId, null);
    }

    protected void inflateContent(View inflateView) {
        inflateContent(inflateView, null);
    }

    protected void inflateContent(int resId, FrameLayout.LayoutParams params) {
        inflateContent(View.inflate(this, resId, null), params);
    }

    protected void inflateContent(View inflateView, FrameLayout.LayoutParams params) {
        if (mFrameContent != null && inflateView != null) {
            mFrameContent.addView(inflateView, params == null ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) : params);
        }
    }

    /**
     * 提醒继承者填充FrameLayout,可以使用{@link BaseActivity#inflateContent(int)}系列函数
     */
    protected abstract void inflateView();

    /**
     * 提醒初始化Presenter
     *
     * @return MVP中的Presenter
     */
    protected abstract P initPresenter();

    /**
     * 进入界面时填充数据
     */
    protected abstract void initData();

    /**
     * 刷新数据,定义为空则不刷新
     */
    protected abstract void refreshData();

    @Override
    protected void onResume() {
        super.onResume();
        mActivityBeShown = true;
        if (mPresenter != null) {
            mPresenter.attach((V) this);
        }
        if (isFirstInit) {//第一次初始化
            isFirstInit = false;
            initData();
        } else {
            refreshData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivityBeShown = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        initEventBus();
        if (mPresenter != null) {
            mPresenter.detach();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        application.getActivityManager().popActivity(this);
    }

    private void initEventBus() {
        if (!eventbusEnable) return;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        } else {
            EventBus.clearCaches();
            EventBus.getDefault().unregister(this);
        }

    }

//	public void onActivityBack(View v) {
//		finish();
//	}
//
//	@Override
//	public void finish() {
//		super.finish();
//		overridePendingTransition(R.anim.anim_exit_in, R.anim.anim_exit_out);
//	}
//
//	@Override
//	public void startActivity(Intent intent) {
//		super.startActivity(intent);
//		overridePendingTransition(R.anim.anim_enter_in, R.anim.anim_enter_out);
//	}
//
//	@Override
//	public void startActivityForResult(Intent intent, int requestCode) {
//		super.startActivityForResult(intent, requestCode);
//		overridePendingTransition(R.anim.anim_enter_in, R.anim.anim_enter_out);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode != RESULT_OK) return;
//
//	}

    protected void toActivity(Class toClass) {
        toActivity(toClass, -1);
    }

    protected void toActivity(Class toClass, int requestCode) {
        Intent intent = new Intent(this, toClass);
        if (requestCode != -1) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
    }

    public void callPhone(final String telephone) {
        if (TextUtils.isEmpty(telephone)) return;
        StringBuffer sb = new StringBuffer().append(getString(R.string.call)).append(telephone);
        new AlertDialog.Builder(this).setMessage(sb.toString())
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
                        if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        BaseActivity.this.startActivity(intent);
                    }
                }).setNegativeButton(getString(R.string.cancel), null).create().show();
    }

    @Override
    public void showLog(String msg) {
        LTUtils.w(tag, msg);
    }

    @Override
    public void showLog(int resId) {
        showLog(getString(resId));
    }

    @Override
    public void showToast(String msg) {
        showLog(msg);
        LTUtils.showToastShort(this, msg);
    }

    @Override
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showLoading(String text) {
        if (mProgressDialogs != null) {
            mProgressDialogs.showDialog(text);
        }
    }

    @Override
    public void showLoading() {
        showLoading("");
    }

    @Override
    public void dismissLoading() {
        if (mProgressDialogs != null) {
            mProgressDialogs.closeDialog();
        }
    }

    @Override
    public void onSucceed() {
        dismissLoading();
    }

    @Override
    public void onFailed() {
        dismissLoading();

    }

    @Override
    public void onFailed(Throwable throwable) {
        dismissLoading();
        showLog(throwable.getMessage() + throwable.getCause());
    }

    public void showSnackbar(String content, int duration) {
        if (duration != Snackbar.LENGTH_SHORT || duration != Snackbar.LENGTH_LONG)
            return;
        Snackbar.make(null, content, duration).show();
    }


    /**
     * 倒计时
     *
     * @param second 倒计时时间总秒数
     */
    protected void countDown(int second) {
        if (second <= 0) return;
        new CountDownTimer(second * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int secondUntilFinished = Math.round(millisUntilFinished / 1000);
                onCountDownTick(secondUntilFinished);
            }

            @Override
            public void onFinish() {
                onCountDownFinish();
            }
        }.start();
    }

    /**
     * 倒计时秒的处理
     *
     * @param secondUntilFinished 剩余秒数
     */
    protected void onCountDownTick(int secondUntilFinished) {

    }

    protected void onCountDownFinish() {

    }

    private void clearMemory() {
        ActivityManager activityManger = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = activityManger.getRunningAppProcesses();
        if (appList != null) {
            for (int i = 0; i < appList.size(); i++) {
                ActivityManager.RunningAppProcessInfo appInfo = appList.get(i);
                String[] pkgList = appInfo.pkgList;
                if (appInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    for (int j = 0; j < pkgList.length; j++) {
                        activityManger.killBackgroundProcesses(pkgList[j]);
                    }
                }
            }
        }
    }

    /**
     * 更改UI透明和是否可以截图选项
     *
     * @param showStatus     状态栏是否显示
     * @param showNavigation 虚拟菜单栏是否显示
     * @param couldCapture   是否可以截图
     */
    protected void setTranslucentSystemUI(boolean showStatus, boolean showNavigation, boolean couldCapture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if (!showStatus) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            if (!showNavigation) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            if (!couldCapture) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        }
    }

    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE//读写权限0
            , Manifest.permission.CAMERA//相机权限1
            , Manifest.permission.WRITE_CONTACTS//写入联系人2
            , Manifest.permission.CALL_PHONE//打电话3
            , Manifest.permission.SEND_SMS//发送短信4
            , Manifest.permission.RECORD_AUDIO//麦克风打开5
            , Manifest.permission.ACCESS_FINE_LOCATION//定位相关6
            , Manifest.permission.BODY_SENSORS//传感器 7  最小使用SDK 20
            , Manifest.permission.WRITE_CALENDAR//日历写入8
    };

    protected void requestPermission(String... permissions) {
        if (permissions.length > 1) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {//单个权限申请结果
            if (grantResults.length == 0) return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限申请成功
                onRequestPermissionSuccess(permissions, grantResults);
            } else {//权限申请失败
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {//已点不再询问
                    showSnackbar("权限已被禁止,并不再询问,请在设置中打开", Snackbar.LENGTH_LONG);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("是否打开应用设置页面?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAppDetailSettingIntent();
                        }
                    }).setNegativeButton("取消", null).create().show();
                } else {//再次询问?
                    onRequestPermissionFailed(permissions, grantResults);
                }
            }
        }
        if (requestCode == REQUEST_CODE_PERMISSIONS) {//多个权限申请结果
            if (grantResults.length == 0) return;


        }
    }


    protected void onRequestPermissionFailed(String[] permissions, int[] grantResults) {

    }

    protected void onRequestPermissionSuccess(String[] permissions, int[] grantResults) {

    }

    /**
     * 打开应用设置界面
     */
    private void getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    public void setToolbar() {
        if (findViewById(R.id.layout_toolbar_tb) == null) return;
        if (this instanceof AppCompatActivity) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.layout_toolbar_tb);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.widget.Toolbar toolbar = (android.widget.Toolbar) findViewById(R.id.layout_toolbar_tb);
                setActionBar(toolbar);
                getActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        setActivityTitle(getTitle());
        findViewById(R.id.layout_toolbar_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setActivityTitle(CharSequence charSequence) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_title)).setText(charSequence);
    }

}
