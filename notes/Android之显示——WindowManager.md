# Window,WindowManager,WMS


	目录:
		- WindowManagerService
		- Window
		- WindowManager
		
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


- 应用层级Window，层级范围1-99,对应一个 Acitivity;
- 子层级Window，层级范围1000-1999,依附于应用层级Window(需要token),对应 Dialog,PopupWindow等;
- 系统层级Window，层级范围2000-2999,对应Toast,状态栏(Status Bar), 导航栏(Navigation Bar), 壁纸(Wallpaper), 来电显示窗口(Phone)，锁屏窗口(KeyGuard), 信息提示窗口(Toast)， 音量调整窗口，鼠标光标等等;


注意: Window层级可以通过**`WindowManager$LayoutParams#type`**设置.

---
### WindowManagerImpl`(implements WindowManager(extends ViewManager))`

- 内部机制:
1. WindowManagerImpl通过创建WindowManagerGlobal对象,并将view,viewRootImpl,LayoutParams添加到集合中,将正在被删除的view添加到mDyingViews集合中;

			private final ArrayList<View> mViews = new ArrayList<View>();
		    private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();
		    private final ArrayList<WindowManager.LayoutParams> mParams =
		            new ArrayList<WindowManager.LayoutParams>();
		    private final ArraySet<View> mDyingViews = new ArraySet<View>();


2. `ViewRootImpl#requestLayout()`调用`scheduleTraversals()`刷新UI;
3. 在 WindowManagerService 内部会为每一个应用保留一个单独的 Session，最终都会通过一个 IPC 过程将操作移交给 WindowManagerService 这个位于 Framework 层的窗口管理服务来处理。

	![IPC流程](http://img.blog.csdn.net/20170402131427522?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWhhb2xweg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)



http://blog.csdn.net/yhaolpz/article/details/68936932


---

