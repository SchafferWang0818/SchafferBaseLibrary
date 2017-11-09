package com.schaffer.base.common.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.schaffer.base.R;
import com.schaffer.base.common.utils.LTUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a7352 on 2017/5/13.
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {

    private String tag;
    protected Activity activity;
    private View mRootView;
    private boolean mIsFirst = true;
    protected P mPresenter;
    private ProgressDialog progress;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        tag = getClass().getSimpleName();
        this.activity = activity;
        mPresenter = initPresenter();
    }

    protected abstract P initPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
            return mRootView;
        } else {
            mRootView = initView(inflater, container);
        }
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && mIsFirst) {
//            initData();
//            mIsFirst = false;
//            return;
//        }
//        if (isVisibleToUser) {
//            refreshData();
//        }
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initData();

    protected abstract void refreshData();

    @Override
    public void onResume() {
        if (mPresenter != null) {
            mPresenter.attach((V) this);
        }
        if (mIsFirst) {
            mIsFirst = false;
            initData();
        } else {
            refreshData();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        super.onDestroyView();
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
        LTUtils.showToastShort(activity, msg);
    }

    @Override
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showLoading(String text) {
        progress = showProgress(text, false);
    }

    @Override
    public void showLoading() {
        showLoading("");
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
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

    public void callPhone(final String telephone) {
        if (TextUtils.isEmpty(telephone)) return;
        StringBuffer sb = new StringBuffer().append(getString(R.string.call));
        new AlertDialog.Builder(activity).setMessage(sb.toString())
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intent);
                    }
                }).setNegativeButton(getString(R.string.cancel), null).create().show();
    }


    public static final int REQUEST_CODE_PERMISSIONS = 20;
    public static final int REQUEST_CODE_PERMISSION = 19;

    protected String[] dangerousPermissions = {
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

    void requestPermission(String description, final String... permissions) {
        if (permissions.length > 1) {
            new AlertDialog.Builder(activity).setCancelable(false)
                    .setMessage(TextUtils.isEmpty(description) ? "为了能正常实现功能，我们将向您申请权限。" : description).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSIONS);

                }
            }).create().show();
        } else {
            if (ActivityCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                if (permissionResultListener != null)
                    permissionResultListener.onSinglePermissionGranted(permissions[0]);
            } else {
                new AlertDialog.Builder(activity).setCancelable(false)
                        .setMessage(TextUtils.isEmpty(description) ? "为了能正常实现功能，我们将向您申请权限。" : description).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSION);
                    }
                }).create().show();
            }
        }
    }

    /**
     * 请求运行时权限
     *
     * @param listener
     * @param permissions
     */
    protected void requestPermission(PermissionResultListener listener, String... permissions) {
        requestPermission(null, listener, permissions);
    }

    /**
     * 请求运行时权限
     *
     * @param listener
     * @param permissions
     */
    protected void requestPermission(String description, final PermissionResultListener listener, final String... permissions) {
        if (Build.VERSION.SDK_INT > 23) {
            permissionResultListener = listener;
            requestPermission(description, permissions);
        } else {
            if (listener != null) {
                listener.onSinglePermissionGranted(permissions[0]);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            //单个权限申请结果
            if (grantResults.length == 0) return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限申请成功
                if (permissionResultListener != null)
                    permissionResultListener.onSinglePermissionGranted(permissions[0]);
            } else {//权限申请失败
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                    //已点不再询问
                    showToast("权限已被禁止,并不再询问,请在设置中打开");
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("是否打开应用设置页面?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAppDetailSettingIntent();
                        }
                    }).create().show();
                } else {//再次询问?
                    if (permissionResultListener != null)
                        permissionResultListener.onSinglePermissionDenied(permissions[0]);
                }
            }
        }
        if (requestCode == REQUEST_CODE_PERMISSIONS) {//多个权限申请结果
            if (grantResults.length == 0) return;
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (deniedPermissions.size() != 0) {//有权限未允许
                if (permissionResultListener != null)
                    permissionResultListener.onPermissionsDenied(deniedPermissions);
            } else {//权限已被完全允许
                if (permissionResultListener != null)
                    permissionResultListener.onPermissionsGrantedAll();
            }
        }
    }

    PermissionResultListener permissionResultListener;

    public interface PermissionResultListener {
        void onSinglePermissionDenied(String permission);

        void onSinglePermissionGranted(String permission);

        void onPermissionsGrantedAll();

        void onPermissionsDenied(List<String> deniedPermissions);

    }

    /**
     * 打开应用设置界面
     */
    private void getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        startActivity(localIntent);
    }


    public void showSnackbar(String content, int duration) {
        if (duration != Snackbar.LENGTH_SHORT && duration != Snackbar.LENGTH_LONG) {
            return;
        }
        Snackbar.make(activity.getWindow().getDecorView().getRootView(), content, duration).show();
    }

    public ProgressDialog showProgress(String content, boolean touchOutside) {
        ProgressDialog loadingDialog = new ProgressDialog(activity);    // 创建自定义样式dialog
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(touchOutside);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setMessage(content);
        loadingDialog.show();
        return loadingDialog;
    }

    public void dismissProgress() {
        if (progress != null && progress.isShowing()) {
            progress.cancel();
            progress = null;
        }
    }
}
