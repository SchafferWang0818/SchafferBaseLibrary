# <font color="red" size = 6 face="微软雅黑">多进程与IPC </font> #

每一个应用会被分配一个唯一的UID， **<font color=red>具有相同UID的应用才能 共享数据 / 内存数据 / data目录 / 组件信息 等。</font>**


	多进程的用处:

		1. 特殊原因需要运行在特殊进程中;
		2. 多进程可获得多份内存空间(最早版本单个应用可以使用16MB);

	指定多进程的方式:
	
		1. 四大组件指定 → android:process
			1. 使用":"声明的进程属于私有进程,其他应用组件不可在同一进程;
			2. 不使用":"声明的进程可以通过相同的ShareUID和签名才能在同一进程;
		2. JNI native 层 fork 新进程


	跨进程通信的方式有:
		
		- Intent传递数据;
		- 共享文件和SharePreferences;(":"私有进程)
		- Binder机制(Messenger机制与AIDL机制);
		- Socket通信;

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


**
流程分析:
	客户端请求Binder并挂起等待
	 → Binder将数据写入data →  onTransact(transact(根据code取出方法所需要参数传入)
	 → 线程池中完成操作 → 将返回内容写入reply)
	 → 返回Binder强转类型(失败成功看onTransact返回值)
	 → 返回数据给客户端
**

![image](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512390149844&di=fa72137446d16e811330d3dcb956b7ca&imgtype=0&src=http%3A%2F%2Fwww.th7.cn%2Fd%2Ffile%2Fp%2F2016%2F09%2F22%2F6c542eae3fcf2879e6900b41d1157958.jpg)


AIDL支持的数据类型:
	1. 基本数据类型
	2. String,CharSequeue
	3. ArrayList
	4. HashMap
	5. Parcelable
	6. AIDL--->可打包(实现Parceable)的AIDL对象

	RemoteCallbackList:进程间通信过程中系统专门用于提供删除Listener的接口。内部有Map专门用来保存所有AIDL的回调 
	key是IBinder类型，value是Callback类型.解除注册时,遍历服务器所有的listener,对应的删除.
	beginBroadcast()获取有多少listener 必须要和finishBroadcast一起使用.


---

### Messenger ###
底层实现为AIDL.
Messenger中进行数据传递必须将数据放进Message.

**Message中可以用来传递数据的内容有:` what , arg1 , arg2 , object , data(Bundle) , replyTo .`**

	   Android 2.2 之前,object不支持跨进程传输; Android 2.2 之后,object只支持跨进程传输 
	Parcelable 实现类;
		
	   Bundle 可以支持大量的数据类型;
---