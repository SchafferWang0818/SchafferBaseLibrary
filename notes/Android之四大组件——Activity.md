# Activity #



## Activity的使用 ##





---
## Activity工作原理 ##

> 备注:
> 1. `ApplicationThread`: APT,继承自`IApplicationThread`(继承自`IInterface`),`ApplicationThreadNative`;
> 2. `ActivityThread`:    AT
> 3. `Instrumentation`:   Ins,仪表仪器;
> 4. `ActivityManagerNative`: AMN,继承自`Binder`与`IActivityManager`;
> 5. `ActivityManagerService`:AMS,继承自AMN;
> 6. `ActivityStackSupervisor`: ASS,Activity栈监测类;
> 7. `ActivityStack`:AS;

> 8. `IInterface`: `Binder` 基类;
> 9. `ApplicationThreadNative`:APTN,继承自`Binder`,`IApplicationThread`;

### 启动流程 ###

![Activity 启动流程图](https://i.imgur.com/VKNhWzp.png)



- Activity的启动流程一般是通过调用startActivity/startActivityForResult来开始的

- Activity的启动流程涉及到多个进程之间的通讯这里主要是ActivityThread与ActivityManagerService之间的通讯

- ActivityThread向ActivityManagerService传递进程间消息通过ActivityManagerNative，ActivityManagerService向ActivityThread进程间传递消息通过IApplicationThread。

- ActivityManagerService接收到应用进程创建Activity的请求之后会执行初始化操作，解析启动模式，保存请求信息等一系列操作。

- ActivityManagerService保存完请求信息之后会将当前系统栈顶的Activity执行onPause操作，并且IApplication进程间通讯告诉应用程序继承执行当前栈顶的Activity的onPause方法；

- ActivityThread接收到SystemServer的消息之后会统一交个自身定义的Handler对象处理分发；

- ActivityThread执行完栈顶的Activity的onPause方法之后会通过ActivityManagerNative执行进程间通讯告诉ActivityManagerService，栈顶Actiity已经执行完成onPause方法，继续执行后续操作；

- ActivityManagerService会继续执行启动Activity的逻辑，这时候会判断需要启动的Activity所属的应用进程是否已经启动，若没有启动则首先会启动这个Activity的应用程序进程；

- ActivityManagerService会通过socket与Zygote继承通讯，并告知Zygote进程fork出一个新的应用程序进程，然后执行ActivityThread的mani方法；

- 在ActivityThead.main方法中执行初始化操作，初始化主线程异步消息，然后通知ActivityManagerService执行进程初始化操作；

- ActivityManagerService会在执行初始化操作的同时检测当前进程是否有需要创建的Activity对象，若有的话，则执行创建操作；

- ActivityManagerService将执行创建Activity的通知告知ActivityThread，然后通过反射机制创建出Activity对象，并执行Activity的onCreate方法，onStart方法，onResume方法；

- ActivityThread执行完成onResume方法之后告知ActivityManagerService onResume执行完成，开始执行栈顶Activity的onStop方法；

- ActivityManagerService开始执行栈顶的onStop方法并告知ActivityThread；

- ActivityThread执行真正的onStop方法；


---


---