package com.schaffer.base.common.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.schaffer.base.BuildConfig;
import com.schaffer.base.common.block.BlockError;
import com.schaffer.base.common.block.BlockLooper;
import com.schaffer.base.common.manager.ActivityController;
import com.schaffer.base.common.manager.ActivityManager;
import com.schaffer.base.common.utils.Utils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author SchafferWang
 */

public /*abstract*/ class BaseApplication extends Application {

    private static BaseApplication app;
    private ActivityManager mActivityManager;

    protected static class DefinedActivityLifeCycleCallback implements ActivityLifecycleCallbacks {


        @Override
        public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
            ActivityController.addActivity(activity);
//            BaseApplication.getRefWatcher(activity).watch(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            BlockLooper.getBlockLooper().start();
        }

        @Override
        public void onActivityResumed(Activity activity) {
            ActivityController.setCurrActivity(new WeakReference<Activity>(activity));
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            BlockLooper.getBlockLooper().stop();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ActivityController.removeActivity(activity);
        }
    }

//    private RefWatcher install;
//
//    public static RefWatcher getRefWatcher(Context context) {
//        BaseApplication application = (BaseApplication) context.getApplicationContext();
//        return application.install;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager = ActivityManager.getScreenManager();
        registerActivityLifecycleCallbacks(new DefinedActivityLifeCycleCallback());
        app = this;
        Utils.init(this);
        configBlock();
        libraryInit(app);//第三方
    }

    private void libraryInit(BaseApplication app) {
        //bugly
        Bugly.init(this, "69353c4a55", BuildConfig.DEBUG);//69353c4a55
        //leaks
//        install = LeakCanary.install(this);
        initRealm();
        initOkHttpUtils();
        initImagePicker();
        initOthersLibrary();
    }

    public static synchronized BaseApplication getInstance() {
        return app;
    }

    public ActivityManager getActivityManager() {
        return mActivityManager;
    }


//    protected void setJPushAlias(final String jPushAlias) {
//        final String TAG = "jpush";
//		JPushInterface.setAlias(this, jPushAlias, new TagAliasCallback() {
//
//			@Override
//			public void gotResult(int code, String arg1,
//								  Set<String> arg2) {
//				String logs;
//				switch (code) {
//					case 0:
//						logs = "Set tag and jPushAlias success";
//
//						// 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
//						break;
//					case 6002:
//						logs = "Failed to set jPushAlias and tags due to timeout. Try again after 60s.";
//						Log.i(TAG, logs);
//						// 延迟 60 秒来调用 Handler 设置别名
//						Message message = mHandler.obtainMessage();
//						message.what = 6002;
//						message.obj = jPushAlias;
//						mHandler.sendMessageDelayed(message, 60000);
//						break;
//					default:
//						logs = "Failed with errorCode = " + code;
//						Log.e(TAG, logs);
//				}
//			}
//		});
//    }


    /**
     * 捕获ANR事件和影响
     */
    private void configBlock() {
        BlockLooper.initialize(new BlockLooper.Builder(this)
                .setIgnoreDebugger(true)
                .setReportAllThreadInfo(true)
                .setSaveLog(true)
                .setOnBlockListener(new BlockLooper.OnBlockListener() {//回调在非UI线程
                    @Override
                    public void onBlock(BlockError blockError) {
                        blockError.printStackTrace();//把堆栈信息输出到控制台
                    }
                })
                .build());
    }


    public void initRealm() {
        //Realm的配置与使用    http://www.jianshu.com/p/28912c2f31db
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration
                .Builder()
                .name("realm")
                .deleteRealmIfMigrationNeeded().build());
    }

    /**
     * OkhttpUtils
     */
    private void initOkHttpUtils() {
//        https://github.com/hongyangAndroid/okhttputils
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(new LoggerInterceptor("xg"))
//                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
//                .readTimeout(10000L, TimeUnit.MILLISECONDS)
//                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
//                .build();
//        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * ImagePicker
     */
    private void initImagePicker() {
//        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
//        imagePicker.setShowCamera(false);  //显示拍照按钮
//        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
//        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
//        imagePicker.setSelectLimit(1);    //选中数量限制
//        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
//        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
//        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    /**
     * 其他三方工具
     */
    private void initOthersLibrary() {
////        //微信 注册
//        mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//        mWxApi.registerApp(Constants.APP_ID);
////        //极光推送
//        JPushInterface.setLatestNotificationNumber(this, 2);
//        JPushInterface.setDebugMode(BuildConfig.DEBUG);
//        JPushInterface.init(this);
//        registrationID = JPushInterface.getRegistrationID(this);
//        //友盟统计
//        MobclickAgent.setDebugMode(true);
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        //友盟分享
//        PlatformConfig.setQQZone("1106276710", "KEYMuxYzRSpy4xUIijI");
//        PlatformConfig.setSinaWeibo("2119529910", "e7c58b0788955685668dd7a4a6f2e3a5", "http://sns.whalecloud.com");
//        UMShareAPI.get(this);
//        //bugly,5966cf1dab
////        CrashReport.initCrashReport(this, "5966cf1dab", BuildConfig.DEBUG);
//        Bugly.init(this, "5966cf1dab", true);
//
//        install = LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        Beta.installTinker();
    }
}
