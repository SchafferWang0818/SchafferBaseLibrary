# Android开发笔记之Handler,Looper,MessageQueue和HandlerThread #



	参考文章:
		- Handler,Looper,MessageQueue
			1. http://blog.csdn.net/lmj623565791/article/details/38377229
			2. http://blog.csdn.net/guolin_blog/article/details/9991569
		- HandlerThread
			http://blog.csdn.net/lmj623565791/article/details/47079737/
			
### Handler,Looper,MessageQueue		

1. 流程

	1. Activity启动时,UI线程中调用Looper.prepare(),Looper.loop();

		    public static void main(String[] args) {  
		        SamplingProfilerIntegration.start();  
		        CloseGuard.setEnabled(false);  
		        Environment.initForCurrentUser();  
		        EventLogger.setReporter(new EventLoggingReporter());  
		        Process.setArgV0("<pre-initialized>");  
		        Looper.prepareMainLooper();  //looper初始化,调用Looper.prepare();
		        ActivityThread thread = new ActivityThread();  
		        thread.attach(false);  
		        if (sMainThreadHandler == null) {  
		            sMainThreadHandler = thread.getHandler();  
		        }  
		        AsyncTask.init();  
		        if (false) {  
		            Looper.myLooper().setMessageLogging(new LogPrinter(Log.DEBUG, "ActivityThread"));  
		        }  
		        Looper.loop();  
		        throw new RuntimeException("Main thread loop unexpectedly exited");  
		    }  		


	2. Looper.prepare():线程中保存一个Looper实例,并在实例中保存一个MessageQueue对象,线程中prepare()只能调用一次,消息队列也只能有一个;
	3. Looper.loop()使当前线程进入循环模式,不断从MessageQueue中读取消息并得到msg.target,使target调用dispatchMessage(msg);
	4. Handler构造函数中获取一个Looper实例,并与Looper实例的MessageQueue相关联;
	5. Handler的sendMessage()将handler.this赋值给msg.target,调用自身的dispatchMessage(msg)函数;
	6. **dispatchMessage(msg)中调用空函数handleMessage(Message msg)或handleCallback(msg)来处理Message内容**.
	7. **handleCallback(msg)直接调用Callback的run(),所以handler#post(r)系列中的Runnable内容都是在handler所在线程中进行的**.




2. Handler#post(Runnable r)系列


	1. 内部调用sendMessageDelayed(getPostMessage(r), 0);
	2. getPostMessage(r)将runnable赋值给msg.callback;[2]

		注:[2]Message.obtain()Message内部维护了一个Message池用于Message的复用，避免使用new重新分配内存.


3. MessageQueue

		- MessageQueue的入队其实就是将所有的消息按时间来进行排序,根据时间的顺序调用msg.next()从而为每一个消息指定它的下一个消息是什么;
		- sendMessageAtFrontOfQueue()是发送消息到队首;
		- Message msg = queue.next();在Looper.looper()中用于消息出队;


4. View#post(Runnable r)

	内部调用handler#post(r);



### HandlerThread (extends Thread) ###

	/**
	 * Handy class for starting a new thread that has a looper. The looper can then be 
	 * used to create handler classes. Note that start() must still be called.
	 */
	HandlerThread是启动一个自带Looper轮询器的线程处理类,
	可以用于创建Handler类,start()必须被调用.


1. HandlerThread的函数:

	- `HandlerThread(String name,int priority)`
		- 线程名称
		- 线程优先级,默认`Process.THREAD_PRIORITY_DEFAULT`;	
	- `void onLooperPrepared()`
		- Looper轮询之前需要做的事需要重写当前函数
	- `Looper getLooper()`
		- 当前线程存活状态下返回当前线程的looper,线程若已经开始而没有looper时,会等待直到looper被初始化;

	- `boolean quitSafely()`
	- `boolean quit()`

		- (不)安全地退出线程消息轮询;







	