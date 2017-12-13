# <font color="red" size = 6 face="微软雅黑">多进程与IPC </font> #

	相关链接：
			1. http://blog.csdn.net/spencer_hale/article/details/54968092

每一个应用会被分配一个唯一的UID， **<font color=red>具有相同UID的应用才能 共享数据 / 内存数据 / data目录 / 组件信息 等。</font>**


	多进程的用处:

		1. 常驻后台做守护进程，收发消息;
		2. 多进程可获得多份内存空间(最早版本单个应用可以使用16MB);

	多进程弊端：

		1. 耗电；
		2. 调试断点问题；
		3. 文件共享，内存对象共享问题；
		4. Application多次重建问题；
		4. 交互复杂性；

	指定多进程的方式:
	
		1. 四大组件指定 → android:process
			1. 使用":"声明的进程属于私有进程,其他应用组件不可在同一进程;
			2. 不使用":"声明的进程可以通过相同的ShareUID和签名才能在同一进程;
		2. JNI native 层 fork 新进程


	跨进程通信的方式有:
		
		- Intent/Bundle传递数据;
		- 共享文件和SharePreferences(私有进程可以访问);
		- Binder机制(Messenger/AIDL机制);
		- ContentProvider;
		- Socket通信;


名称 | 优点 | 缺点/注意点 |设用场景
:-:|-|:-:|:-:
Bundle|简单易用|只能传输Bundle支持的数据类型|四大组件间的进程通信
文件共享|简单易用|不适合高并发的情况，<br>并且无法做到进程间的即时通讯	|无并发访问情况下，<br>交换简单的<br>数据实时性不高的情况
AIDL|支持一对多并发通信，<br>支持实时通讯|需要处理好线程同步|一对多通信且有RPC需求
Messenger|支持一对多串行通信，<br>支持实时通讯|不能很好处理高并发情况，<br>不支持RPC,数据通过Message进行传输，<br>因此只能传输Bundle支持的数据类型|低并发的一对多即时通信,<br>无RPC需求，或者无需返回结果的RPC需求
ContentProvider|在数据源访问方面功能强大，<br>支持一对多并发数据共享，<br>可通过Call方法扩展其他操作|主要提供数据源的CRUD操作|一对多的进程间数据共享
Socket|功能强大，<br>可以通过网络传输字节流，<br>支持一对多并发实时通讯|实现细节有点繁琐，不支持直接的RPC|网络数据交换

注：RPC(调用远程服务中的方法)


---
### Binder & IBinder
从框架层角度: **Binder是`ServiceManager(c++)` 连接各种`Manager`和`ManagerService`的桥梁 ; **
从应用层角度: Binder是客户端和服务端通信的媒介。

 
工作原理:
	
	asInterface(IBinder binder):服务段Binder转换成客户端需要的AIDL接口类型,同进程就返回的是Stub本身,
	反之就是系统分装后的Stub.proxy对象;
		
	onTransact(int code,Pracel data,Pracel reply,int flags):服务端通过code判断客户端请求的目标方法,
	然后从data中取出方法所需要的参数执行,然后给reply写入返回值,当前方法返回false客户端就请求失败.
	
	客户端发生远程请求时,线程挂起一直到得到响应或者被杀死,Binder得到客户端请求通过inTransact()将data参数写入到目标函数,
	目标函数执行完毕将数据返回给客户端.

```

流程分析:
	客户端请求Binder并挂起等待
	 → Binder将数据写入data →  onTransact(transact(根据code取出方法所需要参数传入)
	 → 线程池中完成操作 → 将返回内容写入reply)
	 → 返回Binder强转类型(失败成功看onTransact返回值)
	 → 返回数据给客户端

```

![image](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512390149844&di=fa72137446d16e811330d3dcb956b7ca&imgtype=0&src=http%3A%2F%2Fwww.th7.cn%2Fd%2Ffile%2Fp%2F2016%2F09%2F22%2F6c542eae3fcf2879e6900b41d1157958.jpg)




---
### AIDL ###

AIDL支持的数据类型:

	1. 基本数据类型
	2. String,CharSequeue
	3. ArrayList
	4. HashMap
	5. Parcelable
	6. 可打包(实现Parceable)的AIDL对象(AIDL对象必须手动导入文件位置)

<font color = red  face="微软雅黑">
注 :  
- **当AIDL文件要使用实现Parcelable的类时,需要同时在AIDL文件夹下与Java文件夹下同位置创建Java类与AIDL文件** ;<font color = black>  例如:

		//com.schaffer.base.test.Book.java

		package com.schaffer.base.test;
		public class Book implements Parcelable {
			//...
		}

		//com.schaffer.base.test.Book.aidl
		package com.schaffer.base.test;
		parcelable Book;
</font >
- 由于进程间对象不可以共享,<font color = black>跨进程移除监听不可以直接移除,</font>需要使用`RemoteCallbackList`来移除`Listener`.

		内部使用ArrayMap<IBinder,Callback>;
		获取集合中的内容需要使用 beginBroadcast/finishBroadcast()进行遍历或者其他操作;	
		解除注册时,遍历服务器所有的listener,将和解除注册的Listener的binder对应的删除.

</font >



#### ServiceConnection
`onServiceConnected()` & `onServiceDisconnected()` 均运行在UI线程.不可做耗时操作. 

#### Binder重连
Binder是可能意外死亡的，往往是由于服务端进程意外停止了，这时我们需要重新连接服务，有两种方式： 

- 给Binder设置死亡代理,监听`binderDied`方法回调(运行在客户端Binder线程池) 

		// 服务端
		Binder binder = new DefineInterface.Stub() {
	        @Override
	        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
	
	        }
	
	        //...
	
	        @Override
	        public void setBinderDeath(IBinder binder) throws RemoteException {
	            binder.linkToDeath(new MyDeathRecipient(), 0);
	        }
	    };

			
	    public static class MyDeathRecipient implements IBinder.DeathRecipient {
	
	        @Override
	        public void binderDied() {
	            Log.d("TAG", "binder 离线");
	        }
	    }

		

		//客户端
		@Override
	    public void onCreate(@Nullable Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        binder = new Binder();
	        bindService(new Intent(this,TestAidlService.class),connection,BIND_AUTO_CREATE);
	    }

		public ServiceConnection connection = new ServiceConnection() {
	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            DefineInterface define = DefineInterface.Stub.asInterface(service);
	
	            try {
	                define.setBinderDeath(binder);
	            } catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	
	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	        }
	    };

- 在`onServiceDisConnected`中重连服务(运行在主线程)



#### AIDL进程交互所需权限 ####

客户端: **添加自定义权限**

	    <permission
	        android:name="com.schaffer.base.permission.BIND_TEST"
	        android:protectionLevel="normal" />

服务端: **可以使用两种验证方式判断是否允许其他进程客户端绑定Service.**

	    @Nullable
	    @Override
	    public IBinder onBind(Intent intent) {
			/* 判断权限是否被允许 */
	        if (checkCallingOrSelfPermission("com.schaffer.base.permission.BIND_TEST") == PackageManager.PERMISSION_DENIED) {
	            return null;
	        }
	        return binder;
	    }
	
	    Binder binder = new DefineInterface.Stub() {
	        @Override
	        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
	
	        }
			
			//...

	        @Override
	        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
	            /* 判断权限是否被允许 */
	            if (checkCallingOrSelfPermission("com.schaffer.base.permission.BIND_TEST") == PackageManager.PERMISSION_DENIED) {
	                return false;
	            }
	            /* 判断包名是否被允许 */
	            String pn = null;
	            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
	            if (packages != null && packages.length > 0) {
	                pn = packages[0];
	            }
	            if (!pn.contains("com.schaffer")) {
	                return false;
	            }
	            return super.onTransact(code, data, reply, flags);
	        }
	    };


---
### Messenger & Message ###
底层实现为`AIDL`.
`Messenger`**串行处理**进程间通讯.
`Messenger`中进行数据传递必须**将数据放进`Message`**.
`Messenger`**接收数据离不开`Handler`接收并处理**.

**Message中可以用来传递数据的内容有:` what , arg1 , arg2 , object , data(Bundle) , replyTo .`**

	   object	 : Android 2.2 之前不支持跨进程传输; Android 2.2 之后只支持跨进程传输 
					Parcelable 实现类;
	   Bundle	 : 可以支持大量的数据类型;
	   replyTo	: 存储的是接收回复Message的Messenger信使;
---
### Socket ###








---
### 使用ContentProvider ###
[<font color=red>**查看ContentProvider**</font>](https://github.com/SchafferWang0818/SchafferBaseLibrary/blob/master/notes/Android%E4%B9%8B%E5%9B%9B%E5%A4%A7%E7%BB%84%E4%BB%B6%E2%80%94%E2%80%94ContentProvider.md)
