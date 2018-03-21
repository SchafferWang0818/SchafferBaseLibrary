---

title: Android之 Service
categories: "android 总结"
tags: 
	- Service

---
# Service #

>	相关链接:
>	1. Service: 		https://developer.android.google.cn/guide/components/services.html
>	2. BindService:		https://developer.android.google.cn/guide/components/bound-services.html
>	3. AIDL:			https://developer.android.google.cn/guide/components/aidl.html
>	4. Service 工作原理: Android 开发艺术探索 Page 336 

```
	- Service 是一个可以在后台执行长时间运行操作而不提供用户界面的应用组件。
	- 服务在其托管进程的主线程中运行，它既不创建自己的线程，也不在单独的进程中运行（除非另行指定）。
	- startService()：不会将结果返回给调用方，一旦启动，服务即可在后台无限期运行；操作完成后，服务会自行停止运行。
	- bindService ()：多个组件可以同时绑定到Service，但全部取消绑定后，Service即会被销毁。
	- IntentService ：使用工作线程逐一处理所有启动请求。如果您不要求服务同时处理多个请求，这是最好的选择。
		实现 onHandleIntent() 方法会接收每个启动请求的 Intent，使能够执行后台工作。

```

## Service 基本使用 ##

```
	目录: 
		- startService

			- 前台运行Service

		- bindService

		- Service模式的混合使用

		- Service保活/内存常驻手段



```

![image](https://developer.android.google.cn/images/service_lifecycle.png)

* 注：`startService()`并没有`onStop()`回调。

### startService ###

NAME|DEFINE
-|-
`START_NOT_STICKY`|onStartCommand() 返回后终止服务，<font color = orange>**没有挂起的 Intent 要传递，系统不会重建**</font>。避免在不必要时以及应用能够轻松重启所有未完成的作业时运行服务。
`START_STICKY`|onStartCommand() 返回后终止服务，则<font color = orange>**会重建服务并调用 onStartCommand()，但不会重新传递最后一个 Intent**</font>。相反，除非有挂起 Intent 要启动服务（在这种情况下，将传递这些 Intent ），否则系统会通过空 Intent 调用 onStartCommand()。这适用于不执行命令、但无限期运行并等待作业的媒体播放器（或类似服务）。
`START_REDELIVER_INTENT`|onStartCommand() 返回后终止服务，则会<font color = orange>**重建服务并通过传递给服务的最后一个 Intent 调用 onStartCommand()**</font>。任何挂起 Intent 均依次传递。这适用于主动执行应该立即恢复的作业（例如下载文件）的服务。


`Service`使用`stopSelf(int)`停止自己。传递与停止请求的 ID 对应的启动请求的 ID（传递给 `onStartCommand()` 的 `startId`）。然后，如果在您能够调用 `stopSelf(int)` 之前服务收到了新的启动请求，ID 就不匹配，服务也就不会停止。

#### · 前台运行服务 ####
- 调用 `startForeground()`让服务运行于前台。此方法采用两个参数：唯一标识通知的整型数和状态栏的 Notification。

```
	Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text),
	        System.currentTimeMillis());
	Intent notificationIntent = new Intent(this, ExampleActivity.class);
	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	notification.setLatestEventInfo(this, getText(R.string.notification_title),
	        getText(R.string.notification_message), pendingIntent);
	startForeground(1, notification);

```
> 注意：<font color = red>**提供给 `startForeground()` 的整型 ID 不得为 0。**</font>

- `stopForeground(boolean)`采用一个boolean值，指示是否也移除状态栏通知。**此方法不会停止服务。**

- 运行于前台的Service停止时，状态栏通知也将移除。

---
### bindService ###
**Service 绑定模式主要适用于进程间通讯,[点击这里传送至多进程通讯。](https://github.com/SchafferWang0818/SchafferBaseLibrary/blob/master/notes/Android%E4%B9%8B%E5%A4%9A%E8%BF%9B%E7%A8%8B.md)**

- 在 Activity 可见时与服务交互，则应在 `onStart()` 期间绑定，在 `onStop()` 期间取消绑定。
- ** 互相调用的两个 Activity 均绑定某个 Service ,被调用者在前者解绑并销毁之前绑定,<font color = red>系统可能销毁服务并重建服务。</font>**
- **始终`catch` 因远程连接中断而造成的 `DeadObjectException`**。
---

### 混合使用的Service ###

`bindService`:  当所有绑定 `Service` 的部件解除绑定后直至把所有 `ServiceConnection` 解绑才会销毁 `Service`;
`startService`: 当 `Service` 所有任务完成 或 调用`stopService()` 或 `stopSelf()` 后才会回调 `onDestroy()`后销毁 `Service`.

> <font color = red>**当混合使用时 解绑所有 `ServiceConnection` 后并不能销毁 , 需要调用 `stopService()` 或 `stopSelf()` 完成销毁**</font>

- 当混合使用不论先后
	-  `stopService()` 不会进行任何操作,退出绑定页面后自动完成`unbind`操作,并回调`Service#onDestroy()`
	- `unbindService()`完成解绑操作,退出页面不会销毁service;

---
### <font color = "red">**内存常驻的手段**</font> ###

- 设置START_STICKY，kill后会被重启（等待5秒左右），重传Intent，保持与重启前一样

- 通过 startForeground将进程设置为前台进程，做前台服务，优先级和前台应用一个级别​，除非在系统内存非常缺，否则此进程不会被 kill

- 双进程Service：让2个进程互相保护，其中一个Service被清理后，另外没被清理的进程可以立即重启进程

- QQ黑科技:在应用退到后台后，另起一个只有 1 像素的页面停留在桌面上，让自己保持前台状态，保护自己不被后台清理工具杀死

- 在已经root的设备下，修改相应的权限文件，将App伪装成系统级的应用（Android4.0系列的一个漏洞，已经确认可行）

- Android系统中当前进程(Process)fork出来的子进程，被系统认为是两个不同的进程。当父进程被杀死的时候，子进程仍然可以存活，并不受影响。 鉴于目前提到的在Android-Service层做双守护都会失败，我们可以fork出c进程，多进程守护。死循环在那检查是否还存在， 具体的思路如下（Android5.0以下可行）

- 用C编写守护进程(即子进程)，守护进程做的事情就是循环检查目标进程是否存在，不存在则启动它。

- 在NDK环境中将1中编写的C代码编译打包成可执行文件(BUILD_EXECUTABLE)。
主进程启动时将守护进程放入私有目录下，赋予可执行权限，启动它即可。


- 联系厂商，加入白名单

----

----

## Service 工作原理 ##

### startService ###

![Android startService启动原理](https://i.imgur.com/5AqP7s8.png)

---
### bindService ###

![Android bindService启动原理](https://i.imgur.com/fC709Tr.png)

---

----