---

title: 框架源码原理分析
categories: "android 总结"
tags: 
     - android
 
---
# 框架源码原理分析 #


### `Multidex` ###


---
### `Glide` ###
- 参考:
	- [ ** 一篇文章带你完全掌握Glide图片加载框架 ** ](http://mp.weixin.qq.com/s/SoqLK7eoJYT15sSDorpr9g)
	- [ ** 带你全面了解Glide 4的用法 ** ](http://mp.weixin.qq.com/s/p5mIA6nuVEWZsWPHOCHcYw)
	- [ **实战，实现带进度的Glide图片加载功能 ** ](http://mp.weixin.qq.com/s/ANc7ZJRe7UjpD0v8ioz0yA)
	- [ ** 来学习一下Glide强大的图片变换功能吧 ** ](http://mp.weixin.qq.com/s/M12AzUNSeLq202GV5h_4PQ)
	- [ ** Android图片加载框架最全解析（二），从源码的角度理解Glide的执行流程 ** ](http://blog.csdn.net/guolin_blog/article/details/53939176)
	
---
### `Gson` ###
- 参考:
	- [ **推酷 - Gson源码设计学习** ](https://www.tuicool.com/articles/bAn6zu7)
	- [ **掘金 - Gson 源码解析** ](https://juejin.im/entry/59376ff9a22b9d00580d3d7d)
	- [ **CSDN - Google官方Gson解析** ](http://blog.csdn.net/Jsagacity/article/details/78123410)

- `json to JavaBean`

![1](https://upload-images.jianshu.io/upload_images/6193428-e97afe8385100aeb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- `JavaBean to json`	


---
### `Retrofit & OkHttp` ###
- 参考:
	- [ **retrofit源码分析 ** ](http://blog.csdn.net/qq_24675479/article/details/79493948)



- retrofit网络通信八步
	1. 创建retrofit实例
	2. 定义一个网络请求接口中的方法添加注解
	3. 通过动态代理生成网络请求对象
	4. 通过网络请求适配器将网络请求对象进行平台适配(android ,java,IOS)
	5. 通过网络请求执行器 发起网络请求
	6. 通过数据转换器 解析数据
	7. 通过回调执行器，切换主线程
	8. 用户在主线程处理结果

---
### `Rxjava` ###
- 参考:
	- [ ** ** ]()
	- [ ** ** ]()
	- [ ** ** ]()

---
### `EventBus` ###
- 参考:
	- [ ** ** ]()
	- [ ** ** ]()
	- [ ** ** ]()

---
### 热更新`bugly / tinker` ###
- 参考:
	- [ **Android热更新实现原理** ](http://blog.csdn.net/lzyzsd/article/details/49843581/)
	- [ ** ** ]()
	- [ ** ** ]()

java运行时加载的类是通过ClassLoader来实现的,Android中使用PathClassLoader类作为Android的默认的类加载器， `PathClassLoader`其实实现的就是简单的从文件系统中加载类文件。BaseDexClassLoader将findClass方法委托给了pathList对象的findClass方法，pathList对象是在BaseDexClassLoader的构造函数中new出来的， 它的类型是`DexPathList`。

**一旦有类被成功加载，那么它的dex一定会出现在dexElements所对应的dex文件中**，并且dexElements中出现的顺序也很重要，在dexElements前面出现的dex会被优先加载，一旦Class被加载成功，就会立即返回，也就是说，**如果想做hotpatch，一定要保证我们的hotpacth dex文件出现在dexElements列表的前面。**


由于PathClassLoader.pathList.dexElements是private,就需要**反射**来修改。
构造我们自己的dex文件所对应的dexElements数组的时候，我们也可以采取一个比较取巧的方式，就是通过构造一个DexClassLoader对象来加载我们的dex文件，并且调用一次`dexClassLoader.loadClass(dummyClassName)`; 
方法，这样，`dexClassLoader.pathList.dexElements`中，就会包含我们的dex，**通过把`dexClassLoader.pathList.dexElements`插入到系统默认的`classLoader.pathList.dexElements`列表前面，就可以让系统优先加载我们的dex中的类**，从而可以实现热更新了。

---