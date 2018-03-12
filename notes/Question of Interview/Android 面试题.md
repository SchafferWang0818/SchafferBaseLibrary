# Android 面试题 #

	Android基础知识点
		- 四大组件是什么
		- 四大组件的生命周期和简单用法
		- Activity之间的通信方式
		- Activity各种情况下的生命周期
		- 横竖屏切换的时候，Activity 各种情况下的生命周期
		- Activity与Fragment之间生命周期比较
		- Activity上有Dialog的时候按Home键时的生命周期
		- 两个Activity 之间跳转时必然会执行的是哪几个方法？
		- 前台切换到后台，然后再回到前台，Activity生命周期回调方法。弹出Dialog，生命值周期回调方法。
		- Activity的四种启动模式对比
		- Activity状态保存与恢复
		- fragment各种情况下的生命周期
		- Fragment状态保存startActivityForResult是哪个类的方法，在什么情况下使用？
		- 如何实现Fragment的滑动？
		- fragment之间传递数据的方式？
		- Activity 怎么和Service 绑定？
		- 怎么在Activity 中启动自己对应的Service？
		- service和activity怎么进行数据交互？
		- Service的开启方式
		- 请描述一下Service 的生命周期
		- 谈谈你对ContentProvider的理解
		- 说说ContentProvider、ContentResolver、ContentObserver 之间的关系
		- 请描述一下广播BroadcastReceiver的理解
		- 广播的分类
		- 广播使用的方式和场景
		- 在manifest 和代码中如何注册和使用BroadcastReceiver?
		- 本地广播和全局广播有什么差别？
		- BroadcastReceiver，LocalBroadcastReceiver 区别
		- AlertDialog,popupWindow,Activity区别
		- Application 和 Activity 的 Context 对象的区别
		- Android属性动画特性
		- 如何导入外部数据库?
		- LinearLayout、RelativeLayout、FrameLayout的特性及对比，并介绍使用场景。
		- 谈谈对接口与回调的理解
		- 回调的原理
		- 写一个回调demo
		- 介绍下SurfaceView
		- RecycleView的使用
		- 序列化的作用，以及Android两种序列化的区别
		- 差值器
		- 估值器
		- Android中数据存储方式

	Android源码相关分析

		- Android动画框架实现原理
		- Android各个版本API的区别
		- Requestlayout，onlayout，onDraw，DrawChild区别与联系
		- invalidate和postInvalidate的区别及使用
		- Activity-Window-View三者的差别
		- 谈谈对Volley的理解
		- 如何优化自定义View
		- 低版本SDK如何实现高版本api？
		- 描述一次网络请求的流程
		- HttpUrlConnection 和 okhttp关系
		- Bitmap对象的理解
		- looper架构
		- ActivityThread，AMS，WMS的工作原理
		- 自定义View如何考虑机型适配
		- 自定义View的事件
		- AsyncTask+HttpClient 与 AsyncHttpClient有什么区别？
		- LaunchMode应用场景
		- AsyncTask 如何使用?
		- SpareArray原理
		- 请介绍下ContentProvider 是如何实现数据共享的？
		- Android Service与Activity之间通信的几种方式
		- IntentService原理及作用是什么？
		- 说说Activity、Intent、Service 是什么关系
		- ApplicationContext和ActivityContext的区别
		- SP是进程同步的吗?有什么方法做到同步？
		- 谈谈多线程在Android中的使用
		- 进程和 Application 的生命周期
		- 封装View的时候怎么知道view的大小
		- RecycleView原理
		- AndroidManifest的作用与理解

	常见的一些原理性问题

		- Handler机制和底层实现
		- Handler、Thread和HandlerThread的差别
		- handler发消息给子线程，looper怎么启动？
		- 关于Handler，在任何地方new Handler 都是什么线程下?
		- ThreadLocal原理，实现及如何保证Local属性？
		- 请解释下在单线程模型中Message、Handler、Message Queue、Looper之间的关系
		- 请描述一下View事件传递分发机制
		- Touch事件传递流程
		- 事件分发中的onTouch 和onTouchEvent 有什么区别，又该如何使用？
		- View和ViewGroup分别有哪些事件分发相关的回调方法
		- View刷新机制
		- View绘制流程
		- 自定义控件原理
		- 自定义View如何提供获取View属性的接口？
		- Android代码中实现WAP方式联网
		- AsyncTask机制
		- AsyncTask原理及不足
		- 如何取消AsyncTask？
		- 为什么不能在子线程更新UI？
		- ANR产生的原因是什么？
		- ANR定位和修正
		- oom是什么？
		- 什么情况导致oom？
		- 有什么解决方法可以避免OOM？
		- Oom 是否可以try catch？为什么？
		- 内存泄漏是什么？
		- 什么情况导致内存泄漏？
		- 如何防止线程的内存泄漏？
		- 内存泄露场的解决方法
		- 内存泄漏和内存溢出区别？
		- LruCache默认缓存大小
		- ContentProvider的权限管理(解答：读写分离，权限控制-精确到表级，URL控制)
		- 如何通过广播拦截和abort一条短信？
		- 广播是否可以请求网络？
		- 广播引起anr的时间限制是多少？
		- 计算一个view的嵌套层级
		- Activity栈
		- Android线程有没有上限？
		- 线程池有没有上限？
		- ListView重用的是什么？
		- Android为什么引入Parcelable？
		- 有没有尝试简化Parcelable的使用？

	开发中常见的一些问题

		- ListView 中图片错位的问题是如何产生的?
		- 混合开发有了解吗？
		- 知道哪些混合开发的方式？说出它们的优缺点和各自使用场景？（解答：比如:RN，weex，H5，小程序，WPA等。做Android的了解一些前端js等还是很有好处的)；
		- 屏幕适配的处理技巧都有哪些?
		- 服务器只提供数据接收接口，在多线程或多进程条件下，如何保证数据的有序到达？
		- 动态布局的理解
		- 怎么去除重复代码？
		- 画出 Android 的大体架构图
		- Recycleview和ListView的区别
		- ListView图片加载错乱的原理和解决方案
		- 动态权限适配方案，权限组的概念
		- Android系统为什么会设计ContentProvider？
		- 下拉状态栏是不是影响activity的生命周期
		- 如果在onStop的时候做了网络请求，onResume的时候怎么恢复？
		- Bitmap 使用时候注意什么？
		- Bitmap的recycler()
		- Android中开启摄像头的主要步骤
		- ViewPager使用细节，如何设置成每次只初始化当前的Fragment，其他的不初始化？
		- 点击事件被拦截，但是想传到下面的View，如何操作？
		- 微信主页面的实现方式
		- 微信上消息小红点的原理
		- CAS介绍（这是阿里巴巴的面试题，我不是很了解，可以参考博客: CAS简介http://blog.csdn.net/jly4758/article/details/46673835）

	图片

		- 图片库对比
		- 图片库的源码分析
		- 图片框架缓存实现
		- LRUCache原理
		- 图片加载原理
		- 自己去实现图片库，怎么做？
		- Glide源码解析
		- Glide使用什么缓存？
		- Glide内存缓存如何控制大小？

	网络和安全机制

		- 网络框架对比和源码分析
		- 自己去设计网络请求框架，怎么做？
		- okhttp源码
		- 网络请求缓存处理，okhttp如何处理网络缓存的
		- 从网络加载一个10M的图片，说下注意事项
		- TCP的3次握手和四次挥手
		- TCP与UDP的区别
		- TCP与UDP的应用
		- HTTP协议
		- HTTP1.0与2.0的区别
		- HTTP报文结构
		- HTTP与HTTPS的区别以及如何实现安全性
		- 如何验证证书的合法性?
		- https中哪里用了对称加密，哪里用了非对称加密，对加密算法（如RSA）等是否有了解?
		- client如何确定自己发送的消息被server收到?
		- 谈谈你对WebSocket的理解
		- WebSocket与socket的区别
		- 谈谈你对安卓签名的理解。
		- 请解释安卓为啥要加签名机制?
		- 视频加密传输
		- App 是如何沙箱化，为什么要这么做？
		- 权限管理系统（底层的权限是如何进行 grant 的）？

	数据库

		- sqlite升级，增加字段的语句
		- 数据库框架对比和源码分析
		- 数据库的优化
		- 数据库数据迁移问题

	算法

		- 排序算法有哪些？
		- 最快的排序算法是哪个？
		- 手写一个冒泡排序
		- 手写快速排序代码
		- 快速排序的过程、时间复杂度、空间复杂度
		- 手写堆排序
		- 堆排序过程、时间复杂度及空间复杂度
		- 写出你所知道的排序算法及时空复杂度，稳定性
		- 二叉树给出根节点和目标节点，找出从根节点到目标节点的路径
		- 给阿里2万多名员工按年龄排序应该选择哪个算法？
		- GC算法(各种算法的优缺点以及应用场景)
		- 蚁群算法与蒙特卡洛算法
		- 子串包含问题(KMP 算法)写代码实现
		- 一个无序，不重复数组，输出N个元素，使得N个元素的和相加为M，给出时间复杂度、空间复杂度。手写算法
		- 万亿级别的两个URL文件A和B，如何求出A和B的差集C(提示：Bit映射->hash分组->多文件读写效率->磁盘寻址以及应用层面对寻址的优化)
		- 百度POI中如何试下查找最近的商家功能(提示：坐标镜像+R树)。
		- 两个不重复的数组集合中，求共同的元素。
		- 两个不重复的数组集合中，这两个集合都是海量数据，内存中放不下，怎么求共同的元素？
		- 一个文件中有100万个整数，由空格分开，在程序中判断用户输入的整数是否在此文件中。说出最优的方法
		- 一张Bitmap所占内存以及内存占用的计算
		- 2000万个整数，找出第五十大的数字？
		- 烧一根不均匀的绳，从头烧到尾总共需要1个小时。现在有若干条材质相同的绳子，问如何用烧绳的方法来计时一个小时十五分钟呢？
		- 求1000以内的水仙花数以及40亿以内的水仙花数
		- 5枚硬币，2正3反如何划分为两堆然后通过翻转让两堆中正面向上的硬8币和反面向上的硬币个数相同
		- 时针走一圈，时针分针重合几次
		- N*N的方格纸,里面有多少个正方形
		- x个苹果，一天只能吃一个、两个、或者三个，问多少天可以吃完？

	插件化、模块化、组件化、热修复、增量更新、Gradle

		- 对热修复和插件化的理解
		- 插件化原理分析
		- 模块化实现（好处，原因）
		- 热修复,插件化
		- 项目组件化的理解
		- 描述清点击 Android Studio 的 build 按钮后发生了什么

	架构设计和设计模式

		- 谈谈你对Android设计模式的理解
		- MVC MVP MVVM原理和区别
		- 你所知道的设计模式有哪些？
		- 项目中常用的设计模式
		- 手写生产者/消费者模式
		- 写出观察者模式的代码
		- 适配器模式，装饰者模式，外观模式的异同？
		- 用到的一些开源框架，介绍一个看过源码的，内部实现过程。
		- 谈谈对RxJava的理解
		- RxJava的功能与原理实现
		- RxJava的作用，与平时使用的异步操作来比的优缺点
		- 说说EventBus作用，实现方式，代替EventBus的方式
		- 从0设计一款App整体架构，如何去做？
		- 说一款你认为当前比较火的应用并设计(比如：直播APP，P2P金融，小视频等)
		- 谈谈对java状态机理解
		- Fragment如果在Adapter中使用应该如何解耦？
		- Binder机制及底层实现
		- 对于应用更新这块是如何做的？(解答：灰度，强制更新，分区域更新)？
		- 实现一个Json解析器(可以通过正则提高速度)
		- 统计启动时长,标准

	性能优化

		- 如何对Android 应用进行性能分析以及优化?
		- ddms 和 traceView
		- 性能优化如何分析systrace？
		- 用IDE如何分析内存泄漏？
		- Java多线程引发的性能问题，怎么解决？
		- 启动页白屏及黑屏解决？
		- 启动太慢怎么解决？
		- 怎么保证应用启动不卡顿？
		- App启动崩溃异常捕捉
		- 自定义View注意事项
		- 现在下载速度很慢,试从网络协议的角度分析原因,并优化(提示：网络的5层都可以涉及)。
		- Https请求慢的解决办法（提示：DNS，携带数据，直接访问IP）
		- 如何保持应用的稳定性
		- RecyclerView和ListView的性能对比
		- ListView的优化
		- RecycleView优化
		- View渲染
		- Bitmap如何处理大图，如一张30M的大图，如何预防OOM
		- java中的四种引用的区别以及使用场景
		- 强引用置为null，会不会被回收？

	NDK、jni、Binder、AIDL、进程通信有关

		- 请介绍一下NDK
		- 什么是NDK库?
		- jni用过吗？
		- 如何在jni中注册native函数，有几种注册方式?
		- Java如何调用c、c++语言？
		- jni如何调用java层代码？
		- 进程间通信的方式？
		- Binder机制
		- 简述IPC？
		- 什么是AIDL？
		- AIDL解决了什么问题？
		- AIDL如何使用？
		- Android 上的 Inter-Process-Communication 跨进程通信时如何工作的？
		- 多进程场景遇见过么？
		- Android进程分类？
		- 进程和 Application 的生命周期？
		- 进程调度
		- 谈谈对进程共享和线程安全的认识
		- 谈谈对多进程开发的理解以及多进程应用场景
		- 什么是协程？

	framework层、ROM定制、Ubuntu、Linux之类的问题

		- java虚拟机的特性
		- 谈谈对jvm的理解
		- JVM内存区域，开线程影响哪块内存
		- 对Dalvik、ART虚拟机有什么了解？
		- Art和Dalvik对比
		- 虚拟机原理，如何自己设计一个虚拟机(内存管理，类加载，双亲委派)
		- 谈谈你对双亲委派模型理解
		- JVM内存模型，内存区域
		- 类加载机制
		- 谈谈对ClassLoader(类加载器)的理解
		- 谈谈对动态加载（OSGI）的理解
		- 内存对象的循环引用及避免
		- 内存回收机制、GC回收策略、GC原理时机以及GC对象
		- 垃圾回收机制与调用System.gc()区别
		- Ubuntu编译安卓系统
		- 系统启动流程是什么？（提示：Zygote进程 –> SystemServer进程 –> 各种系统服务 –> 应用进程）
		- 大体说清一个应用程序安装到手机上时发生了什么
		- 简述Activity启动全部过程
		- App启动流程，从点击桌面开始
		- 逻辑地址与物理地址，为什么使用逻辑地址？
		- Android为每个应用程序分配的内存大小是多少？
		- Android中进程内存的分配，能不能自己分配定额内存？
		- 进程保活的方式
		- 如何保证一个后台服务不被杀死？（相同问题：如何保证service在后台不被kill？）比较省电的方式是什么？
		- App中唤醒其他进程的实现方式













（一）Android基础知识点

四大组件是什么
四大组件的生命周期和简单用法
Activity之间的通信方式
Activity各种情况下的生命周期
横竖屏切换的时候，Activity 各种情况下的生命周期
Activity与Fragment之间生命周期比较
Activity上有Dialog的时候按Home键时的生命周期
两个Activity 之间跳转时必然会执行的是哪几个方法？
前台切换到后台，然后再回到前台，Activity生命周期回调方法。弹出Dialog，生命值周期回调方法。
Activity的四种启动模式对比
Activity状态保存于恢复
fragment各种情况下的生命周期
Fragment状态保存startActivityForResult是哪个类的方法，在什么情况下使用？
如何实现Fragment的滑动？
fragment之间传递数据的方式？
Activity 怎么和Service 绑定？
怎么在Activity 中启动自己对应的Service？
service和activity怎么进行数据交互？
Service的开启方式
请描述一下Service 的生命周期
谈谈你对ContentProvider的理解
说说ContentProvider、ContentResolver、ContentObserver 之间的关系
请描述一下广播BroadcastReceiver的理解
广播的分类
广播使用的方式和场景
在manifest 和代码中如何注册和使用BroadcastReceiver?
本地广播和全局广播有什么差别？
BroadcastReceiver，LocalBroadcastReceiver 区别
AlertDialog,popupWindow,Activity区别
Application 和 Activity 的 Context 对象的区别
Android属性动画特性
如何导入外部数据库?
LinearLayout、RelativeLayout、FrameLayout的特性及对比，并介绍使用场景。
谈谈对接口与回调的理解
回调的原理
写一个回调demo
介绍下SurfView
RecycleView的使用
序列化的作用，以及Android两种序列化的区别
差值器
估值器
Android中数据存储方式


（二）Android源码相关分析

Android动画框架实现原理
Android各个版本API的区别
Requestlayout，onlayout，onDraw，DrawChild区别与联系
invalidate和postInvalidate的区别及使用
Activity-Window-View三者的差别
谈谈对Volley的理解
如何优化自定义View
低版本SDK如何实现高版本api？
描述一次网络请求的流程
HttpUrlConnection 和 okhttp关系
Bitmap对象的理解
looper架构
ActivityThread，AMS，WMS的工作原理
自定义View如何考虑机型适配
自定义View的事件
AstncTask+HttpClient 与 AsyncHttpClient有什么区别？
LaunchMode应用场景
AsyncTask 如何使用?
SpareArray原理
请介绍下ContentProvider 是如何实现数据共享的？
AndroidService与Activity之间通信的几种方式
IntentService原理及作用是什么？
说说Activity、Intent、Service 是什么关系
ApplicationContext和ActivityContext的区别
SP是进程同步的吗?有什么方法做到同步？
谈谈多线程在Android中的使用
进程和 Application 的生命周期
封装View的时候怎么知道view的大小
RecycleView原理
AndroidManifest的作用与理解

（三）常见的一些原理性问题

Handler机制和底层实现
Handler、Thread和HandlerThread的差别
handler发消息给子线程，looper怎么启动？
关于Handler，在任何地方new Handler 都是什么线程下?
ThreadLocal原理，实现及如何保证Local属性？
请解释下在单线程模型中Message、Handler、Message Queue、Looper之间的关系
请描述一下View事件传递分发机制
Touch事件传递流程
事件分发中的onTouch 和onTouchEvent 有什么区别，又该如何使用？
View和ViewGroup分别有哪些事件分发相关的回调方法
View刷新机制
View绘制流程
自定义控件原理
自定义View如何提供获取View属性的接口？
Android代码中实现WAP方式联网
AsyncTask机制
AsyncTask原理及不足
如何取消AsyncTask？
为什么不能在子线程更新UI？
ANR产生的原因是什么？
ANR定位和修正
oom是什么？
什么情况导致oom？
有什么解决方法可以避免OOM？
Oom 是否可以try catch？为什么？
内存泄漏是什么？
什么情况导致内存泄漏？
如何防止线程的内存泄漏？
内存泄露场的解决方法
内存泄漏和内存溢出区别？
LruCache默认缓存大小
ContentProvider的权限管理(解答：读写分离，权限控制-精确到表级，URL控制)
如何通过广播拦截和abort一条短信？
广播是否可以请求网络？
广播引起anr的时间限制是多少？
计算一个view的嵌套层级
Activity栈
Android线程有没有上限？
线程池有没有上限？
ListView重用的是什么？
Android为什么引入Parcelable？
有没有尝试简化Parcelable的使用？

（四）开发中常见的一些问题

ListView 中图片错位的问题是如何产生的?
混合开发有了解吗？
知道哪些混合开发的方式？说出它们的优缺点和各自使用场景？（解答：比如:RN，weex，H5，小程序，WPA等。做Android的了解一些前端js等还是很有好处的)；
屏幕适配的处理技巧都有哪些?
服务器只提供数据接收接口，在多线程或多进程条件下，如何保证数据的有序到达？
动态布局的理解
怎么去除重复代码？
画出 Android 的大体架构图
Recycleview和ListView的区别
ListView图片加载错乱的原理和解决方案
动态权限适配方案，权限组的概念
Android系统为什么会设计ContentProvider？
下拉状态栏是不是影响activity的生命周期
如果在onStop的时候做了网络请求，onResume的时候怎么恢复？
Bitmap 使用时候注意什么？
Bitmap的recycler()
Android中开启摄像头的主要步骤
ViewPager使用细节，如何设置成每次只初始化当前的Fragment，其他的不初始化？
点击事件被拦截，但是想传到下面的View，如何操作？
微信主页面的实现方式
微信上消息小红点的原理
CAS介绍（这是阿里巴巴的面试题，我不是很了解，可以参考博客: CAS简介http://blog.csdn.net/jly4758/article/details/46673835）

（一）图片

图片库对比
图片库的源码分析
图片框架缓存实现
LRUCache原理
图片加载原理
自己去实现图片库，怎么做？
Glide源码解析
Glide使用什么缓存？
Glide内存缓存如何控制大小？

（二）网络和安全机制

网络框架对比和源码分析
自己去设计网络请求框架，怎么做？
okhttp源码
网络请求缓存处理，okhttp如何处理网络缓存的
从网络加载一个10M的图片，说下注意事项
TCP的3次握手和四次挥手
TCP与UDP的区别
TCP与UDP的应用
HTTP协议
HTTP1.0与2.0的区别
HTTP报文结构
HTTP与HTTPS的区别以及如何实现安全性
如何验证证书的合法性?
https中哪里用了对称加密，哪里用了非对称加密，对加密算法（如RSA）等是否有了解?
client如何确定自己发送的消息被server收到?
谈谈你对WebSocket的理解
WebSocket与socket的区别
谈谈你对安卓签名的理解。
请解释安卓为啥要加签名机制?
视频加密传输
App 是如何沙箱化，为什么要这么做？
权限管理系统（底层的权限是如何进行 grant 的）？

（三）数据库

sqlite升级，增加字段的语句
数据库框架对比和源码分析
数据库的优化
数据库数据迁移问题

（四）算法

排序算法有哪些？
最快的排序算法是哪个？
手写一个冒泡排序
手写快速排序代码
快速排序的过程、时间复杂度、空间复杂度
手写堆排序
堆排序过程、时间复杂度及空间复杂度
写出你所知道的排序算法及时空复杂度，稳定性
二叉树给出根节点和目标节点，找出从根节点到目标节点的路径
给阿里2万多名员工按年龄排序应该选择哪个算法？
GC算法(各种算法的优缺点以及应用场景)
蚁群算法与蒙特卡洛算法
子串包含问题(KMP 算法)写代码实现
一个无序，不重复数组，输出N个元素，使得N个元素的和相加为M，给出时间复杂度、空间复杂度。手写算法
万亿级别的两个URL文件A和B，如何求出A和B的差集C(提示：Bit映射->hash分组->多文件读写效率->磁盘寻址以及应用层面对寻址的优化)
百度POI中如何试下查找最近的商家功能(提示：坐标镜像+R树)。
两个不重复的数组集合中，求共同的元素。
两个不重复的数组集合中，这两个集合都是海量数据，内存中放不下，怎么求共同的元素？
一个文件中有100万个整数，由空格分开，在程序中判断用户输入的整数是否在此文件中。说出最优的方法
一张Bitmap所占内存以及内存占用的计算
2000万个整数，找出第五十大的数字？
烧一根不均匀的绳，从头烧到尾总共需要1个小时。现在有若干条材质相同的绳子，问如何用烧绳的方法来计时一个小时十五分钟呢？
求1000以内的水仙花数以及40亿以内的水仙花数
5枚硬币，2正3反如何划分为两堆然后通过翻转让两堆中正面向上的硬8币和反面向上的硬币个数相同
时针走一圈，时针分针重合几次
N*N的方格纸,里面有多少个正方形
x个苹果，一天只能吃一个、两个、或者三个，问多少天可以吃完？

（五）插件化、模块化、组件化、热修复、增量更新、Gradle

对热修复和插件化的理解
插件化原理分析
模块化实现（好处，原因）
热修复,插件化
项目组件化的理解
描述清点击 Android Studio 的 build 按钮后发生了什么

（六）架构设计和设计模式

谈谈你对Android设计模式的理解
MVC MVP MVVM原理和区别
你所知道的设计模式有哪些？
项目中常用的设计模式
手写生产者/消费者模式
写出观察者模式的代码
适配器模式，装饰者模式，外观模式的异同？
用到的一些开源框架，介绍一个看过源码的，内部实现过程。
谈谈对RxJava的理解
RxJava的功能与原理实现
RxJava的作用，与平时使用的异步操作来比的优缺点
说说EventBus作用，实现方式，代替EventBus的方式
从0设计一款App整体架构，如何去做？
说一款你认为当前比较火的应用并设计(比如：直播APP，P2P金融，小视频等)
谈谈对java状态机理解
Fragment如果在Adapter中使用应该如何解耦？
Binder机制及底层实现
对于应用更新这块是如何做的？(解答：灰度，强制更新，分区域更新)？
实现一个Json解析器(可以通过正则提高速度)
统计启动时长,标准

（七）性能优化

如何对Android 应用进行性能分析以及优化?
ddms 和 traceView
性能优化如何分析systrace？
用IDE如何分析内存泄漏？
Java多线程引发的性能问题，怎么解决？
启动页白屏及黑屏解决？
启动太慢怎么解决？
怎么保证应用启动不卡顿？
App启动崩溃异常捕捉
自定义View注意事项
现在下载速度很慢,试从网络协议的角度分析原因,并优化(提示：网络的5层都可以涉及)。
Https请求慢的解决办法（提示：DNS，携带数据，直接访问IP）
如何保持应用的稳定性
RecyclerView和ListView的性能对比
ListView的优化
RecycleView优化
View渲染
Bitmap如何处理大图，如一张30M的大图，如何预防OOM
java中的四种引用的区别以及使用场景
强引用置为null，会不会被回收？

（八）NDK、jni、Binder、AIDL、进程通信有关

请介绍一下NDK
什么是NDK库?
jni用过吗？
如何在jni中注册native函数，有几种注册方式?
Java如何调用c、c++语言？
jni如何调用java层代码？
进程间通信的方式？
Binder机制
简述IPC？
什么是AIDL？
AIDL解决了什么问题？
AIDL如何使用？
Android 上的 Inter-Process-Communication 跨进程通信时如何工作的？
多进程场景遇见过么？
Android进程分类？
进程和 Application 的生命周期？
进程调度
谈谈对进程共享和线程安全的认识
谈谈对多进程开发的理解以及多进程应用场景
什么是协程？

（九）framework层、ROM定制、Ubuntu、Linux之类的问题

java虚拟机的特性
谈谈对jvm的理解
JVM内存区域，开线程影响哪块内存
对Dalvik、ART虚拟机有什么了解？
Art和Dalvik对比
虚拟机原理，如何自己设计一个虚拟机(内存管理，类加载，双亲委派)
谈谈你对双亲委派模型理解
JVM内存模型，内存区域
类加载机制
谈谈对ClassLoader(类加载器)的理解
谈谈对动态加载（OSGI）的理解
内存对象的循环引用及避免
内存回收机制、GC回收策略、GC原理时机以及GC对象
垃圾回收机制与调用System.gc()区别
Ubuntu编译安卓系统
系统启动流程是什么？（提示：Zygote进程 –> SystemServer进程 –> 各种系统服务 –> 应用进程）
大体说清一个应用程序安装到手机上时发生了什么
简述Activity启动全部过程
App启动流程，从点击桌面开始
逻辑地址与物理地址，为什么使用逻辑地址？
Android为每个应用程序分配的内存大小是多少？
Android中进程内存的分配，能不能自己分配定额内存？
进程保活的方式
如何保证一个后台服务不被杀死？（相同问题：如何保证service在后台不被kill？）比较省电的方式是什么？
App中唤醒其他进程的实现方式