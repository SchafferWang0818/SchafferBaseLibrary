package com.schaffer.base.common.base;

import android.Manifest;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
 *
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
    protected boolean eventbusEnable = false;//需要用户自己initEventBus()之前设定

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
        if (mFrameContent != null) {
            mFrameContent.addView(View.inflate(this, resId, null), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 提醒继承者填充FrameLayout
     */
    protected abstract void inflateView();

    /**
     * 提醒初始化Presenter
     * @return  MVP中的Presenter
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
//		overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
//	}
//
//	@Override
//	public void startActivity(Intent intent) {
//		super.startActivity(intent);
//		overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
//	}
//
//	@Override
//	public void startActivityForResult(Intent intent, int requestCode) {
//		super.startActivityForResult(intent, requestCode);
//		overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
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
        ((TextView) findViewById(R.id.layout_toolbar_tv_title)).setText(getTitle());
        findViewById(R.id.layout_toolbar_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
