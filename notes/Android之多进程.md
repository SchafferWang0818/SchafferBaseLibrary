# <font color="red" size = 6 face="微软雅黑">多进程与IPC </font> #

	相关链接：
			1. http://blog.csdn.net/spencer_hale/article/details/54968092
			2. https://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247485002&idx=1&sn=e07f1949362946ee545079259b6b9014&chksm=96cda707a1ba2e11da927e18c0500bcc9231a82e401069f06048f68b09392c3f332e7893f9a5&mpshare=1&scene=23&srcid=06168CkIdWwm7Ep72uzPbVLx#rd
			3. https://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=400656149&idx=1&sn=122b4f4965fafebf78ec0b4fce2ef62a&mpshare=1&scene=1&srcid=0501f6p8yRsM5qj6OBKEVY1T&key=16e063fbfd27c52cdf5c92791e0542126da55aeb373dcd13df6aa6c417ec61127af2618384b2201ffa7c918e4bbe6780b4d20d3e2ec989af4e2ec3adfda18308cac9706ac4f970ae73fb86211c44b7c2&ascene=0&uin=ODExMTkxNjU%3D&devicetype=iMac+MacBookPro11%2C2+OSX+OSX+10.12.3+build&version=12020510&nettype=WIFI&fontScale=100&pass_ticket=AxhG0QxjCX8weF512sU8ttFb%2B7z%2B8JxvShlgh7diOtM%3D
每一个应用会被分配一个唯一的UID， **<font color=red>具有相同UID的应用才能 共享数据 / 内存数据 / data目录 / 组件信息 等。</font>**


多进程的用处:

	1. 常驻后台做守护进程，推送服务;
	2. 多进程可获得多份内存空间(最早版本单个应用可以使用16MB);
	3. 对于webview，图库等，由于存在内存系统泄露或者占用内存过多的问题，我们可以采用单独的进程。

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
根据AIDL自动生成的java 代码中, 使用代理模式,`Proxy`类使用`IBinder # transact()`标记函数参数,返回值等内容.

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
```
	    <permission
	        android:name="com.schaffer.base.permission.BIND_TEST"
	        android:protectionLevel="normal" />

```
服务端: **可以使用两种验证方式判断是否允许其他进程客户端绑定Service.**

```
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

```

#### AIDL java代码的生成 ####

- AIDL: 初始自定义内容

```
	// >>> main/aidl/com.schaffer.base/test/Book.aidl
	package com.schaffer.base.test;
	parcelable Book;	//Parcelable java实现类

	// >>> main/aidl/com.schaffer.base/IMyAidlInterface.aidl
	package com.schaffer.base;
	
	interface IMyAidlInterface {
	     String back(int type);
	}

	// >>> main/aidl/com.schaffer.base/DefineInterface.aidl
	package com.schaffer.base;
	//导入需要的 aidl 或 Parcelable
	import com.schaffer.base.IMyAidlInterface;
	import com.schaffer.base.test.Book;
	
	interface DefineInterface {
	
	    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
	            double aDouble, String aString);
	            
	     void myInterface(in IMyAidlInterface inter);
	     List<Book> getList();
	     void setBinderDeath(IBinder binder);
	}


```
- 根据aidl 自动生成的 java 代码,**服务端使用生成的抽象 `Stub` ,完成自定义函数的重写 , 实现 具体实现功能; 客户端 通过静态函数`Stub#asInterface `判断`IBinder`类型并返回具体处理代理`Proxy(外部aidl 生成接口的实现类)`类对象,调用其函数代理给服务端实现.**

```
//project\app\build\generated\source\aidl\mainflavor\debug\com\schaffer\base\DefineInterface.java
package com.schaffer.base;

public interface DefineInterface extends android.os.IInterface {

    public static abstract class Stub extends android.os.Binder implements com.schaffer.base.DefineInterface {
        private static final java.lang.String DESCRIPTOR = "com.schaffer.base.DefineInterface";


        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static com.schaffer.base.DefineInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.schaffer.base.DefineInterface))) {
                return ((com.schaffer.base.DefineInterface) iin);
            }
            return new com.schaffer.base.DefineInterface.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_basicTypes: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    long _arg1;
                    _arg1 = data.readLong();
                    boolean _arg2;
                    _arg2 = (0 != data.readInt());
                    float _arg3;
                    _arg3 = data.readFloat();
                    double _arg4;
                    _arg4 = data.readDouble();
                    java.lang.String _arg5;
                    _arg5 = data.readString();
                    this.basicTypes(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_myInterface: {
                    data.enforceInterface(DESCRIPTOR);
                    com.schaffer.base.IMyAidlInterface _arg0;
                    _arg0 = com.schaffer.base.IMyAidlInterface.Stub.asInterface(data.readStrongBinder());
                    this.myInterface(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getList: {
                    data.enforceInterface(DESCRIPTOR);
                    java.util.List<com.schaffer.base.test.Book> _result = this.getList();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_setBinderDeath: {
                    data.enforceInterface(DESCRIPTOR);
                    android.os.IBinder _arg0;
                    _arg0 = data.readStrongBinder();
                    this.setBinderDeath(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.schaffer.base.DefineInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(anInt);
                    _data.writeLong(aLong);
                    _data.writeInt(((aBoolean) ? (1) : (0)));
                    _data.writeFloat(aFloat);
                    _data.writeDouble(aDouble);
                    _data.writeString(aString);
                    mRemote.transact(Stub.TRANSACTION_basicTypes, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void myInterface(com.schaffer.base.IMyAidlInterface inter) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((inter != null)) ? (inter.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_myInterface, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public java.util.List<com.schaffer.base.test.Book> getList() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List<com.schaffer.base.test.Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getList, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(com.schaffer.base.test.Book.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void setBinderDeath(android.os.IBinder binder) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    mRemote.transact(Stub.TRANSACTION_setBinderDeath, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_basicTypes = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_myInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_setBinderDeath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    }

    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException;

    public void myInterface(com.schaffer.base.IMyAidlInterface inter) throws android.os.RemoteException;

    public java.util.List<com.schaffer.base.test.Book> getList() throws android.os.RemoteException;

    public void setBinderDeath(android.os.IBinder binder) throws android.os.RemoteException;
}


```

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
[点击这里查看socket通信](https://github.com/SchafferWang0818/SchafferBaseLibrary/blob/master/notes/Android%E4%B9%8B%E9%80%9A%E4%BF%A1%E2%80%94%E2%80%94Socket%E9%80%9A%E4%BF%A1.md)

---
### 使用ContentProvider ###
[<font color=red>**查看ContentProvider**</font>](https://github.com/SchafferWang0818/SchafferBaseLibrary/blob/master/notes/Android%E4%B9%8B%E5%9B%9B%E5%A4%A7%E7%BB%84%E4%BB%B6%E2%80%94%E2%80%94ContentProvider.md)
