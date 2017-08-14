# Framework 速记2



### Apk 安装流程 ###
[安装界面点击进入](https://github.com/android/platform_packages_apps_packageinstaller/blob/master/src/com/android/packageinstaller/PackageInstallerActivity.java)

- 代码中执行intent.setDataAndType(Uri.parse("file://" + path),"application/vnd.android.package-archive");可以调起PackageInstallerActivity；

- PackageInstallerActivity主要用于执行解析apk文件，解析manifest，解析签名等操作；

- InstallAppProcess主要用于执行安装apk逻辑，用于初始化安装界面，用于初始化用户UI。并调用PackageInstaller执行安装逻辑；

- InstallAppProcess内注册有广播，当安装完成之后接收广播，更新UI。显示apk安装完成界面；


### Activity的启动 ###



- Activity的启动流程一般是通过调用startActivity或者是startActivityForResult来开始的

- startActivity内部也是通过调用startActivityForResult来启动Activity，只不过传递的requestCode小于0

- Activity的<font color = "red">启动</font>流程涉及到多个进程之间的通讯这里主要是<font color = "red">ActivityThread与ActivityManagerService之间的通讯</font>

- <font color = "red">ActivityThread向ActivityManagerService传递进程间消息通过ActivityManagerNative，ActivityManagerService向ActivityThread进程间传递消息通过IApplicationThread。</font>

- ActivityManagerService接收到应用进程创建Activity的请求之后会执行初始化操作，解析启动模式，保存请求信息等一系列操作。

- ActivityManagerService保存完请求信息之后会将当前系统栈顶的Activity执行onPause操作，并且IApplication进程间通讯告诉应用程序继承执行当前栈顶的Activity的onPause方法；

- ActivityThread接收到SystemServer的消息之后会统一交给自身定义的Handler对象处理分发；

- ActivityThread执行完栈顶的Activity的onPause方法之后会通过ActivityManagerNative执行进程间通讯告诉ActivityManagerService，栈顶Activity已经执行完成onPause方法，继续执行后续操作；

- ActivityManagerService会继续执行启动Activity的逻辑，这时候会判断需要启动的Activity所属的应用进程是否已经启动，若没有启动则首先会启动这个Activity的应用程序进程；

- ActivityManagerService会通过socket与Zygote继承通讯，并告知Zygote进程fork出一个新的应用程序进程，然后执行ActivityThread的mani方法；

- 在ActivityThead.main方法中执行初始化操作，初始化主线程异步消息，然后通知ActivityManagerService执行进程初始化操作；

- ActivityManagerService会在执行初始化操作的同时检测当前进程是否有需要创建的Activity对象，若有的话，则执行创建操作；

- ActivityManagerService将执行创建Activity的通知告知ActivityThread，然后通过反射机制创建出Activity对象，并执行Activity的onCreate方法，onStart方法，onResume方法；

- ActivityThread执行完成onResume方法之后告知ActivityManagerService onResume执行完成，开始执行栈顶Activity的onStop方法；

- ActivityManagerService开始执行栈顶的onStop方法并告知ActivityThread；

- ActivityThread执行真正的onStop方法；


---

### Activity的销毁 ###

所有Activity的生命周期函数的操作,都是通过ActivityThread和ActivityManagerService来交互执行的,交互方式使用Binder子类ActivityManagerNative和IApplicationThread,ActivityThread对于即将进行的操作,通知给AMS后来做准备工作,如:保存信息,进程初始化判断,是否进程存在判断,是否需要创建Activity的判断等.创建进程的操作由AMS通过socket交互的方式通知zygote进程fork新的进程.

---
### Context & 内存泄漏 & GC机制###

	1. context的实现与使用:http://blog.csdn.net/feiduclear_up/article/details/47356289
	2. 垃圾回收机制:
		1. http://www.jianshu.com/p/8c6cf3d7a98a
		2. http://www.jianshu.com/p/214e42fc0d37
		3. http://blog.csdn.net/lu1005287365/article/details/52475957



- `gc` 与`Out of Memory` 的关系

	GC在优先级最低的线程中进行,所以当应用忙时,GC线程就不会被调用。当应用线程在运行,并在运行过程中创建新对象,若这时内存空间不足,JVM就会强制地调用GC线程,以便回收内存用于新的分配。若GC一次之后仍不能满足内存分配的要求,JVM会再进行两次GC作进一步的尝试,若仍无法满足要求,则 JVM将报“out of memory”的错误,Java应用将停止。


- 减少GC开销的方法

		- 不显式调用System.gc();
		- 减少临时变量(不调用后成为垃圾)的使用;
		- 对象不用时置为null;
		- 字符串累加使用StringBuilder/StringBuffer不造成大量内存垃圾;
		- 减少使用包装类;
		- 减少一直占用内存的静态变量;
		- 减少一次性创建或删除(需要回首内存,整理碎片)占用大量内存的对象;














-----


--------

----
----
----
----

----

----
### 返回按键执行操作 ###

android系统的事件分发流程:

		 Native层	--> ViewRootImpl层 
					 --> DecorView层 
					 --> Activity层 
					 --> ViewGroup层 
					 --> View层


#### 1. Native层 ####

- **启动管理服务和管理类**:
	- 在`SystemServer`进程中启动`WindowManagerService`服务，进而启动`InputManagerService`服务。
	- 使用`InputManager`类来管理消息,通过两个线程实现:
		- InputReaderThread线程负责消息的读取
		- InputDispatcherThread则负责消息的预处理和分发到各个应用进程中。
- **获取**:从底层驱动获取各种原始的用户消息;
- **预处理**:对最原始的消息进行预处理:
	- 一方面，将消息转化成系统可以处理的消息事件
	- 一方面，处理一些特殊的事件，比如HOME、MENU、POWER键等处理

- **分发**:Android系统使用**IPC机制的管道**来进行消息的传递，分发到各个应用进程。最终调用的并是`InputEventSender`的子类`ImeInputEventSender`的`onInputEventFinished()`

#### 2. ViewRootImpl层  ####

- native层分发之后最终调用ViewRootImpl$ImeInputStage#onFinishedInputEvent()
	
- → ViewRootImpl$ViewPostImeInputStage#processKeyEvent()

- → PhoneWindow#mDecorView#processKeyEvent(QueuedInputEvent)

#### 3. DecorView层  ####

- dispatchKeyEvent()
- → mWindow.getCallback().dispatchKeyEvent(event) 

#### 4. Activity层  ####

- → activity.dispatchKeyEvent(event)
- → event.dispatch(this, decor != null?decor.getKeyDispatcherState() : null, this)
- → Activity$Callback#onKeyUp(mKeyCode, this)
- → event.onKeyUp(int keyCode, KeyEvent event)
- → Activity#onBackPressed()
- → Activity#finishAfterTransition()
- → Activity#finish()
#### 5. ViewGroup/View层 ####




---
### 触摸事件的分发处理 ###

- ViewRootImpl层将事件交给Activity#dispatchTouchEvent()
- Activity
	- 首先触发按home，back，menu键也会进行的onUserInteraction()
	- 交给PhoneWindow/DecorView去判断是否要拦截`(getWindow().superDispatchTouchEvent(ev))`
		- `DecorView`直接调用`ViewGroup#dispatchTouchEvent(event)`

	- 否则直接自己处理`onTouchEvent(ev)`;




注:

	当ViewGroup对触摸事件进行拦截时,可以使用View#requestDisallowInterceptTouchEvent()对外层控件的拦截关闭.