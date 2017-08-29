Delvik和AndroidRuntime
-

##Dalvik
Dalvik是Google公司自己设计用于Android平台的Java虚拟机。Dalvik虚拟机是Google等厂商合作开发的Android移动设备平台的核心组成部分之一。它可以支持已转换为 .dex（即Dalvik Executable）格式的Java应用程序的运行，.dex格式是专为Dalvik设计的一种压缩格式，适合**内存和处理器速度有限**的系统。Dalvik 经过优化，允许**在有限的内存中同时运行多个虚拟机**的实例，并且**每一个Dalvik 应用作为一个独立的Linux 进程执行**。独立的进程可以防止在虚拟机崩溃的时候所有程序都被关闭。


##AndroidRuntime
 ART代表Android Runtime，其处理应用程序执行的方式完全不同于Dalvik，Dalvik是依靠一个**Just-In-Time (JIT)**编译器去解释字节码。开发者编译后的应用代码需要通过一个解释器在用户的设备上运行，这一机制并不高效，但**让应用能更容易在不同硬件和架构上运行**。ART则完全改变了这套做法，在应用安装时就**预编译字节码到机器语言**，这一机制叫**Ahead-Of-Time (AOT）**编译。在移除解释代码这一过程后，**应用程序执行将更有效率，启动更快。**