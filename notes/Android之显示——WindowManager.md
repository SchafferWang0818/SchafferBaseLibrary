---

title: Android之Window,WindowManager,WindowManagerService 
categories: "android 总结"
tags: 
     - android
     - Window
     - WindowManager
     - WindowManagerService 
 
---
# Window,WindowManager,WMS
<font color="blue">
**`Window`的增加 , 删除 , 更新过程整体总结:**

		`WindowManager # addView()/removeView()/updateViewLayout()`
		→`WindowManagerImpl`
		→`WindowManagerGlobal`
		→`ViewRootImpl`添加/删除/更新 `view`,参数
		→<font color="red">**`ViewRootImpl#scheduleTraversals()`更新UI(`measure`,`layout`,`draw`)**</font>
		→<font color="red">**`WindowSession`(`Binder`对象)**</font>
		→`Session`→<font color="red">**`WindowManagerService`** </font></font>


	目录:
		- WindowManagerService
		- Window
		- WindowManager
		- WindowManager$LayoutParams
			- flags
			- type
			- softInputMode 请查看关于键盘的内容 相关文献: http://blog.csdn.net/i_lovefish/article/details/8050025
		- Window创建过程
			- activity
			- dialog
			- Toast

	

---
### WindowManagerService
功能如图所示:

![WMS的作用](http://img.blog.csdn.net/20170401210007652?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWhhb2xweg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

	
	windowManagerService用于将所有的window内容布局、显示、排序、维护在surface中,
	通过设置WindowManager完成对单独系统服务进程中的WMS进行IPC操作。


---
### Window

#### Window层级
window可以分为应用层级Window，子层级Window，系统层级Window。
![Window层级](http://upload-images.jianshu.io/upload_images/1344733-e29b623b5af77c69.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


- 应用层级Window，层级范围1-99,对应一个 Activity;
- 子层级Window，层级范围1000-1999,依附于应用层级Window(需要token),对应 Dialog,PopupWindow等;
- 系统层级Window，层级范围2000-2999,对应Toast,状态栏(Status Bar), 导航栏(Navigation Bar), 壁纸(Wallpaper), 来电显示窗口(Phone)，锁屏窗口(KeyGuard), 信息提示窗口(Toast)， 音量调整窗口，鼠标光标等等;


注意: Window层级可以通过**`WindowManager$LayoutParams#type`**设置.

---
### WindowManagerImpl`(implements WindowManager(extends ViewManager))`

- 内部机制之交互:
1. <font color="red">**①WMI工厂模式提供实例 , 桥接模式WMI委托给WMG;②创建ViewRootImpl并添加View**</font>
	
	WindowManagerImpl通过创建WindowManagerGlobal对象,并将view,viewRootImpl,LayoutParams添加到集合中,将正在被删除的view添加到mDyingViews集合中;
			private final ArrayList<View> mViews = new ArrayList<View>();
		    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();
		    private final ArrayList<WindowManager.LayoutParams> mParams =
		            new ArrayList<WindowManager.LayoutParams>();
		    private final ArraySet<View> mDyingViews = new ArraySet<View>();

2. <font color="red">**`ViewRootImpl#requestLayout()`调用`scheduleTraversals()`刷新UI ; **</font>
 
3. <font color="red">**IPC操作:WindowSession(Binder对象)通过WMS实现添加**</font>
	在 WindowManagerService 内部会为每一个应用保留一个单独的 Session，最终都会通过一个 IPC 过程将操作移交给 WindowManagerService 这个位于 Framework 层的窗口管理服务来处理。

	![IPC流程](http://img.blog.csdn.net/20170402131427522?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWhhb2xweg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

- 内部机制之添加删除更新View流程

	- Window的添加流程:

		- 检查参数是否合法,还需要调整子Window布局参数;
		- 创建ViewRootImpl并将View添加到集合列表中;
			- 集合有以下几种:所有window对应的View集合,所有window对应的RootImpl集合,所有window对应的布局参数集合,所有正在删除view的window对象.一次性添加前三个集合中内容.
		- 使用ViewRootImpl的setView()来更新界面并完成Window添加.底部通过requestLayout()完成异步刷新请求. → 通过WindowSession完成Window的添加,它的类型是IWindowSession,是一个Binder对象,实现类是Session,是一次IPC调用.内部通过WindowManagerService实现Window添加.  → WindowManagerService为每一个应用保留一个单独的Session

	- Window的移除流程:

		- findViewLocked查找待删除的View的索引,遍历数组,
		- 调用removeViewLocked进行删除.
			- removeView:异步删除,由ViewRootImpl的die()发送请求删除消息,view添加到待删除的View列表中.Handler处理消息并调用doDie(),doDie()内部调用了dispatchDetachFromWindow();dispatchDetachFromWindow()内部完成一下事件:
	
				- 清除数据消息和回调;
				- Session通过remove()删除Window  → IPC过程,调用WIndowManagerService的removeWindow().
				- 调用View的dispatchDetachFromWindow()来资源回收,终止动画停止线程.
				- WindowManagerGlobal的doRemoveView()刷新数据,清除三个集合中的关联信息.
				- removeViewImmediate:同步删除,容易发生意外错误.不发送消息直接删除.

	- Window的更新流程:

		- 更新View的LayoutParams
		- 更新ViewRootImpl的LayoutParams(使用setLayoutParams()),通过scheduleTraversals()对View重新测量布局重绘
		- ViewRootImpl通过WindowSession更新Window视图 → IPC过程,WindowManagerService的relayoutWindow()具体实现.



参考文献: http://blog.csdn.net/yhaolpz/article/details/68936932

---
### WindowManager$LayoutParams

#### .flags
- Window的属性,控制Window的显示特性.

	> 对于 `DecorView` ,其 `MeasureSpec` 由窗口的尺寸和其自身的 `LayoutParams` 来共同决定;
	> 对于普通 `View` ,其 `MeasureSpec` 由父容器的`MeasureSpec`和自身的 `LayoutParams` 来共同决定。

- 主要的显示特性:


	- `FLAG_NOT_FOCUSABLE`:不需要获取焦点,不接受各种输入事件与下者一起使用.
	- `FLAG_NOT_TOUCH_MODAL`:将点击事件传递给底层的Window,当前区域以内的单机事件自己处理.
	- `FLAG_SHOW_WHEN_LOCKED`:可以使Window显示在锁屏的界面上.

#### .type
- Window的类型,分为3类:应用Window,子Window,系统Window.且Window是分层的,对应一个Z轴的高度,层级越高,Z的值就越大.

- 系统Window常用的type有:

	- `LayoutParams.TYPE_SYSTEM_OVERLAY`
	- `LayoutParams.TYPE_SYSTEM_ERROR`


---

### Window的创建过程 ###

#### Activity ####
Activity的启动方式:

- **创建Activity: **`ActivityThread#performLaunchActivity()`完成启动,通过类加载器创建Activity对象;
- **`Activity#attach()`→ 关联运行过程中所依赖的上下文环境变量, PolicyManager创建Window(PhoneWindow),使Activity关联Window,实现回调接口: **

	- 创建Window对象是通过PolicyManager#makeNewWindow()工厂方法实现的.
	- Window回调接口的函数包括`onAttachToWindow(),onDetachFromWindow(),dispatchTouchEvent()`等

- **`Activity#setContentView()` → `PhoneWindow#setContentView()`**

	- 创建DecorView
	- `PhoneWindow#generateLayout()`加载`layoutInflater`映射出来的布局View到DecorView的`ContentParent(com.android.internal.R.id.content)`.
	- 回调`Activity#onContentChanged()`通知Activity视图改变.

- `ActivityThread#handleResumeActivity()`调用`Activity#onResume()`,进而调用`makeVisible()`,**将`DecorView`添加至`WindowManager`,设置显示`DecorView`**




#### Dialog ####

	1. PolicyManager#makeNewWindow() 完成 PhoneWindow 的创建
	2. 初始化 DecorView 将 Dialog 的视图添加到 DecorView 中
	3. DecorView 添加到 Window 中并显示

注: `Dialog` 必须使用 `Activity` 的 `context` (<font color="red">**`Activity` 的 `context`存在 token,而其他Context没有**</font>).若要显示系统的Window就可以不需要token,需要设置`WindowManager.LayoutParams.type`的值为`TYPE_SYSTEM_ERROR`,声明权限`SYSTEM_ALERT_WINDOW`即可.

#### Toast ####

* Toast的显示隐藏具有定时取消的功能,需要一个Handler
* 内部存在两个IPC过程,(NMS运行在系统进程中所以属于进程间通信)

	* NotificationManagerService
	* NotificationManagerService回调TN接口

* Toast有两种视图指定方式

	* 系统默认
	* setView()指定

* IPC过程

	* NotificationManagerService处理显示隐藏时需跨进程回调TN接口中的函数,TN运行在Binder线程池中,需要使用Handler切换到当前线程

	* 显示过程中`NMS#enqueueToast()`被调用,传入包名,TN接口对象,Toast时长,封装ToastRecord请求添加到请求队列ArrayList中,对于非系统应用来说,ToastQueue同时只能存在50个,以防止Denial of Service(多次连续弹出Toast,其他应用无机会弹出,系统将拒绝其他应用的Toast服务叫做拒绝服务.).

	* `NMS#showNextToastLocked()`显示Toast,由ToastRecord的回调完成,就是TN接口的Binder,需要跨进程完成,TN的函数会运行在发起Toast请求的应用Binder线程池中.

	* `NMS#scheduleTimeoutLocked()`发送延时消息,具体的延时取决于Toast的duration.TN接口的Binder完成.

注:Toast的显示隐藏都是NotificationManagerService以跨进程的方式调用的,运行在Binder线程池中,使用Handler将执行环境切换到Toast请求所在的线程中,




---
### `LayoutParams # flags/type`备注 ###

#### flags ####

flags|flags含义
:--|:-----
`FLAG_ALLOW_LOCK_WHILE_SCREEN_ON`|允许锁屏
`FLAG_BLUR_BEHIND`|背景**模糊（blur）效果**
`FLAG_DIM_BEHIND`|背景**暗淡（dim）效果**
`FLAG_KEEP_SCREEN_ON`|**高亮（bright）效果**
`FLAG_SCALED`|surface**屏幕缩放**
`FLAG_SECURE`|**不允许截屏**
`FLAG_SHOW_WALLPAPER`|**显示系统墙纸为背景**
<font color="red">**`FLAG_SHOW_WHEN_LOCKED`**</font>|**锁屏显示该window**
`FLAG_DISMISS_KEYGUARD`| 隐藏键盘,除非是安全锁定的键盘.
`FLAG_FORCE_NOT_FULLSCREEN`|**非全屏显示( 默认 )**
`FLAG_FULLSCREEN`|**全屏显示**
`FLAG_LAYOUT_IN_SCREEN`|**占满屏幕，不留边界（border）**
`FLAG_LAYOUT_INSET_DECOR`|配合`FLAG_LAYOUT_IN_SCREEN`使用
`FLAG_LAYOUT_NO_LIMITS`|window可能超出屏幕之外，这时部分内容在屏幕之外。
<font color="red">**`FLAG_NOT_FOCUSABLE`**</font>|window不能获得焦点,按键事件及按钮事件
`FLAG_ALT_FOCUSABLE_IM`|与`FLAG_NOT_FOCUSABLE`配合,与输入法互动有关
`FLAG_NOT_TOUCHABLE`|window不接受触摸屏事件
<font color="red">**`FLAG_NOT_TOUCH_MODAL`**</font>|**`FLAG_NOT_FOCUSABLE`不设置情况下,window获得焦点，穿透event给其他window.**
`FLAG_WATCH_OUTSIDE_TOUCH`|**`FLAG_NOT_TOUNCH_MODAL`发送事件之后仍以`MotionEvent.ACTION_OUTSIDE`形式收到该触摸屏事件**
`FLAG_SPLIT_TOUCH`|当该window在可以接受触摸屏情况下，发送到后面的window的触摸屏可以支持split touch.
`FLAG_TOUCHABLE_WHEN_WAKING`| **手机睡眠时屏幕被按下，该window第一个收到事件**
`FLAG_TURN_SCREEN_ON`|系统将把window显示当做一个用户活动事件**以点亮手机屏幕**。
`FLAG_DITHER`|**开启抖动（dithering）**
`FLAG_HARDWARE_ACCELERATED`|Activity或Dialog的ContentView之前对该window进行硬件加速.mainfest文件`android:hardwareAccelerated = "true"`属性默认开启的。手动设置之后不能在文件中改变.
`FLAG_IGNORE_CHEEK_PRESSES`|当屏幕与人脸正对,为防止误触,当手指释放时将事件作为`MotionEvent.ACTION_CANCEL`传递给应用.


#### type ####

type|type 含义
:--|:-----
`TYPE_BASE_APPLICATION`	|应用层级，所有程序窗口的“基地”窗口，其他应用程序窗口都显示在它上面。
`TYPE_APPLICATION`	|应用层级，普通的应用程序window，token指向某个activity
`TYPE_APPLICATION_STARTING`	|应用层级，用于应用程序启动时所显示的窗口。应用本身不要使用这种类型。它用于让系统显示些信息，直到应用程序可以开启自己的窗口
-----------分界线-------------------|-----------分界线-------------------
`TYPE_APPLICATION_PANEL`	|面板窗口，显示于宿主窗口上层
`TYPE_APPLICATION_MEDIA`	|媒体窗口，例如视频。显示于宿主窗口下层。
`TYPE_APPLICATION_SUB_PANEL`	|应用程序窗口的子面板。显示于所有面板窗口的上层。（GUI的一般规律，越“子”越靠上）
`TYPE_APPLICATION_ATTACHED_DIALOG`	|对话框。类似于面板窗口，绘制类似于顶层窗口，而不是宿主的子窗口。
-----------分界线-------------------|-----------分界线-------------------
`TYPE_STATUS_BAR`	|系统层级，状态栏类型的window。只能有一个状态栏window；它位于屏幕顶端，其他窗口都位于它下方。
`TYPE_SEARCH_BAR`	|系统层级，搜索栏。只能有一个搜索栏；它位于屏幕上方。
`TYPE_PHONE`	|系统层级，电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
`TYPE_SYSTEM_ALERT`	|系统层级，系统提示window,比如电池低的警告。它总是出现在应用程序窗口之上。
`TYPE_KEYGUARD`	|系统层级，锁屏窗口
`TYPE_TOAST`	|系统层级，toast类型的window
<font color="red">`TYPE_SYSTEM_OVERLAY`</font>	|<font color="red">系统层级，系统顶层窗口。显示在其他一切内容之上。此窗口不能获得输入焦点，否则影响锁屏。</font>
`TYPE_PRIORITY_PHONE`	|系统层级，电话优先，当锁屏时显示。此窗口不能获得输入焦点，否则影响锁屏。
`TYPE_SYSTEM_DIALOG`	|系统层级，系统对话框。（例如音量调节框）
`TYPE_KEYGUARD_DIALOG`	|系统层级，锁屏时显示的对话框
<font color="red">**`TYPE_SYSTEM_ERROR`**</font>	|系统层级，系统内部错误提示，显示于所有内容之上，<font color="red">**需要权限`SYSTEM_ALERT_WINDOW`**</font>
`TYPE_INPUT_METHOD`	|系统层级，内部输入法窗口，显示于普通UI之上。应用程序可重新布局以免被此窗口覆盖
`TYPE_INPUT_METHOD_DIALOG`	|系统层级，内部输入法对话框，显示于当前输入法窗口之上
`TYPE_WALLPAPER`	|系统层级，用于墙纸的window
`TYPE_STATUS_BAR_PANEL`	|系统层级，状态栏的滑动面板