在Thread或Runnable接口中的run方法首句加入

`Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);` 
//设置线程优先级为后台，这样当多个线程并发后很多无关紧要的线程分配的CPU时间将会减少，有利于主线程的处理，相关的Thread优先级.

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