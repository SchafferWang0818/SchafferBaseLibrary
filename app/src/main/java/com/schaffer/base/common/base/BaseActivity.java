package com.schaffer.base.common.base;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.constants.DayNight;
import com.schaffer.base.common.utils.LTUtils;
import com.schaffer.base.helper.DayNightHelper;
import com.schaffer.base.widget.ProgressDialogs;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
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
    private CountDownTimer countDownTimer;
    protected List<TextView> textViews = new ArrayList<>();
    protected List<? extends ViewGroup> viewGroups = new ArrayList<>();
    protected List<RecyclerView> recyclerViews = new ArrayList<>();
    protected List<? extends AdapterView<?>> adapterViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21
//            getWindow().setEnterTransition(new Fade());
//            getWindow().setExitTransition(new Fade());
//        }//新转场动画
        mFrameContent = (FrameLayout) findViewById(R.id.layout_group_content);
        inflateView(textViews, viewGroups, recyclerViews, adapterViews);
//        setToolbar();
        initEventBus();
        mPresenter = initPresenter();
        tag = getClass().getSimpleName();
        mProgressDialogs = new ProgressDialogs(this);
        application = (BaseApplication) this.getApplication();
        application.getActivityManager().pushActivity(this);
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

    /**
     * 提醒继承者填充FrameLayout,可以使用{@link BaseActivity#inflateContent(int)}系列函数
     *
     * @param textViews     TextView 加入集合用于改变对应颜色
     * @param viewGroups    ViewGroup 加入集合用于改变对应颜色
     * @param recyclerViews recyclerView加入集合用于改变对应颜色
     * @param adapterViews  adapterView加入集合用于改变对应颜色
     */
    protected abstract void inflateView(List<TextView> textViews, List<? extends ViewGroup> viewGroups, List<RecyclerView> recyclerViews, List<? extends AdapterView<?>> adapterViews);

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
        if (duration != Snackbar.LENGTH_SHORT && duration != Snackbar.LENGTH_LONG)
            return;
        Snackbar.make(mFrameContent, content, duration).show();
    }


    /**
     * 倒计时
     *
     * @param second 倒计时时间总秒数
     */
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
    void requestPermission(String... permissions) {
        if (permissions.length > 1) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
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
            new AlertDialog.Builder(this).setCancelable(false)
                    .setMessage(TextUtils.isEmpty(description) ? "为了能正常实现功能，我们将向您申请权限。" : description).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    permissionResultListener = listener;
                    requestPermission(permissions);
                }
            }).create().show();
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

    public void setToolbar(int visibility) {
        if (findViewById(R.id.layout_toolbar_tb) == null) return;
        findViewById(R.id.layout_toolbar_tb).setVisibility(visibility);
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

    protected void setRightIcon(@DrawableRes int resId) {
        setRightIcon(resId, View.VISIBLE);
    }

    protected void setRightIcon(@DrawableRes int resId, int visibility) {
        setRightIcon(resId, visibility, null);
    }

    protected void setRightIcon(@DrawableRes int resId, int visibility, View.OnClickListener onClickListener) {
        if (resId != 0) {
            if (onClickListener != null) {
                findViewById(R.id.layout_toolbar_iv_right).setOnClickListener(onClickListener);
            }
            findViewById(R.id.layout_toolbar_iv_right).setVisibility(visibility);
            ((ImageView) findViewById(R.id.layout_toolbar_iv_right)).setImageResource(resId);
        }
    }

    protected void setRightText(String content) {
        setRightText(content, View.VISIBLE);
    }

    protected void setRightText(String content, int visibility) {
        setRightText(content, visibility, null);
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

    /**
     * 需要有切换主题需求时使用
     */
    private void initTheme() {
        DayNightHelper helper = new DayNightHelper(this);
        if (helper.isDay()) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme_Night);
        }
    }

    /**
     * 切换主题设置
     */
    private void toggleThemeSetting() {
        DayNightHelper helper = new DayNightHelper(this);
        if (helper.isDay()) {
            helper.setMode(DayNight.NIGHT);
            setTheme(R.style.AppTheme_Night);
        } else {
            helper.setMode(DayNight.DAY);
            setTheme(R.style.AppTheme);
        }
    }

    private void RefreshUIForChangeTheme() {
        TypedValue background = new TypedValue();//背景色
        TypedValue textColor = new TypedValue();//字体颜色
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.clock_background, background, true);
        theme.resolveAttribute(R.attr.clock_textColor, textColor, true);
        if (textViews.size() > 0) {
            for (TextView textView : textViews) {
                textView.setBackgroundResource(background.resourceId);
                textView.setTextColor(textColor.resourceId);
            }
        }

        if (viewGroups.size() > 0) {
            for (ViewGroup viewGroup : viewGroups) {
                viewGroup.setBackgroundResource(background.resourceId);
            }
        }

        if (adapterViews.size() > 0) {
            //todo adapterViews
        }

        if (recyclerViews.size() > 0) {
            //todo recyclerViews
        }
        //RecyclerView 解决方案

//        int childCount = mRecyclerView.getChildCount();
//        for (int childIndex = 0; childIndex < childCount; childIndex++) {
//            ViewGroup childView = (ViewGroup) mRecyclerView.getChildAt(childIndex);
//            childView.setBackgroundResource(background.resourceId);
//            View infoLayout = childView.findViewById(R.id.info_layout);
//            infoLayout.setBackgroundResource(background.resourceId);
//            TextView nickName = (TextView) childView.findViewById(R.id.tv_nickname);
//            nickName.setBackgroundResource(background.resourceId);
//            nickName.setTextColor(resources.getColor(textColor.resourceId));
//            TextView motto = (TextView) childView.findViewById(R.id.tv_motto);
//            motto.setBackgroundResource(background.resourceId);
//            motto.setTextColor(resources.getColor(textColor.resourceId));
//        }
//
//        //让 RecyclerView 缓存在 Pool 中的 Item 失效
//        //那么，如果是ListView，要怎么做呢？这里的思路是通过反射拿到 AbsListView 类中的 RecycleBin 对象，然后同样再用反射去调用 clear 方法
//        Class<RecyclerView> recyclerViewClass = RecyclerView.class;
//        try {
//            Field declaredField = recyclerViewClass.getDeclaredField("mRecycler");
//            declaredField.setAccessible(true);
//            Method declaredMethod = Class.forName(RecyclerView.Recycler.class.getName()).getDeclaredMethod("clear", (Class<?>[]) new Class[0]);
//            declaredMethod.setAccessible(true);
//            declaredMethod.invoke(declaredField.get(mRecyclerView), new Object[0]);
//            RecyclerView.RecycledViewPool recycledViewPool = mRecyclerView.getRecycledViewPool();
//            recycledViewPool.clear();
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        refreshStatusBar();
    }

    /**
     * 刷新 StatusBar
     */
    private void refreshStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            getWindow().setStatusBarColor(getResources().getColor(typedValue.resourceId));
        }
    }

    /**
     * 展示一个切换动画
     */
    private void showAnimation() {
        final View decorView = getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorView);
        if (decorView instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(this);
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorView).addView(view, layoutParam);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorView).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }

    /**
     * 获取一个 View 的缓存视图
     *
     * @param view
     * @return
     */
    private Bitmap getCacheBitmapFromView(View view) {
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

    public void changeTheme() {
        showAnimation();
        toggleThemeSetting();
        RefreshUIForChangeTheme();
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
     * 设置EventBus可用
     *
     * @param enable
     */
    protected void setEventbusEnable(boolean enable) {
        eventbusEnable = enable;
        initEventBus();
    }

    public static boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
    }
}

