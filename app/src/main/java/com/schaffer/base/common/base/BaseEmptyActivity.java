package com.schaffer.base.common.base;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.schaffer.base.R;
import com.schaffer.base.common.constants.Constants;
import com.schaffer.base.common.utils.LTUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Schaffer
 * @date 2017/10/12
 */

public abstract class BaseEmptyActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {


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
    private int mainThemeColor = Color.BLACK;
    /**
     * 需要用户自己 onCreate()之前设定
     */
    protected boolean eventbusEnable = false;
    public static final int REQUEST_CODE_PERMISSIONS = 20;
    public static final int REQUEST_CODE_PERMISSION = 19;
    public static final String INTENT_DATA_IMG_PATHS = "img_paths";
    public static final String INTENT_DATA_IMG_RES = "img_resIds";
    public static final String INTENT_DATA_IMG_CURRENT_INDEX = "img_current";
    protected ProgressDialog progress;
    protected Window window;
    protected FrameLayout mFrameContent;

    public void setMainThemeColor(int mainThemeColor) {
        this.mainThemeColor = mainThemeColor;
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
        if (duration != Snackbar.LENGTH_SHORT && duration != Snackbar.LENGTH_LONG) {
            return;
        }
        Snackbar snackbar = Snackbar.make(window.getDecorView().getRootView(), content, duration);
        snackbar.getView().setBackgroundColor(mainThemeColor);
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showSnackbar(String content) {
        showSnackbar(content, Snackbar.LENGTH_SHORT);
    }

    public void showSnackbar(View view, String content, String action, int clickColor, View.OnClickListener listener) {
        final Snackbar snackbar = Snackbar.make(view == null ? window.getDecorView().getRootView() : view, content, Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(mainThemeColor);
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.setActionTextColor(clickColor);
        snackbar.setAction(action, listener != null ? listener : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void showSnackbar(String content, String action, int clickColor, View.OnClickListener listener) {
        showSnackbar(null, content, action, clickColor, listener);
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

    /**
     * -------------------------------------------------------基础生命周期函数如下-----------------------------------------------------------------------
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21
//            getWindow().setEnterTransition(new Fade());
//            getWindow().setExitTransition(new Fade());
//        }//新转场动画
//        onCreateInit((Object) this.getClass().getSuperclass() instanceof BaseAppCompatActivity);
        onCreateInit(this.getClass().isAssignableFrom(BaseEmptyActivity.class));
        setCurrentTransition(getIntent().getIntExtra(Constants.WINDOW_TRANSITION, -1));
    }

    private void onCreateInit(boolean useBase) {
        if (useBase) {
            setContentView(R.layout.activity_base);
            mFrameContent = (FrameLayout) findViewById(R.id.layout_group_content);
        }
        inflateView();
        initEventBus();
        window = getWindow();
        tag = getClass().getSimpleName();
        application = (BaseApplication) this.getApplication();
        application.getActivityManager().pushActivity(this);
        mPresenter = initPresenter();
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

    /**
     * 设置EventBus可用,填充界面时使用
     *
     * @param enable
     */
    protected void setEventbusEnable(boolean enable) {
        eventbusEnable = enable;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    /*---------------------------------------------------------------动态权限相关------------------------------------------------------------------------------------*/

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

    protected void requestPermission(String description, final String... permissions) {
        if (permissions.length > 1) {
            new AlertDialog.Builder(this).setCancelable(false)
                    .setMessage(TextUtils.isEmpty(description) ? "为了能正常实现功能，我们将向您申请权限。" : description).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(BaseEmptyActivity.this, permissions, REQUEST_CODE_PERMISSIONS);

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
                        ActivityCompat.requestPermissions(BaseEmptyActivity.this, permissions, REQUEST_CODE_PERMISSION);
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

    protected PermissionResultListener permissionResultListener;

    public interface PermissionResultListener {
        void onSinglePermissionDenied(String permission);

        void onSinglePermissionGranted(String permission);

        void onPermissionsGrantedAll();

        void onPermissionsDenied(List<String> deniedPermissions);

    }

    /**
     * 打开应用设置界面
     */
    protected void getAppDetailSettingIntent() {
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
    /**----------------------------------------------------------EditText Cursor 调整如下-----------------------------------------------------------------*/

    /**
     * EditText 光标在最后
     *
     * @param editText
     */
    protected void judgementCursor(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    /**----------------------------------------------------------抽象函数如下--------------------------------------------------------------------------------*/

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

    /*动画 <!--transition 动画专用-->*/
    public static final int TRANSITION_EXPLODE = 1;
    public static final int TRANSITION_SLIDE = 2;
    public static final int TRANSITION_FADE = 3;

    public void setCurrentTransition(int transition) {
        if (transition < 0) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            switch (transition) {
                case TRANSITION_EXPLODE:
                default:
                    Explode explode = new Explode();
                    explode.setDuration(800);
                    window.setEnterTransition(explode);
                    break;
                case TRANSITION_SLIDE:
                    Slide slide = new Slide();
                    slide.setDuration(800);
                    window.setEnterTransition(slide);
                    break;
                case TRANSITION_FADE:
                    Fade fade = new Fade();
                    fade.setDuration(800);
                    window.setEnterTransition(fade);
                    break;
            }
        }
    }


    @Override
    public void startActivity(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            super.startActivity(intent);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }
}
