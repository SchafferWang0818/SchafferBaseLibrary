package com.schaffer.base.common.base;


import android.Manifest;
import android.animation.Animator;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;

import com.schaffer.base.R;

import java.util.List;

/**
 * <pre>
 *
 * @author Schaffer
 * @date 2017/5/13
 * </pre>
 */

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseAppCompatActivity<V, P> {


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
/*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@Override
public void startActivity(Intent intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this)
                .toBundle());
    }else{
        super.startActivity(intent);
    }
//    overridePendingTransition(R.anim.anim_enter_in, R.anim.anim_enter_out);
}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.startActivityForResult(intent,requestCode, ActivityOptions.makeSceneTransitionAnimation(this)
                    .toBundle());
        }else{
            super.startActivityForResult(intent,requestCode);
        }
//        overridePendingTransition(R.anim.anim_enter_in, R.anim.anim_enter_out);
    }*/

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


    /**
     * 5.0揭露动画
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius, int seconds) {
        Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        anim.setDuration(seconds * 1000);
        anim.start();
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
}

