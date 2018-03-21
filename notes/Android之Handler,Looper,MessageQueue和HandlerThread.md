---

title: Android之 Handler,Looper,MessageQueue和HandlerThread
categories: "android 总结"
tags: 
	- Handler
	- Looper
	- MessageQueue
	- HandlerThread

---
# Android开发笔记之Handler,Looper,MessageQueue和HandlerThread #

	1. 目录:
		1. Handler,Looper,MessageQueue
		2. HandlerThread (extends Thread) 
		3. Looper与ThreadLocal

	2. 参考文章:
		- Handler,Looper,MessageQueue
			1. http://blog.csdn.net/lmj623565791/article/details/38377229
			2. http://blog.csdn.net/guolin_blog/article/details/9991569
		- HandlerThread
			http://blog.csdn.net/lmj623565791/article/details/47079737/
		- Looper与ThreadLocal
			https://mp.weixin.qq.com/s/SVSJ7imNj6lsbuTE9ix2Zg 
	
			
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

---

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

---
###  Looper与ThreadLocal

- `ThreadLocal # get()`
	ThreadLocal 通过当前线程来获取Map中的值.不同的线程设置 / 获得的值不同.
	```java
	    /**
	     * ThreadLocal.java
	     * Returns the value in the current thread's copy of this
	     * thread-local variable.  If the variable has no value for the
	     * current thread, it is first initialized to the value returned
	     * by an invocation of the {@link #initialValue} method.
	     *
	     * @return the current thread's value of this thread-local
	     */
	    public T get() {
	        Thread t = Thread.currentThread();
	        ThreadLocalMap map = getMap(t);
	        if (map != null) {
	            ThreadLocalMap.Entry e = map.getEntry(this);
	            if (e != null)
	                return (T)e.value;
	        }
	        return setInitialValue();
	    }
	```

	

 - `Looper # prepare()`
	```java
	static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>(); 

	private static void prepare(boolean quitAllowed) { 
	    if (sThreadLocal.get() != null) { 
	        throw new RuntimeException("Only one Looper may be created per thread"); 
	    } 
	    sThreadLocal.set(new Looper(quitAllowed)); 
	}
	
	private Looper(boolean quitAllowed) {
	        mQueue = new MessageQueue(quitAllowed);
	        mThread = Thread.currentThread();
	}
	```

	Looper 内部封装 静态的ThreadLocal<Looper>,对于不同的线程, `prepare()`将创建不同的Looper .
	- 主线程`main(String[])`主动调用`Looper # prepareMainLooper()`间接调用 ` Looper # prepare() `,并加锁赋值给` Looper.sMainLooper `;
	- 其他线程
		1. 需要手动调用`Looper # prepare()`创建`Looper`与`MessageQueue`,
		2. `Handler`构造中调用`Looper.myLooper()`正是获得`Handler`当前线程的`looper`并绑定`MessageQueue`,
		3. `Handler # sendMessage(msg)`最终调用`Handler # enqueueMessage(...)`将`Handler`作为target
		4. 在`Looper.loop()`中将`message`再一一传递给target也就是`Handler`.
		```java
		    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
		        msg.target = this;
		        if (mAsynchronous) {
		            msg.setAsynchronous(true);
		        }
		        return queue.enqueueMessage(msg, uptimeMillis);
		    }
		```

 - `Looper # loop()`	

	```java
		/**
	     * Run the message queue in this thread. Be sure to call
	     * {@link #quit()} to end the loop.
	     */
	    public static void loop() {
	    	// 获得线程中的Looper
	        final Looper me = myLooper();
	        if (me == null) {
	            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
	        }
	        // 获得线程Looper对应的MessageQueue
	        final MessageQueue queue = me.mQueue;
	
	        // Make sure the identity of this thread is that of the local process,
	        // and keep track of what that identity token actually is.
	        Binder.clearCallingIdentity();
	        final long ident = Binder.clearCallingIdentity();
			//线程循环操作
	        for (;;) {
	        	// 获得接下来的 Message
	            Message msg = queue.next(); // might block
	            if (msg == null) {
	                // No message indicates that the message queue is quitting.
	                return;
	            }
	
	            // This must be in a local variable, in case a UI event sets the logger
	            final Printer logging = me.mLogging;
	            if (logging != null) {
	                logging.println(">>>>> Dispatching to " + msg.target + " " +
	                        msg.callback + ": " + msg.what);
	            }
	
	            final long traceTag = me.mTraceTag;
	            if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
	                Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
	            }
	            try {
	            	// 将Message 回传给 Handler
	                msg.target.dispatchMessage(msg);
	            } finally {
	                if (traceTag != 0) {
	                    Trace.traceEnd(traceTag);
	                }
	            }
	
	            if (logging != null) {
	                logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
	            }
	
	            // Make sure that during the course of dispatching the
	            // identity of the thread wasn't corrupted.
	            final long newIdent = Binder.clearCallingIdentity();
	            if (ident != newIdent) {
	                Log.wtf(TAG, "Thread identity changed from 0x"
	                        + Long.toHexString(ident) + " to 0x"
	                        + Long.toHexString(newIdent) + " while dispatching to "
	                        + msg.target.getClass().getName() + " "
	                        + msg.callback + " what=" + msg.what);
	            }
	
	            msg.recycleUnchecked();
	        }
	    }
	```
---







	