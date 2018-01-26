# Surface & SurfaceView #


---
## Surface ##
- surface对应一个图层,原始图像缓冲区(由屏幕图像合成器管理)的句柄,
- surface可以理解为用于显示的一段内存;
- surface持有一定量的像素点,每一个Window都对应一个surface来将自己的内容进行绘制;
- surface的双缓冲区的作用: 在绘制16ms后显示的内容的同时,可以将当前时刻的所有像素根据z轴上的位置渲染到屏幕上.

简要理解:
	
```java
1. updateWindow()中IWindowSession(≈ViewRootImpl)创建surface
2. 跨进程调用WindowManagerService重绘surface并更新(mSurface.transferFrom(mNewSurface))
3. 返回surface给SurfaceView
```


---




## SurfaceView ##
- View适用于对于用户的操作做出一定的响应,较为被动;
- SurfaceView较为主动,不会阻塞UI,需要不同的线程和surface来动态更新;


### 和View的区别
1. View通过刷新来重绘视图，Android系统通过发出VSYNC信号来进行屏幕的重绘，刷新的时间间隔为16ms,超出就会卡顿;
2. SurfaceView继承于View,拥有独立的绘制表面,不与宿主窗口共享一个绘制表面,单独在一个线程中绘制,不占用主线程的资源,主要用到的位置是游戏和视频播放,直播;
	* 每一个SurfaceView在SurfaceFlinger服务中还对应有一个独立的Layer或者LayerBuffer，用来单独描述它的绘图表面，以区别于它的宿主窗口的绘图表面。
3. SurfaceView有两个子类GLSurfaceView和VideoView;
4. [其他区别](http://www.jianshu.com/p/15060fc9ef18):
	- View--主动更新，SurfaceView--被动更新，例如频繁地刷新;
	- View--主线程，而SurfaceView--子线程刷新页面;
	- SufaceView实现双缓冲机制;




### View的缺陷

- View没有双缓冲机制
- View更新图像必须重新绘制
- View的更新必须在定义线程中进行,由于使用的是UI中公用的surface




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
		
	- 初始化可以控制大小,格式,监控或改变SurfaceView的surfaceHolder;
	- 设置是否可以获取焦点和触摸获取焦点信息,是否保持屏幕长亮;(一般上对EditText设置,设置了focusableInTouchMode(true)的editText才能在弹出键盘的时候得到输入的内容)
	```java
		mSurfaceHolder = getHolder();//得到SurfaceHolder对象
		mSurfaceHolder.addCallback(this);//注册SurfaceHolder
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);//保持屏幕长亮		
	```

3. 使用
```java	
	* 在开启的线程中使用SurfaceHolder#lockCanvas()获取canvas进行绘制;
	* 创建之后循环绘制,使用unlockCanvasAndPost(mCanvas)进行画布的提交;
	* canvas的擦除内容,需要使用drawColor();
```	


### SurfaceHolder

- 与SurfaceView结合使用
- 调用SurfaceView的getHolder()获得关联的SurfaceHolder
- SurfaceHolder设置监听Callback
	- surfaceChanged(SurfaceHolder holder, int format
		, int width, int height)
		- 格式大小发生改变时被调用
	- surfaceCreated(SurfaceHolder holder)
		- 创建时被调用
	- surfaceDestroyed(SurfaceHolder holder)
		- 销毁前被调用
- getSurface()
	- 对surface持有管理 
- 更新参数得到新的surface的函数
	- setFixedSize()
	- setSizeFromLayout()
	- setFormat()

### SurfaceView的绘制过程

- 绘制之前需要**锁定绘制区域**选取来得到Canvas:
		
		canvas = holder.lockCanvas(new Rect(lt_x,lt_y,rb_x,rb_y));
		
		* 注:lockCanvas()无参时绘制所有区域,有参绘制效率更高;

- 绘制结束释放Canvas提交修改内容

		holder.unlockCanvasAndPost(canvas);


### 其他理解

	1. SurfaceView所做的全部就是要求WindowManager创建一个window，并告诉Window Manager所创建的window的Z轴顺序（Z-order）
	2. 这个Z轴顺序可以帮助Window Manager决定将新建的window置于SurfaceView所属window的前面还是后面。
	3. WindowManager会将新建的window放置到SurfaceView在所属window中的位置。如果新建window在SurfaceView所属window后面，SurfaceView会将它在所属window中占据的部分变透明，以便让后面的window显示出来。


SurfaceView和DecorView根节点视图一样,能对WMS可见.虽然在Server端（WMS和SF）中，但是与宿主窗口是分离的.在WMS中有对应的WindowState,对应SurfaceFlinger的Layer(图层).

	- 优点:Surface可以放到单独的线程做,渲染自己对应的GL Context.
	- 缺点:不在View树型结构中,不受View的属性控制,不能进行各种变换,一些属性也无法使用.


---

