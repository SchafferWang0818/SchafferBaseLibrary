---

title: Android之虚拟机 
categories: "android 总结"
tags: 
     - android
     - 虚拟机
     - JVM
     - Dalvik
     - AndroidRuntime
 
---
## JVM
 
**包含有JVM的JRE为JDK提供java应用程序最低要求的<font color="red">环境</font>。**

**JAVA虚拟机运行的是JAVA字节码。**

**JVM基于栈。**程序在运行时虚拟机需要频繁的从栈上读取写入数据，这个过程需要更多的指令分派与内存访问次数，会耗费很多CPU时间。

### 生命周期
1. 启动：任何一个拥有main函数的class都可以作为JVM实例运行的起点
2. 运行：main函数为起点，程序中的其他线程均由它启动，包括daemon守护线程和non-daemon普通线程。daemon是JVM自己使用的线程比如GC线程，main方法的初始线程是non-daemon。
3. 消亡：所有线程终止时，JVM实例结束生命。

---
## Dalvik
Dalvik是Google公司自己设计用于Android平台的Java虚拟机。Dalvik虚拟机是Google等厂商合作开发的Android移动设备平台的核心组成部分之一。它可以支持已转换为 .dex（即Dalvik Executable）格式的Java应用程序的运行，.dex格式是专为Dalvik设计的一种压缩格式，适合**内存和处理器速度有限**的系统。Dalvik 经过优化，允许**在有限的内存中同时运行多个虚拟机**的实例，并且**每一个Dalvik 应用作为一个独立的Linux 进程执行**。独立的进程可以防止在虚拟机崩溃的时候所有程序都被关闭。

Dalvik是依靠一个**Just-In-Time (JIT)**编译器去解释字节码。开发者编译后的应用代码需要通过一个解释器在用户的设备上运行，这一机制并不高效，但**让应用能更容易在不同硬件和架构上运行**。

**Dalvik虚拟机运行的是Dalvik字节码。**依靠**Just-In-Time (JIT)**编译器去解释执行，运行时动态地将执行频率很高的dex字节码翻译成本地机器码，然后在执行，但是将dex字节码翻译成本地机器码是发生在应用程序的运行过程中，并且应用程序每一次重新运行的时候，都要重新做这个翻译工作，因此，及时采用了JIT，Dalvik虚拟机的总体性能还是不能与直接执行本地机器码的ART虚拟机相比。

**DVM基于寄存器。**数据的访问通过寄存器间直接传递，这样的访问方式比基于栈方式要快很多。

---
## AndroidRuntime
 ART代表Android Runtime，在应用安装时就**预编译字节码到机器语言**，这一机制叫**Ahead-Of-Time (AOT），编译由 ART 的 dex2oat 工具执行**。在移除解释代码这一过程后，**应用程序执行将更有效率，启动更快。**[**ART 的 JNI 比 Dalvik 的 JNI 更为严格一些。**](https://developer.android.google.cn/guide/practices/verifying-apps-art.html?hl=zh-cn)

- AOT的编译器分两种模式：

	- 开发: 在开发机上编译预装应用；
	- 安装: 在设备上编译新安装的应用,在应用安装时将dex字节码翻译成本地机器码。
	<br>

- ART优点:

	- 系统性能的显著提升。
	- 应用启动更快、运行更快、体验更流畅、触感反馈更及时。
	- 更长的电池续航能力。
	- 支持更低的硬件。

- ART缺点:

	- 更大的存储空间占用，可能会增加10%-20%。
	- 更长的应用安装时间。

---



