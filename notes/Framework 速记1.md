# Framework 速记1


### 1. 项目如何构建 ###

流程:

		1. 资源文件由aapt工具打包,layout,清单文件编译为二进制形式生成id生成R.java,assets不被编译;
		2. AIDL接口被aidl工具转换为java接口;
		3. 以上(包括被编译成的)java代码被java编译成.class文件;
		4. dex工具将.class与library中的.class编译成.dex文件,以最终打包入apk中,使dalvik虚拟机执行;
		5. 打包成apk后,需要使用release keystore签名才能安装到设备上;
		6. 正式签名时,为应用运行时读取、运行更快,使用zipalign工具对apk进行对齐操作;(buildTypes下的release内部使用`zipAlignEnabled true`)


流程图如下:
![image](http://img.blog.csdn.net/20160204114932917)




---


### Handler - 线程交互机制 ###

<font color="red">**注意点:**</font>

1. <font color="red">Android主线程中`ActivityThread#main(String[])`
	使用以下函数初始化主线程的looper,而子线程需要自己调用,
		且初始化不可重复;</font>

			Looper.prepareMainLooper();
			... ...
			Looper.loop();


2. `Looper#prepare(boolean)`中


	<font color="red">初始化Looper,并在构造方法中关联一个MessageQueue对象;</font>

			sThreadLocal.set(new Looper(quitAllowed));

		private Looper(boolean quitAllowed) { 
			mQueue = new MessageQueue(quitAllowed); mThread = Thread.currentThread(); 
		}



3. Handler构造函数:

	<font color="red">得到线程的Looper对象和对应的MessageQueue对象</font>

			mLooper = Looper.myLooper();
	        if (mLooper == null) {
	            throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
	        }
	        mQueue = mLooper.mQueue;
	        mCallback = callback;
	        mAsynchronous = async;

4. `Handler#sendMessage(Message)`内部调用了`Handler#enqueueMessage(MessageQueue,Message,long)`
5. `Handler#enqueueMessage(MessageQueue,Message,long)`
			
	<font color="red">msg.target就是handler对象自己;</font>

			private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
		        msg.target = this;
		        if (mAsynchronous) {
		            msg.setAsynchronous(true);
		        }
		        return queue.enqueueMessage(msg, uptimeMillis);
		    }

	<font color="red">`MessageQueue#enqueueMessage()`中使用`message.next`保存下一个Message,按照时间对Message进行排序;</font>

6. <font color="red">`Looper#loop()`方法起了一个死循环，不断的判断MessageQueue中的消息是否为空，如果为空则直接return掉，然后执行`queue.next()`方法</font>
	7. `MessageQueue#next()`主要是Message出栈,然后继续执行`Looper#loop()`中的`msg.target.dispatchMessage(msg);`也就是`Handler#dispatchMessage(msg)`
	8. `Handler#dispatchMessage(msg)`
	
			public void dispatchMessage(Message msg) {
		        if (msg.callback != null) {
		            handleCallback(msg);
		        } else {
		            if (mCallback != null) {
		                if (mCallback.handleMessage(msg)) {
		                    return;
		                }
		            }
		            handleMessage(msg);
		        }
		    }
	
		1. `Message#callback`为Runnable类型,`handleCallback(msg)`内部实现为`message.callback.run();`

			- Message内部可以封装一个Runnable类的callback,当Handler调用post系列函数时就是创建一个新的Message并传入刚传入的Runnable实现对象,然后enqueueMessage()入队列;


		2. Handler可传入Handler$Callback对象,`Callback#handleMessage(msg)`,与`Handler#handleMessage(msg)`均为空实现,使用方式相同;
	
----


### AsyncTask ###

1. 异步任务流程

	- 异步任务内部使用线程池执行后台任务，使用Handler传递消息；
	
	- onPreExecute方法主要用于在异步任务执行之前做一些操作，它所在线程与异步任务的execute方法所在的线程一致，这里若需要更新UI等操作，则execute方法不能再子线程中执行。
	
	- 通过刚刚的源码分析可以知道异步任务一般是顺序执行的，即一个任务执行完成之后才会执行下一个任务。
	
	- doInBackground这个方法所在的进程为任务所执行的进程，在这里可以进行一些后台操作。
	
	- 异步任务执行完成之后会通过一系列的调用操作，最终回调我们的onPostExecute方法
	
	- 异步任务对象不能执行多次，即不能创建一个对象执行多次execute方法。（通过execute方法的源码可以得知）

2. 缺陷

	- 引用

		- View的引用.当AsyncTask没有做取消操作时,不执行`onCancelled(Result result)`,执行`onPostExecute(Result result)` 方法,当Activity销毁之后不取消,AsyncTask的UI线程针对UI的更改都会造成崩溃;

		- Context的引用.
			- AsyncTask的子类作为Activity的非静态内部类持有引用,Activity销毁而AsyncTask不取消仍执行,保留的引用无法回收造成内存泄漏;

			- 旋转屏幕重启Activity,Activity销毁


	- 并行还是串行

      	- 1.6之前，AsyncTask是串行的，
      	- 1.6~2.3，改成了并行的。
      	- 2.3之后，可以支持并行和串行，
	      	- 如果需要串行执行，执行execute()方法，
	      	- 如果需要并行执行，执行executeOnExecutor(Executor)。

	- [线程分配导致程序FC](https://www.oschina.net/question/54100_27825)

---	

### HandlerThread 
HandlerThread 是一个要求有名字的线程,在HandlerThread#run()函数中:

		//
		@Override
	    public void run() {
	        mTid = Process.myTid();
	        Looper.prepare();
	        synchronized (this) {
	            mLooper = Looper.myLooper();
	            notifyAll();
	        }
	        Process.setThreadPriority(mPriority);
	        onLooperPrepared();
	        Looper.loop();
	        mTid = -1;
	    }

在HandlerThread#getLooper()函数中:

		public Looper getLooper() {

	        if (!isAlive()) {
	            return null;
	        }
	        
	        // If the thread has been started, wait until the looper has been created.
	        synchronized (this) {
	            while (isAlive() && mLooper == null) {
	                try {
	                    wait();
	                } catch (InterruptedException e) {
	                }
	            }
	        }
	        return mLooper;
	    }

<font color="red">**通常在使用 HandlerThread 时,我们通过 `HandlerThread#getLooper()`与创建的Handler进行关联。当HandlerThread运行时,加锁操作下初始化当前线程的Looper,`notifyAll()`所有线程,然后完成`wait()`之后的内容,getLooper()返回给其他线程的Handler当前线程的Looper.** </font>

使用`onLooperPrepared()`对需要进行的事件进行编辑.


<font color="red">**当不需要当前HandlerThread线程时调用`HandlerThread#quit()`;** </font>

总结:

	- HandlerThread本质上是一个Thread对象，只不过其内部帮我们创建了该线程的Looper和MessageQueue；
	- 通过HandlerThread我们不但可以实现UI线程与子线程的通信同样也可以实现子线程与子线程之间的通信；
	- HandlerThread在不需要使用的时候需要手动的回收掉；



---


### IntentService - 前提:startService###

onCreate()内部封装自定义线程HandlerThread 并且配对 Handler对封装为Message#obj的Intent内容进行处理 的 Service. 

<font color="orange">**每次IntentService后台任务执行完成之后都会尝试关闭自身`stopSelf(msg.arg1)`，但是当且仅当IntentService消息队列中最后一个消息被执行完成之后才会真正的stop自身**</font>


当内存充足时,`IntentService#setIntentRedelivery(boolean)`传入true,即可在被回收之后尝试重新启动.(`Service#START_REDELIVER_INTENT`)



---

### Log ###

Log类中所有的静态日志方法Log.v()，Log.d()，Log.i()，Log.w()，Log.e()等方法都是底层都是调用了println方法，其实其内部调用的是println_native方法，也就是通过JNI调用底层的c++输出日志.

---


### 缓存机制LruCache：(Least recent used)最少最近使用算法 ###

1. 缓存策略:

	<font color="pink">**cache对象通过一个强引用来访问内容。每次当一个item被访问到的时候(`get(K key)`)，这个item就会被移动到一个队列的队首。当一个item被添加到已经满了的队列时，这个队列的队尾的item就会被移除。**</font>

2. put

	将键值对压入Map数据结构中，若这是Map的大小已经大于LruCache中定义的最大值，则将Map中最早压入的元素remove掉；

3. get

	简单的理解就是通过key值从map中取出Value值。 具体来说，判断map中是否含有key值value值，若存在，则hitCount（击中元素数量）自增，并返回Value值，若没有击中，则执行create(key)方法，这里看到create方法是一个空的实现方法，返回值为null，所以我们可以重写该方法，在调用get（key）的时候若没有找到value值，则自动创建一个value值并压入map中。


[4. 参考](http://blog.csdn.net/ylyg050518/article/details/52094272)

---

---
## 应用启动过程 ##

Android系统是基于Linux内核的，而在linux系统中，所有的进程都是init进程的子孙进程，也就是说，<font color = "red">所有的进程都是直接或者间接地由init进程fork出来的。</font>

![image](http://hi.csdn.net/attachment/201109/16/0_1316190384ZuU0.gif)

内容参考[这里](http://blog.csdn.net/luoshengyang/article/details/6768304)

Zygote进程也不例外，它是在系统启动的过程，由init进程创建的。
### Zygote进程 ###

	public static void main(String argv[]) {
	    try {
	        RuntimeInit.enableDdms();
	        // Start profiling the zygote initialization.
	        SamplingProfilerIntegration.start();
	
	        boolean startSystemServer = false;
	        String socketName = "zygote";
	        String abiList = null;
	        for (int i = 1; i < argv.length; i++) {
	            if ("start-system-server".equals(argv[i])) {
	                startSystemServer = true;
	            } else if (argv[i].startsWith(ABI_LIST_ARG)) {
	                abiList = argv[i].substring(ABI_LIST_ARG.length());
	            } else if (argv[i].startsWith(SOCKET_NAME_ARG)) {
	                socketName = argv[i].substring(SOCKET_NAME_ARG.length());
	            } else {
	                throw new RuntimeException("Unknown command line argument: " + argv[i]);
	            }
	        }
	
	        if (abiList == null) {
	            throw new RuntimeException("No ABI list supplied.");
	        }
	
	        registerZygoteSocket(socketName);
	        EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_START,
	            SystemClock.uptimeMillis());
	        preload();
	        EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_END,
	            SystemClock.uptimeMillis());
	
	        // Finish profiling the zygote initialization.
	        SamplingProfilerIntegration.writeZygoteSnapshot();
	
	        // Do an initial gc to clean up after startup
	        gcAndFinalize();
	
	        // Disable tracing so that forked processes do not inherit stale tracing tags from
	        // Zygote.
	        Trace.setTracingEnabled(false);
	
	        if (startSystemServer) {
	            startSystemServer(abiList, socketName);
	        }
	
	        Log.i(TAG, "Accepting command socket connections");
	        runSelectLoop(abiList);
	
	        closeServerSocket();
	    } catch (MethodAndArgsCaller caller) {
	        caller.run();
	    } catch (RuntimeException ex) {
	        Log.e(TAG, "Zygote died with exception", ex);
	        closeServerSocket();
	        throw ex;
	    }
	}


启动过程解读:

1. 设置DDMS可用
	
		RuntimeInit.enableDdms();

2. 从`main(String[])`的参数判断是否需要启动SystemServer,获得abi列表和socket名称;(由于SystemService 与 Zygote 进程间通信通过 Socket 完成.)

		for (int i = 1; i < argv.length; i++) {
            if ("start-system-server".equals(argv[i])) {
                startSystemServer = true;
            } else if (argv[i].startsWith(ABI_LIST_ARG)) {
                abiList = argv[i].substring(ABI_LIST_ARG.length());
            } else if (argv[i].startsWith(SOCKET_NAME_ARG)) {
                socketName = argv[i].substring(SOCKET_NAME_ARG.length());
            } else {
                throw new RuntimeException("Unknown command line argument: " + argv[i]);
            }
        }

		registerZygoteSocket(socketName);
        


3. preload()初始化需要的class类;

	```
		...

		preload();

		//preload()函数
		static void preload() {
				...		
		        beginIcuCachePinning();
				...		
		        preloadClasses();
				...		
		        preloadResources();//初始化系统资源
				...		
		        preloadOpenGL();//初始化OpenGL
				...
				preloadSharedLibraries();//初始化系统libraries		
		        preloadTextResources();//初始化文字资源
		
		        // Ask the WebViewFactory to do any initialization that must run in the zygote process,
		        // for memory sharing purposes.
		        WebViewFactory.prepareWebViewInZygote();//初始化webview
		        endIcuCachePinning();
		        warmUpJcaProviders();
		        Log.d(TAG, "end preload");
		}

	```

4. 通过Zygote fork出SystemServer进程;
		...

        if (startSystemServer) {
            startSystemServer(abiList, socketName);
        }

<font color = "red">
Zygote启动总结:

	1. 设置ddms;
	2. 循环判断是否需要启动SystemServer,获得abi列表和socket名称;
	3. 预加载需要的class类,OpenGL,系统库,文字资源,WebView等;
	4. 根据abi列表和socket名称fork出SystemServer进程;
</font>
---	
### SystemServer进程 ###

<font color = "red">前述:

	1. Android根进程Zygote启动SystemServer;
	2. 设置系统时间,虚拟机运行内存,加载运行库,设置异步消息;
	3. 创建上下文来创建SystemServiceManager对象;
	4. 管理对象反射得到启动服务Installer来ping连接根进程Zygote,并启动组件交互,电源,灯光,显示,包管理服务;
	5. 管理对象启动内核级服务和其他服务;

</font>

SystemServer承前启后.

	1. 由Android跟进程Zygote做了各种准备工作之后,通过socket启动;
	2. 在进程中启动系统服务和各种系统性的服务.
	3. 应用中需要各种系统服务通过与SystemServer进程通讯获得服务对象的句柄.

SystemServer 源码分析

1. `main(String[])`内部创建当前类的对象并调用了`run()`;
2. `run()`内:

	1. 当系统当前时间小于1970-01-01 08:00:00时,将时间设置为这个时间值	
	2. 设置虚拟机运行内存，加载运行库，设置SystemServer的异步消息
	3. SystemServer#createSystemContext()初始化系统的上下文context;
	4. 根据系统上下文创建SystemServiceManager对象,管理各种服务;
	5. startBootstrapServices()
		1. 内部使用反射,调用apk安装时的一个服务类Installer启动后启动其他系统服务.onStart()中调用InstallerConnection#waitForConnection()不断的通过Socket的ping命令连接Zygote进程;
		2. 使用 mSystemServiceManager 启动 组件交互,电源,灯光,显示,包管理服务;
			1. 启动 ActivityManagerService,并设置mSystemServiceManager和installer.(控制四大组件与系统的交互)
			2. 启动 PowerManagerService(协调与决策Power与其他模块的交互来进行一系列的反应。)
			3. 启动 LightsService(控制闪光灯和LED)
			4. 启动 DisplayManagerService(控制手机显示)
			5. 启动 PackageManagerService(控制多apk文件的安装，解析，删除，卸载等等操作)
	6. startCoreServices()启动了BatteryService（电池相关服务），UsageStatsService(使用状态相关服务)，WebViewUpdateService(网页更新相关服务)服务等。
	7. startOtherServices()，主要用于启动系统中其他的服务如:定位,蓝牙,UI,网络,音视频播放,通话,短信

			private void run() {
		        try {
						...
					//当系统当前时间小于1970-01-01 08:00:00时,将时间设置为这个时间值	
		            if (System.currentTimeMillis() < EARLIEST_SUPPORTED_TIME) {
		                Slog.w(TAG, "System clock is before 1970; setting to 1970.");
		                SystemClock.setCurrentTimeMillis(EARLIEST_SUPPORTED_TIME);
		            }
	
					//设置虚拟机运行内存，加载运行库，设置SystemServer的异步消息
		
		            // If the system has "persist.sys.language" and friends set, replace them with
		            // "persist.sys.locale". Note that the default locale at this point is calculated
		            // using the "-Duser.locale" command line flag. That flag is usually populated by
		            // AndroidRuntime using the same set of system properties, but only the system_server
		            // and system apps are allowed to set them.
		            //
		            // NOTE: Most changes made here will need an equivalent change to
		            // core/jni/AndroidRuntime.cpp
		            if (!SystemProperties.get("persist.sys.language").isEmpty()) {
		                final String languageTag = Locale.getDefault().toLanguageTag();
		
		                SystemProperties.set("persist.sys.locale", languageTag);
		                SystemProperties.set("persist.sys.language", "");
		                SystemProperties.set("persist.sys.country", "");
		                SystemProperties.set("persist.sys.localevar", "");
		            }
		
		            // Here we go!
		            Slog.i(TAG, "Entered the Android system server!");
		            EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_SYSTEM_RUN, SystemClock.uptimeMillis());
		
		            // In case the runtime switched since last boot (such as when
		            // the old runtime was removed in an OTA), set the system
		            // property so that it is in sync. We can't do this in
		            // libnativehelper's JniInvocation::Init code where we already
		            // had to fallback to a different runtime because it is
		            // running as root and we need to be the system user to set
		            // the property. http://b/11463182
		            SystemProperties.set("persist.sys.dalvik.vm.lib.2", VMRuntime.getRuntime().vmLibrary());
		
		            // Enable the sampling profiler.
		            if (SamplingProfilerIntegration.isEnabled()) {
		                SamplingProfilerIntegration.start();
		                mProfilerSnapshotTimer = new Timer();
		                mProfilerSnapshotTimer.schedule(new TimerTask() {
		                        @Override
		                        public void run() {
		                            SamplingProfilerIntegration.writeSnapshot("system_server", null);
		                        }
		                    }, SNAPSHOT_INTERVAL, SNAPSHOT_INTERVAL);
		            }
		
		            // Mmmmmm... more memory!
		            VMRuntime.getRuntime().clearGrowthLimit();
		
		            // The system server has to run all of the time, so it needs to be
		            // as efficient as possible with its memory usage.
		            VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);
		
		            // Some devices rely on runtime fingerprint generation, so make sure
		            // we've defined it before booting further.
		            Build.ensureFingerprintProperty();
		
		            // Within the system server, it is an error to access Environment paths without
		            // explicitly specifying a user.
		            Environment.setUserRequired(true);
		
		            // Within the system server, any incoming Bundles should be defused
		            // to avoid throwing BadParcelableException.
		            BaseBundle.setShouldDefuse(true);
		
		            // Ensure binder calls into the system always run at foreground priority.
		            BinderInternal.disableBackgroundScheduling(true);
		
		            // Increase the number of binder threads in system_server
		            BinderInternal.setMaxThreads(sMaxBinderThreads);
		
		            // Prepare the main looper thread (this thread).
		            android.os.Process.setThreadPriority(
		                android.os.Process.THREAD_PRIORITY_FOREGROUND);
		            android.os.Process.setCanSelfBackground(false);
		            Looper.prepareMainLooper();
		
		            // Initialize native services.
		            System.loadLibrary("android_servers");
		
		            // Check whether we failed to shut down last time we tried.
		            // This call may not return.
		            performPendingShutdown();
	
					//SystemServer#createSystemContext()初始化系统的上下文context;	
		            // Initialize the system context.
		            createSystemContext();
		
					//根据系统上下文创建SystemServiceManager对象,管理各种服务;
		            // Create the system service manager.
		            mSystemServiceManager = new SystemServiceManager(mSystemContext);
		            LocalServices.addService(SystemServiceManager.class, mSystemServiceManager);
		        } finally {
		            Trace.traceEnd(Trace.TRACE_TAG_SYSTEM_SERVER);
		        }
		
		        // Start services.
		        try {
		            Trace.traceBegin(Trace.TRACE_TAG_SYSTEM_SERVER, "StartServices");
		            startBootstrapServices();//启动系统boot级服务,
		            startCoreServices();//启动内核级服务
		            startOtherServices();//启动其他服务
		        } catch (Throwable ex) {
		            Slog.e("System", "******************************************");
		            Slog.e("System", "************ Failure starting system services", ex);
		            throw ex;
		        } finally {
		            Trace.traceEnd(Trace.TRACE_TAG_SYSTEM_SERVER);
		        }
		
		        // For debug builds, log event loop stalls to dropbox for analysis.
		        if (StrictMode.conditionallyEnableDebugLogging()) {
		            Slog.i(TAG, "Enabled StrictMode for system server main thread.");
		        }
		
		        // Loop forever.
		        Looper.loop();
		        throw new RuntimeException("Main thread loop unexpectedly exited");
		    }


	







---

### Launcher启动流程 ###

不同的手机厂商定制android操作系统的时候都会更改Launcher的源代码。

1. SystemServer进程启动中开启其他服务startOtherService()中，执行了各种服务的systemReady()和`ActivityManagerService#systemReady(Runnable)`函数
2. 而这个函数内部调用了`ActivityManagerService#startHomeActivityLocked(int userId, String reason)`启动<font color = "red">HomeActivity系统主界面</font>,该页面的category需要有`Intent.CATEGORY_HOME`;
3. 调用`mActivityStarter.startHomeActivityLocked(intent, aInfo, reason)`隐式调用这个Intent启动LauncherActivity;
4. LauncherActivity#onCreate()中初始化PackageManager获得所有安装的应用列表,包名,图标等信息,交给ListView的ActivityAdapter,onListItemClick()函数中通过得到应用包名和Activity名初始化Intent的方式就启动应用图标对应的应用了.


---

### 应用进程启动流程 ###

应用四大组件开启先要启动应用进程.

1. LauncherActivity在onListItemClick()中,使用Intent的包名类名和Bundle通过startActivity(/forResult)()函数启动启动页面Activity
	1. 内部调用Instrumentation.execStartActivity()这个类是Activity的监测操作类,Activity的生命周期也通过它操作;方法内部:<font color = "red">**ActivityManagerService作为服务器端,ActivityManagerNative作为客户端,客户端向服务器端传递数据,执行服务器端的`startActivity()`函数**</font>
	2. 层层调用,最终调用服务器端的`startProcessLocked()`函数开启新的进程.
	3. 内部调用了`Process.start(...)`函数,调用`ActivityThread#main(String[])`

		   	Process.ProcessStartResult startResult = Process.start(
				entryPoint,			//android.app.ActivityThread 进程开始起点		
             	app.processName,	//进程名称
				uid, uid, gids, debugFlags, mountExternal,	//user-id,group-id,group-ids,
                app.info.targetSdkVersion,	//SDK
				app.info.seinfo, 	//SELinux内核信息
				requiredAbi, 		//应用二进制接口 与so包相关
				instructionSet,		//指令集架构
                app.info.dataDir, 	//应用数据目录
				entryPointArgs);	//支持zygote进程的附加协议
		





---
### apk的再次解析安装和Manifest解析 ###

1. android系统启动之后会解析固定目录下的apk文件，并执行解析，持久化apk信息，重新安装等操作；
2. 解析Manifest
	1. 流程:

			Zygote进程 
			--> SystemServer进程
			--> SystemServer#startBootstrapServices()初始化PackageManagerService服务 
			--> PackageManagerService#main()调用构造函数
			--> 构造器初始化系统安装apk的目录
			--> 构造器调用PackageManagerService#scanDirLI()
			--> scanDirLI()调用scanPackageLI()解析apk 
			--> PackageParser#parserPackage()
			--> PackageParser#parseMonolithicPackage(packageFile, flags)
			--> PackageParser#parseBaseApk(apkFile, assets, flags)重载解析Manifest
			--> while循环解析节点信息

	2. 解析Manifest代码流程(android 25 中)

			    /**
			     * This is the common parsing routing for handling parent and child
			     * packages in a base APK. The difference between parent and child
			     * parsing is that some tags are not supported by child packages as
			     * well as some manifest attributes are ignored. The implementation
			     * assumes the calling code has already handled the manifest tag if needed
			     * (this applies to the parent only).
			     *
			     * @param pkg The package which to populate
			     * @param acceptedTags Which tags to handle, null to handle all
			     * @param res Resources against which to resolve values
			     * @param parser Parser of the manifest
			     * @param flags Flags about how to parse
			     * @param outError Human readable error if parsing fails
			     * @return The package if parsing succeeded or null.
			     *
			     * @throws XmlPullParserException
			     * @throws IOException
			     */
			    private Package parseBaseApkCommon(Package pkg, Set<String> acceptedTags, Resources res,
			            XmlResourceParser parser, int flags, String[] outError) throws XmlPullParserException,
			            IOException {
			        mParseInstrumentationArgs = null;
			        mParseActivityArgs = null;
			        mParseServiceArgs = null;
			        mParseProviderArgs = null;
			
			        int type;
			        boolean foundApp = false;
			
			        TypedArray sa = res.obtainAttributes(parser,
			                com.android.internal.R.styleable.AndroidManifest);
			
			        String str = sa.getNonConfigurationString(
			                com.android.internal.R.styleable.AndroidManifest_sharedUserId, 0);
			        if (str != null && str.length() > 0) {
			            String nameError = validateName(str, true, false);
			            if (nameError != null && !"android".equals(pkg.packageName)) {
			                outError[0] = "<manifest> specifies bad sharedUserId name \""
			                    + str + "\": " + nameError;
			                mParseError = PackageManager.INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID;
			                return null;
			            }
			            pkg.mSharedUserId = str.intern();
			            pkg.mSharedUserLabel = sa.getResourceId(
			                    com.android.internal.R.styleable.AndroidManifest_sharedUserLabel, 0);
			        }
			
			        pkg.installLocation = sa.getInteger(
			                com.android.internal.R.styleable.AndroidManifest_installLocation,
			                PARSE_DEFAULT_INSTALL_LOCATION);
			        pkg.applicationInfo.installLocation = pkg.installLocation;
			
			        /* Set the global "forward lock" flag */
			        if ((flags & PARSE_FORWARD_LOCK) != 0) {
			            pkg.applicationInfo.privateFlags |= ApplicationInfo.PRIVATE_FLAG_FORWARD_LOCK;
			        }
			
			        /* Set the global "on SD card" flag */
			        if ((flags & PARSE_EXTERNAL_STORAGE) != 0) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_EXTERNAL_STORAGE;
			        }
			
			        if ((flags & PARSE_IS_EPHEMERAL) != 0) {
			            pkg.applicationInfo.privateFlags |= ApplicationInfo.PRIVATE_FLAG_EPHEMERAL;
			        }
			
			        // Resource boolean are -1, so 1 means we don't know the value.
			        int supportsSmallScreens = 1;
			        int supportsNormalScreens = 1;
			        int supportsLargeScreens = 1;
			        int supportsXLargeScreens = 1;
			        int resizeable = 1;
			        int anyDensity = 1;
			
			        int outerDepth = parser.getDepth();
			        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
			                && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
			            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
			                continue;
			            }
			
			            String tagName = parser.getName();
			
			            if (acceptedTags != null && !acceptedTags.contains(tagName)) {
			                Slog.w(TAG, "Skipping unsupported element under <manifest>: "
			                        + tagName + " at " + mArchiveSourcePath + " "
			                        + parser.getPositionDescription());
			                XmlUtils.skipCurrentTag(parser);
			                continue;
			            }
			
			            if (tagName.equals(TAG_APPLICATION)) {
			                if (foundApp) {
			                    if (RIGID_PARSER) {
			                        outError[0] = "<manifest> has more than one <application>";
			                        mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
			                        return null;
			                    } else {
			                        Slog.w(TAG, "<manifest> has more than one <application>");
			                        XmlUtils.skipCurrentTag(parser);
			                        continue;
			                    }
			                }
			
			                foundApp = true;
			                if (!parseBaseApplication(pkg, res, parser, flags, outError)) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_OVERLAY)) {
			                sa = res.obtainAttributes(parser,
			                        com.android.internal.R.styleable.AndroidManifestResourceOverlay);
			                pkg.mOverlayTarget = sa.getString(
			                        com.android.internal.R.styleable.AndroidManifestResourceOverlay_targetPackage);
			                pkg.mOverlayPriority = sa.getInt(
			                        com.android.internal.R.styleable.AndroidManifestResourceOverlay_priority,
			                        -1);
			                sa.recycle();
			
			                if (pkg.mOverlayTarget == null) {
			                    outError[0] = "<overlay> does not specify a target package";
			                    mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
			                    return null;
			                }
			                if (pkg.mOverlayPriority < 0 || pkg.mOverlayPriority > 9999) {
			                    outError[0] = "<overlay> priority must be between 0 and 9999";
			                    mParseError =
			                        PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
			                    return null;
			                }
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_KEY_SETS)) {
			                if (!parseKeySets(pkg, res, parser, outError)) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_PERMISSION_GROUP)) {
			                if (parsePermissionGroup(pkg, flags, res, parser, outError) == null) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_PERMISSION)) {
			                if (parsePermission(pkg, res, parser, outError) == null) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_PERMISSION_TREE)) {
			                if (parsePermissionTree(pkg, res, parser, outError) == null) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_USES_PERMISSION)) {
			                if (!parseUsesPermission(pkg, res, parser)) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_USES_PERMISSION_SDK_M)
			                    || tagName.equals(TAG_USES_PERMISSION_SDK_23)) {
			                if (!parseUsesPermission(pkg, res, parser)) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_USES_CONFIGURATION)) {
			                ConfigurationInfo cPref = new ConfigurationInfo();
			                sa = res.obtainAttributes(parser,
			                        com.android.internal.R.styleable.AndroidManifestUsesConfiguration);
			                cPref.reqTouchScreen = sa.getInt(
			                        com.android.internal.R.styleable.AndroidManifestUsesConfiguration_reqTouchScreen,
			                        Configuration.TOUCHSCREEN_UNDEFINED);
			                cPref.reqKeyboardType = sa.getInt(
			                        com.android.internal.R.styleable.AndroidManifestUsesConfiguration_reqKeyboardType,
			                        Configuration.KEYBOARD_UNDEFINED);
			                if (sa.getBoolean(
			                        com.android.internal.R.styleable.AndroidManifestUsesConfiguration_reqHardKeyboard,
			                        false)) {
			                    cPref.reqInputFeatures |= ConfigurationInfo.INPUT_FEATURE_HARD_KEYBOARD;
			                }
			                cPref.reqNavigation = sa.getInt(
			                        com.android.internal.R.styleable.AndroidManifestUsesConfiguration_reqNavigation,
			                        Configuration.NAVIGATION_UNDEFINED);
			                if (sa.getBoolean(
			                        com.android.internal.R.styleable.AndroidManifestUsesConfiguration_reqFiveWayNav,
			                        false)) {
			                    cPref.reqInputFeatures |= ConfigurationInfo.INPUT_FEATURE_FIVE_WAY_NAV;
			                }
			                sa.recycle();
			                pkg.configPreferences = ArrayUtils.add(pkg.configPreferences, cPref);
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_USES_FEATURE)) {
			                FeatureInfo fi = parseUsesFeature(res, parser);
			                pkg.reqFeatures = ArrayUtils.add(pkg.reqFeatures, fi);
			
			                if (fi.name == null) {
			                    ConfigurationInfo cPref = new ConfigurationInfo();
			                    cPref.reqGlEsVersion = fi.reqGlEsVersion;
			                    pkg.configPreferences = ArrayUtils.add(pkg.configPreferences, cPref);
			                }
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_FEATURE_GROUP)) {
			                FeatureGroupInfo group = new FeatureGroupInfo();
			                ArrayList<FeatureInfo> features = null;
			                final int innerDepth = parser.getDepth();
			                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
			                        && (type != XmlPullParser.END_TAG || parser.getDepth() > innerDepth)) {
			                    if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
			                        continue;
			                    }
			
			                    final String innerTagName = parser.getName();
			                    if (innerTagName.equals("uses-feature")) {
			                        FeatureInfo featureInfo = parseUsesFeature(res, parser);
			                        // FeatureGroups are stricter and mandate that
			                        // any <uses-feature> declared are mandatory.
			                        featureInfo.flags |= FeatureInfo.FLAG_REQUIRED;
			                        features = ArrayUtils.add(features, featureInfo);
			                    } else {
			                        Slog.w(TAG, "Unknown element under <feature-group>: " + innerTagName +
			                                " at " + mArchiveSourcePath + " " +
			                                parser.getPositionDescription());
			                    }
			                    XmlUtils.skipCurrentTag(parser);
			                }
			
			                if (features != null) {
			                    group.features = new FeatureInfo[features.size()];
			                    group.features = features.toArray(group.features);
			                }
			                pkg.featureGroups = ArrayUtils.add(pkg.featureGroups, group);
			
			            } else if (tagName.equals(TAG_USES_SDK)) {
			                if (SDK_VERSION > 0) {
			                    sa = res.obtainAttributes(parser,
			                            com.android.internal.R.styleable.AndroidManifestUsesSdk);
			
			                    int minVers = 1;
			                    String minCode = null;
			                    int targetVers = 0;
			                    String targetCode = null;
			
			                    TypedValue val = sa.peekValue(
			                            com.android.internal.R.styleable.AndroidManifestUsesSdk_minSdkVersion);
			                    if (val != null) {
			                        if (val.type == TypedValue.TYPE_STRING && val.string != null) {
			                            targetCode = minCode = val.string.toString();
			                        } else {
			                            // If it's not a string, it's an integer.
			                            targetVers = minVers = val.data;
			                        }
			                    }
			
			                    val = sa.peekValue(
			                            com.android.internal.R.styleable.AndroidManifestUsesSdk_targetSdkVersion);
			                    if (val != null) {
			                        if (val.type == TypedValue.TYPE_STRING && val.string != null) {
			                            targetCode = val.string.toString();
			                            if (minCode == null) {
			                                minCode = targetCode;
			                            }
			                        } else {
			                            // If it's not a string, it's an integer.
			                            targetVers = val.data;
			                        }
			                    }
			
			                    sa.recycle();
			
			                    if (minCode != null) {
			                        boolean allowedCodename = false;
			                        for (String codename : SDK_CODENAMES) {
			                            if (minCode.equals(codename)) {
			                                allowedCodename = true;
			                                break;
			                            }
			                        }
			                        if (!allowedCodename) {
			                            if (SDK_CODENAMES.length > 0) {
			                                outError[0] = "Requires development platform " + minCode
			                                        + " (current platform is any of "
			                                        + Arrays.toString(SDK_CODENAMES) + ")";
			                            } else {
			                                outError[0] = "Requires development platform " + minCode
			                                        + " but this is a release platform.";
			                            }
			                            mParseError = PackageManager.INSTALL_FAILED_OLDER_SDK;
			                            return null;
			                        }
			                        pkg.applicationInfo.minSdkVersion =
			                                android.os.Build.VERSION_CODES.CUR_DEVELOPMENT;
			                    } else if (minVers > SDK_VERSION) {
			                        outError[0] = "Requires newer sdk version #" + minVers
			                                + " (current version is #" + SDK_VERSION + ")";
			                        mParseError = PackageManager.INSTALL_FAILED_OLDER_SDK;
			                        return null;
			                    } else {
			                        pkg.applicationInfo.minSdkVersion = minVers;
			                    }
			
			                    if (targetCode != null) {
			                        boolean allowedCodename = false;
			                        for (String codename : SDK_CODENAMES) {
			                            if (targetCode.equals(codename)) {
			                                allowedCodename = true;
			                                break;
			                            }
			                        }
			                        if (!allowedCodename) {
			                            if (SDK_CODENAMES.length > 0) {
			                                outError[0] = "Requires development platform " + targetCode
			                                        + " (current platform is any of "
			                                        + Arrays.toString(SDK_CODENAMES) + ")";
			                            } else {
			                                outError[0] = "Requires development platform " + targetCode
			                                        + " but this is a release platform.";
			                            }
			                            mParseError = PackageManager.INSTALL_FAILED_OLDER_SDK;
			                            return null;
			                        }
			                        // If the code matches, it definitely targets this SDK.
			                        pkg.applicationInfo.targetSdkVersion
			                                = android.os.Build.VERSION_CODES.CUR_DEVELOPMENT;
			                    } else {
			                        pkg.applicationInfo.targetSdkVersion = targetVers;
			                    }
			                }
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_SUPPORT_SCREENS)) {
			                sa = res.obtainAttributes(parser,
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens);
			
			                pkg.applicationInfo.requiresSmallestWidthDp = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_requiresSmallestWidthDp,
			                        0);
			                pkg.applicationInfo.compatibleWidthLimitDp = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_compatibleWidthLimitDp,
			                        0);
			                pkg.applicationInfo.largestWidthLimitDp = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_largestWidthLimitDp,
			                        0);
			
			                // This is a trick to get a boolean and still able to detect
			                // if a value was actually set.
			                supportsSmallScreens = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_smallScreens,
			                        supportsSmallScreens);
			                supportsNormalScreens = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_normalScreens,
			                        supportsNormalScreens);
			                supportsLargeScreens = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_largeScreens,
			                        supportsLargeScreens);
			                supportsXLargeScreens = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_xlargeScreens,
			                        supportsXLargeScreens);
			                resizeable = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_resizeable,
			                        resizeable);
			                anyDensity = sa.getInteger(
			                        com.android.internal.R.styleable.AndroidManifestSupportsScreens_anyDensity,
			                        anyDensity);
			
			                sa.recycle();
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_PROTECTED_BROADCAST)) {
			                sa = res.obtainAttributes(parser,
			                        com.android.internal.R.styleable.AndroidManifestProtectedBroadcast);
			
			                // Note: don't allow this value to be a reference to a resource
			                // that may change.
			                String name = sa.getNonResourceString(
			                        com.android.internal.R.styleable.AndroidManifestProtectedBroadcast_name);
			
			                sa.recycle();
			
			                if (name != null && (flags&PARSE_IS_SYSTEM) != 0) {
			                    if (pkg.protectedBroadcasts == null) {
			                        pkg.protectedBroadcasts = new ArrayList<String>();
			                    }
			                    if (!pkg.protectedBroadcasts.contains(name)) {
			                        pkg.protectedBroadcasts.add(name.intern());
			                    }
			                }
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_INSTRUMENTATION)) {
			                if (parseInstrumentation(pkg, res, parser, outError) == null) {
			                    return null;
			                }
			            } else if (tagName.equals(TAG_ORIGINAL_PACKAGE)) {
			                sa = res.obtainAttributes(parser,
			                        com.android.internal.R.styleable.AndroidManifestOriginalPackage);
			
			                String orig =sa.getNonConfigurationString(
			                        com.android.internal.R.styleable.AndroidManifestOriginalPackage_name, 0);
			                if (!pkg.packageName.equals(orig)) {
			                    if (pkg.mOriginalPackages == null) {
			                        pkg.mOriginalPackages = new ArrayList<String>();
			                        pkg.mRealPackage = pkg.packageName;
			                    }
			                    pkg.mOriginalPackages.add(orig);
			                }
			
			                sa.recycle();
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_ADOPT_PERMISSIONS)) {
			                sa = res.obtainAttributes(parser,
			                        com.android.internal.R.styleable.AndroidManifestOriginalPackage);
			
			                String name = sa.getNonConfigurationString(
			                        com.android.internal.R.styleable.AndroidManifestOriginalPackage_name, 0);
			
			                sa.recycle();
			
			                if (name != null) {
			                    if (pkg.mAdoptPermissions == null) {
			                        pkg.mAdoptPermissions = new ArrayList<String>();
			                    }
			                    pkg.mAdoptPermissions.add(name);
			                }
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (tagName.equals(TAG_USES_GL_TEXTURE)) {
			                // Just skip this tag
			                XmlUtils.skipCurrentTag(parser);
			                continue;
			
			            } else if (tagName.equals(TAG_COMPATIBLE_SCREENS)) {
			                // Just skip this tag
			                XmlUtils.skipCurrentTag(parser);
			                continue;
			            } else if (tagName.equals(TAG_SUPPORTS_INPUT)) {//
			                XmlUtils.skipCurrentTag(parser);
			                continue;
			
			            } else if (tagName.equals(TAG_EAT_COMMENT)) {
			                // Just skip this tag
			                XmlUtils.skipCurrentTag(parser);
			                continue;
			
			            } else if (tagName.equals(TAG_PACKAGE)) {
			                if (!MULTI_PACKAGE_APK_ENABLED) {
			                    XmlUtils.skipCurrentTag(parser);
			                    continue;
			                }
			                if (!parseBaseApkChild(pkg, res, parser, flags, outError)) {
			                    // If parsing a child failed the error is already set
			                    return null;
			                }
			
			            } else if (tagName.equals(TAG_RESTRICT_UPDATE)) {
			                if ((flags & PARSE_IS_SYSTEM_DIR) != 0) {
			                    sa = res.obtainAttributes(parser,
			                            com.android.internal.R.styleable.AndroidManifestRestrictUpdate);
			                    final String hash = sa.getNonConfigurationString(
			                            com.android.internal.R.styleable.AndroidManifestRestrictUpdate_hash, 0);
			                    sa.recycle();
			
			                    pkg.restrictUpdateHash = null;
			                    if (hash != null) {
			                        final int hashLength = hash.length();
			                        final byte[] hashBytes = new byte[hashLength / 2];
			                        for (int i = 0; i < hashLength; i += 2){
			                            hashBytes[i/2] = (byte) ((Character.digit(hash.charAt(i), 16) << 4)
			                                    + Character.digit(hash.charAt(i + 1), 16));
			                        }
			                        pkg.restrictUpdateHash = hashBytes;
			                    }
			                }
			
			                XmlUtils.skipCurrentTag(parser);
			
			            } else if (RIGID_PARSER) {
			                outError[0] = "Bad element under <manifest>: "
			                    + parser.getName();
			                mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
			                return null;
			
			            } else {
			                Slog.w(TAG, "Unknown element under <manifest>: " + parser.getName()
			                        + " at " + mArchiveSourcePath + " "
			                        + parser.getPositionDescription());
			                XmlUtils.skipCurrentTag(parser);
			                continue;
			            }
			        }
			
			        if (!foundApp && pkg.instrumentation.size() == 0) {
			            outError[0] = "<manifest> does not contain an <application> or <instrumentation>";
			            mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
			        }
			
			        final int NP = PackageParser.NEW_PERMISSIONS.length;
			        StringBuilder implicitPerms = null;
			        for (int ip=0; ip<NP; ip++) {
			            final PackageParser.NewPermissionInfo npi
			                    = PackageParser.NEW_PERMISSIONS[ip];
			            if (pkg.applicationInfo.targetSdkVersion >= npi.sdkVersion) {
			                break;
			            }
			            if (!pkg.requestedPermissions.contains(npi.name)) {
			                if (implicitPerms == null) {
			                    implicitPerms = new StringBuilder(128);
			                    implicitPerms.append(pkg.packageName);
			                    implicitPerms.append(": compat added ");
			                } else {
			                    implicitPerms.append(' ');
			                }
			                implicitPerms.append(npi.name);
			                pkg.requestedPermissions.add(npi.name);
			            }
			        }
			        if (implicitPerms != null) {
			            Slog.i(TAG, implicitPerms.toString());
			        }
			
			        final int NS = PackageParser.SPLIT_PERMISSIONS.length;
			        for (int is=0; is<NS; is++) {
			            final PackageParser.SplitPermissionInfo spi
			                    = PackageParser.SPLIT_PERMISSIONS[is];
			            if (pkg.applicationInfo.targetSdkVersion >= spi.targetSdk
			                    || !pkg.requestedPermissions.contains(spi.rootPerm)) {
			                continue;
			            }
			            for (int in=0; in<spi.newPerms.length; in++) {
			                final String perm = spi.newPerms[in];
			                if (!pkg.requestedPermissions.contains(perm)) {
			                    pkg.requestedPermissions.add(perm);
			                }
			            }
			        }
			
			        if (supportsSmallScreens < 0 || (supportsSmallScreens > 0
			                && pkg.applicationInfo.targetSdkVersion
			                        >= android.os.Build.VERSION_CODES.DONUT)) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS;
			        }
			        if (supportsNormalScreens != 0) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS;
			        }
			        if (supportsLargeScreens < 0 || (supportsLargeScreens > 0
			                && pkg.applicationInfo.targetSdkVersion
			                        >= android.os.Build.VERSION_CODES.DONUT)) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS;
			        }
			        if (supportsXLargeScreens < 0 || (supportsXLargeScreens > 0
			                && pkg.applicationInfo.targetSdkVersion
			                        >= android.os.Build.VERSION_CODES.GINGERBREAD)) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS;
			        }
			        if (resizeable < 0 || (resizeable > 0
			                && pkg.applicationInfo.targetSdkVersion
			                        >= android.os.Build.VERSION_CODES.DONUT)) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS;
			        }
			        if (anyDensity < 0 || (anyDensity > 0
			                && pkg.applicationInfo.targetSdkVersion
			                        >= android.os.Build.VERSION_CODES.DONUT)) {
			            pkg.applicationInfo.flags |= ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES;
			        }
			
			        return pkg;
			    }
					
	3. 解析后会将apk的Manifest信息保存在Settings对象中并持久化，然后执行重新安装的操作；

---
