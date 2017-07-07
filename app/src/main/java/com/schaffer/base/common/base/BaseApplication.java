package com.schaffer.base.common.base;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.block.BlockError;
import com.schaffer.base.common.block.BlockLooper;
import com.schaffer.base.common.listener.OnLowMemoryListener;
import com.schaffer.base.common.manager.ActivityController;
import com.schaffer.base.common.manager.ActivityManager;
import com.schaffer.base.common.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author SchafferWang
 */

public /*abstract*/ class BaseApplication extends Application {


    private static BaseApplication app;
    private ArrayList<WeakReference<OnLowMemoryListener>> mLowMemoryListeners;
    private ActivityManager mActivityManager;

    protected static class DefinedActivityLifeCycleCallback implements ActivityLifecycleCallbacks {


        @Override
        public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
            ActivityController.addActivity(activity);
//            setToolbar(activity);
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

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Utils.init(this);
        configBlock();
        mActivityManager = ActivityManager.getScreenManager();
        mLowMemoryListeners = new ArrayList<WeakReference<OnLowMemoryListener>>();
        registerActivityLifecycleCallbacks(new DefinedActivityLifeCycleCallback());
        libraryInit(app);//第三方
    }

    private void libraryInit(BaseApplication app) {

    }

//    protected abstract void libraryInit(BaseApplication app);

    public static synchronized BaseApplication getInstance() {
        return app;
    }

    public ActivityManager getActivityManager() {
        return mActivityManager;
    }

    // 内存空间过低的时候，被系统调用
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        int i = 0;
        while (i < mLowMemoryListeners.size()) {
            final OnLowMemoryListener l = mLowMemoryListeners.get(i).get();
            if (l == null) {
                mLowMemoryListeners.remove(i);
            } else {
                l.onLowMemoryReceived();
                i++;
            }
        }
    }

    public void registerOnLowMemoryListener(OnLowMemoryListener listener) {
        if (listener != null) {
            mLowMemoryListeners
                    .add(new WeakReference<OnLowMemoryListener>(listener));
        }
    }

    // 移除监听
    public void unregisterOnLowMemoryListener(OnLowMemoryListener listener) {
        if (listener != null) {
            int i = 0;
            while (i < mLowMemoryListeners.size()) {
                final OnLowMemoryListener l = mLowMemoryListeners.get(i).get();
                if (l == null || l == listener) {
                    mLowMemoryListeners.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    protected void setJPushAlias(final String jPushAlias) {
        final String TAG = "jpush";
/*		JPushInterface.setAlias(this, jPushAlias, new TagAliasCallback() {

			@Override
			public void gotResult(int code, String arg1,
								  Set<String> arg2) {
				String logs;
				switch (code) {
					case 0:
						logs = "Set tag and jPushAlias success";

						// 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
						break;
					case 6002:
						logs = "Failed to set jPushAlias and tags due to timeout. Try again after 60s.";
						Log.i(TAG, logs);
						// 延迟 60 秒来调用 Handler 设置别名
						Message message = mHandler.obtainMessage();
						message.what = 6002;
						message.obj = jPushAlias;
						mHandler.sendMessageDelayed(message, 60000);
						break;
					default:
						logs = "Failed with errorCode = " + code;
						Log.e(TAG, logs);
				}
			}
		});*/
    }


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
     * 使用失败
     *
     * @param activity
     */
    @Deprecated
    private static void setToolbar(final Activity activity) {
        //设置标题,标题中的名称通过android: label获得
//        if (activity.findViewById(R.id.layout_toolbar_tb) == null) return;
        if (activity instanceof AppCompatActivity) {
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.layout_toolbar_tb);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.widget.Toolbar toolbar = (android.widget.Toolbar) activity.findViewById(R.id.layout_toolbar_tb);
                activity.setActionBar(toolbar);
                activity.getActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        ((TextView) activity.findViewById(R.id.layout_toolbar_tv_title)).setText(activity.getTitle());
        activity.findViewById(R.id.layout_toolbar_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });


    }


}
