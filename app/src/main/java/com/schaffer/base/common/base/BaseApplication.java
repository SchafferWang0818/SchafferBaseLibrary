package com.schaffer.base.common.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.schaffer.base.common.ActivityController;
import com.schaffer.base.common.ActivityManager;
import com.schaffer.base.common.listener.OnLowMemoryListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author SchafferWang
 */

public abstract class BaseApplication extends Application {


	private static BaseApplication app;
	private ArrayList<WeakReference<OnLowMemoryListener>> mLowMemoryListeners;
	private ActivityManager mActivityManager;

	protected static class DefinedActivityLifeCycleCallback implements ActivityLifecycleCallbacks {


		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			ActivityController.addActivity(activity);
		}

		@Override
		public void onActivityStarted(Activity activity) {

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
		mActivityManager = ActivityManager.getScreenManager();
		mLowMemoryListeners = new ArrayList<WeakReference<OnLowMemoryListener>>();
		registerActivityLifecycleCallbacks(new DefinedActivityLifeCycleCallback());
		libraryInit(app);//第三方
	}

	abstract void libraryInit(BaseApplication app);

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
}
