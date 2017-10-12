package com.schaffer.base.common.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.schaffer.base.R;
import com.schaffer.base.common.utils.LTUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndroidSchaffer on 2017/10/12.
 */

public abstract class BaseAppCompatActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {


    //    static {
//        //允许使用svg于background,必须依附于状态选择器等StateListDrawable,InsetDrawable,LayerDrawable,LevelListDrawable,RotateDrawable
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }
    protected boolean isFirstInit = true;
    protected BaseApplication application;
    protected CountDownTimer countDownTimer;
    protected String tag;
    protected Handler handler;
    protected boolean mActivityBeShown = false;
    protected P mPresenter;
    protected boolean eventbusEnable = false;//需要用户自己 onCreate()之前设定
    public static final int REQUEST_CODE_PERMISSIONS = 20;
    public static final int REQUEST_CODE_PERMISSION = 19;
    public static final String INTENT_DATA_IMG_PATHS = "img_paths";
    public static final String INTENT_DATA_IMG_RES = "img_resIds";
    public static final String INTENT_DATA_IMG_CURRENT_INDEX = "img_current";
    protected ProgressDialog progress;
    protected FrameLayout mFrameContent;

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
        throwable.printStackTrace();
    }

    public void showSnackbar(String content, int duration) {
        if (duration != Snackbar.LENGTH_SHORT && duration != Snackbar.LENGTH_LONG)
            return;
        Snackbar.make(mFrameContent, content, duration).show();
    }

    public ProgressDialog showProgress(String content, boolean touchOutside) {
        ProgressDialog loadingDialog = new ProgressDialog(this);    // 创建自定义样式dialog
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

    protected void inflateContent(@LayoutRes int resId) {
        inflateContent(resId, null);
    }

    protected void inflateContent(View inflateView) {
        inflateContent(inflateView, null);
    }

    protected void inflateContent(@LayoutRes int resId, FrameLayout.LayoutParams params) {
        inflateContent(View.inflate(this, resId, null), params);
    }

    protected void inflateContent(View inflateView, FrameLayout.LayoutParams params) {
        if (mFrameContent != null && inflateView != null) {
            mFrameContent.addView(inflateView, params == null ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) : params);
        }
    }


    /*-------------------------------------------------------基础生命周期函数如下-----------------------------------------------------------------------*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21
//            getWindow().setEnterTransition(new Fade());
//            getWindow().setExitTransition(new Fade());
//        }//新转场动画
        tag = getClass().getSimpleName();
        mFrameContent = (FrameLayout) findViewById(R.id.layout_group_content);
        inflateView();
        initEventBus();
        mPresenter = initPresenter();
        application = (BaseApplication) this.getApplication();
        application.getActivityManager().pushActivity(this);
    }

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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (mPresenter != null) {
            mPresenter.detach();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (permissionResultListener != null) permissionResultListener = null;
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
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
        if (getTitle() != null) {
            setActivityTitle(getTitle());
        }
        setLeftClick(null);
    }

    protected void setLeftClick(View.OnClickListener listener) {
        findViewById(R.id.layout_toolbar_iv_back).setOnClickListener(listener != null ? listener : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void setToolbar(int visibility) {
        setToolbar();
        findViewById(R.id.layout_toolbar_tb).setVisibility(visibility);
    }

    protected void setActivityTitle(CharSequence charSequence) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_title)).setText(charSequence);
    }

    /*---------------------------------------------------------------动态权限相关------------------------------------------------------------------------------------*/

    String[] dangerousPermissions = {
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
            new AlertDialog.Builder(this).setCancelable(false)
                    .setMessage(TextUtils.isEmpty(description) ? "为了能正常实现功能，我们将向您申请权限。" : description).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(BaseAppCompatActivity.this, permissions, REQUEST_CODE_PERMISSIONS);

                }
            }).create().show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                if (permissionResultListener != null)
                    permissionResultListener.onSinglePermissionGranted(permissions[0]);
            } else {
                new AlertDialog.Builder(this).setCancelable(false)
                        .setMessage(TextUtils.isEmpty(description) ? "为了能正常实现功能，我们将向您申请权限。" : description).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(BaseAppCompatActivity.this, permissions, REQUEST_CODE_PERMISSION);
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
        if (requestCode == REQUEST_CODE_PERMISSION) {//单个权限申请结果
            if (grantResults.length == 0) return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限申请成功
                if (permissionResultListener != null)
                    permissionResultListener.onSinglePermissionGranted(permissions[0]);
            } else {//权限申请失败
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {//已点不再询问
                    showSnackbar("权限已被禁止,并不再询问,请在设置中打开", Snackbar.LENGTH_LONG);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    interface PermissionResultListener {
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
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    /*----------------------------------------------------------倒计时如下--------------------------------------------------------------------------------*/

    protected void countDown(int second, final CountDownTimeListener listener) {
        if (second <= 0) return;
        countDownTimer = new CountDownTimer(second * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int secondUntilFinished = Math.round(millisUntilFinished / 1000);
                if (listener != null)
                    listener.onCountDownTick(secondUntilFinished);
            }

            @Override
            public void onFinish() {
                if (listener != null)
                    listener.onCountDownFinish();
            }
        };
        countDownTimer.start();
    }

    public interface CountDownTimeListener {
        /**
         * 倒计时秒的操作
         *
         * @param secondUntilFinished 剩余秒数
         */
        void onCountDownTick(int secondUntilFinished);

        /**
         * 倒计时结束时的操作
         */
        void onCountDownFinish();
    }


    /**
     * 栈内部是否只有当前一个Activity用于判断是否点击图标重新开始
     */
    public void onSplashCreateTaskRootJudgment() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }
    }

    /**
     * 设置EventBus可用,填充界面时使用
     *
     * @param enable
     */
    protected void setEventbusEnable(boolean enable) {
        eventbusEnable = enable;
    }


    /**
     * 获取一个 View 的缓存视图
     *
     * @param view
     * @return
     */
    protected Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }
    /*----------------------------------------------------------EditText Cursor 调整如下-----------------------------------------------------------------*/

    /**
     * EditText 光标在最后
     *
     * @param editText
     */
    protected void judgementCursor(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    /*----------------------------------------------------------抽象函数如下--------------------------------------------------------------------------------*/

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

    public void initView() {
    }


    /*-------------------------------------------------------------标题处理----------------------------------------------------------------------------------------*/

    protected void setLeftIcon(@DrawableRes int resId, View.OnClickListener listener) {
        ((ImageView) findViewById(R.id.layout_toolbar_iv_back)).setImageResource(resId);
        setLeftIconVisible(View.VISIBLE);
        setLeftClick(listener);
    }

    protected void setLeftIconVisible(int visible) {
        findViewById(R.id.layout_toolbar_iv_back).setVisibility(visible == View.VISIBLE ? View.VISIBLE : View.GONE);
        findViewById(R.id.layout_toolbar_tv_left).setVisibility(visible == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    protected void setLeftText(String content, View.OnClickListener onClickListener) {
        setLeftIconVisible(View.GONE);
        findViewById(R.id.layout_toolbar_tv_left).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.layout_toolbar_tv_left)).setText(content);
    }

    protected void setLeftText(int spValue, @ColorInt int color, String content, View.OnClickListener onClickListener) {
        setLeftText(content, onClickListener);
        setLeftTextColor(color);
        setLeftTextSize(spValue);
    }

    protected void setLeftTextSize(int spValue) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_left)).setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
    }

    protected void setLeftTextColor(@ColorInt int color) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_left)).setTextColor(color);
    }



    protected void setRightText(String content) {
        setRightText(content, View.VISIBLE, null);
    }

    protected void setRightText(String content, View.OnClickListener onClickListener) {
        setRightText(content, View.VISIBLE, onClickListener);
    }

    protected void setRightText(String content, int visibility, View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(content)) {
            ((TextView) findViewById(R.id.layout_toolbar_tv_right)).setText(content);
            findViewById(R.id.layout_toolbar_tv_right).setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            findViewById(R.id.layout_toolbar_iv_right).setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        if (onClickListener != null) {
            findViewById(R.id.layout_toolbar_tv_right).setOnClickListener(onClickListener);
        }
    }

    protected void setRightTextColor(@ColorInt int color) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_right)).setTextColor(color);
    }

    protected void setRightTextColor(String color) {
        if (!color.startsWith("#") && !(color.length() != 4 || color.length() != 5 || color.length() != 7 || color.length() != 9))
            return;
        setRightTextColor(Color.parseColor(color));
    }

    protected void setRightTextSize(int spValue) {
        ((TextView) findViewById(R.id.layout_toolbar_tv_right)).setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
    }

    protected void setRightIcon(@DrawableRes int resId) {
        setRightIcon(resId, View.VISIBLE);
    }

    protected void setRightIcon(@DrawableRes int resId, int visibility) {
        setRightIcon(resId, visibility, null);
    }

    protected void setRightIcon(@DrawableRes int resId, View.OnClickListener onClickListener) {
        setRightIcon(resId, View.VISIBLE, onClickListener);
    }

    protected void setRightIcon(@DrawableRes int resId, int visibility, View.OnClickListener onClickListener) {
        if (resId != 0) {
            if (onClickListener != null) {
                findViewById(R.id.layout_toolbar_iv_right).setOnClickListener(onClickListener);
            }
            findViewById(R.id.layout_toolbar_tv_right).setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
            findViewById(R.id.layout_toolbar_iv_right).setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);
            ((ImageView) findViewById(R.id.layout_toolbar_iv_right)).setImageResource(resId);
        }
    }

    protected void setToolbar(int visible, String title, boolean leftBack, boolean rightAllDismiss, boolean rightTextShow, String right, @DrawableRes int rightResId, View.OnClickListener rightClick) {
        setToolbar(visible);
        if (visible == View.GONE) return;
        setActivityTitle(title == null ? "" : title);
        if (leftBack) {
            setLeftClick(null);
        }
        if (rightAllDismiss) return;
        if (rightTextShow) {
            setRightText(right, rightClick);
        } else {
            setRightIcon(rightResId, rightClick);
        }
    }

}
