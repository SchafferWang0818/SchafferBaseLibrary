# Surface & SurfaceView #


---
## Surface ##





---




## SurfaceView ##

###和View的区别
1. View通过刷新来重绘视图，Android系统通过发出VSYNC信号来进行屏幕的重绘，刷新的时间间隔为16ms,超出就会卡顿;
2. SurfaceView继承于View,拥有独立的绘制表面,不与宿主窗口共享一个绘制表面,单独在一个线程中绘制,不占用主线程的资源,主要用到的位置是游戏和视频播放,直播;
	* 每一个SurfaceView在SurfaceFlinger服务中还对应有一个独立的Layer或者LayerBuffer，用来单独描述它的绘图表面，以区别于它的宿主窗口的绘图表面。
3. SurfaceView有两个子类GLSurfaceView和VideoView;
4. 其他区别:
	
		* View--主动更新，SurfaceView--被动更新，例如频繁地刷新;
		* View--主线程，而SurfaceView--子线程刷新页面;
		* SufaceView实现双缓冲机制;
		原文链接：http://www.jianshu.com/p/15060fc9ef18




---




## SurfaceView基本使用 ##
1. 创建
		
		* extends SurfaceView implements SurfaceHolder.Callback
		* 实现函数有:
			* 创建--surfaceCreated(SurfaceHolder holder)
			* 更新--surfaceChanged(SurfaceHolder holder, int format, int width, int height)
			* 销毁--surfaceDestroyed(SurfaceHolder holder) 
		* SurfaceHolder.CallBack还有一个子Callback2接口，
		  里面添加了一个surfaceRedrawNeeded (SurfaceHolder holder)方法,
		  当需要重绘SurfaceView中的内容使用.
		
2. 初始化
		
		* 初始化可以控制大小,格式,监控或改变SurfaceView的surfaceHolder;
		* 设置是否可以获取焦点和触摸获取焦点信息,是否保持屏幕长亮;(一般上对EditText设置,设置了focusableInTouchMode(true)的editText才能在弹出键盘的时候得到输入的内容)
		* 代码如下
			mSurfaceHolder = getHolder();//得到SurfaceHolder对象
        	mSurfaceHolder.addCallback(this);//注册SurfaceHolder
        	setFocusable(true);
        	setFocusableInTouchMode(true);
        	this.setKeepScreenOn(true);//保持屏幕长亮		


3. 使用
		
		* 在开启的线程中使用SurfaceHolder#lockCanvas()获取canvas进行绘制;
		* 创建之后循环绘制,使用unlockCanvasAndPost(mCanvas)进行画布的提交;
		* canvas的擦除内容,需要使用drawColor();
	




