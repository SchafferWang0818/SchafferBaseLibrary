# 多线程 & ANR

### 多线程


#### 线程优先级
在`Thread`或`Runnable`接口中的`run()`首句加入`Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);` 
//设置线程优先级为后台，这样当多个线程并发后很多无关紧要的线程分配的CPU时间将会减少，有利于主线程的处理

	Android平台专有的定义罗列有以下几种:
	int THREAD_PRIORITY_AUDIO //标准音乐播放使用的线程优先级
	int THREAD_PRIORITY_BACKGROUND //标准后台程序
	int THREAD_PRIORITY_DEFAULT // 默认应用的优先级
	int THREAD_PRIORITY_DISPLAY //标准显示系统优先级，主要是改善UI的刷新
	int THREAD_PRIORITY_FOREGROUND //标准前台线程优先级
	int THREAD_PRIORITY_LESS_FAVORABLE //低于favorable
	int THREAD_PRIORITY_LOWEST //有效的线程最低的优先级
	int THREAD_PRIORITY_MORE_FAVORABLE //高于favorable
	int THREAD_PRIORITY_URGENT_AUDIO //标准较重要音频播放优先级
	int THREAD_PRIORITY_URGENT_DISPLAY //标准较重要显示优先级，对于输入事件同样适用。



---

### ANR 

ANR现象:
	1. Activity在5s内没有响应按下事件和触摸事件;
	2. 广播在10s之内没有执行完毕;
	3. 服务在20s之内没有处理完成(小概率)

避免ANR:
	1. 避免Activity耗时操作,使用多线程异步任务;
	2. 避免广播的耗时操作,适当启动其他进程Service或Service内部线程来执行任务;
	3. 避免广播启动Activity,使用Notification的形式来实现;



---