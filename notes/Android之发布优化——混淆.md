---

title: Android之 混淆
categories: "android 总结"
tags: 
	- 混淆

---
# 混淆 #

参考: 
- [ **Android Studio 代码混淆** ](https://juejin.im/post/5947e7e8128fe1006a52d922)
- [ **写给Android开发者的混淆使用手册** ](https://juejin.im/entry/59df172f6fb9a04522067f98?utm_source=gold_browser_extension)	
- [ **Android Proguard（混淆）**](https://juejin.im/entry/572d31b579df540060ae260a)
- [ **Android 混淆从入门到精通** ](https://juejin.im/entry/57e341102e958a00541c12b0)

目录:
1.  混淆功能
2.  混淆配置
3.  混淆规则
4.  基本混淆内容请查看当前项目的混淆文件

---
### 1. 混淆功能 ###
`proguard` 包括四个功能，`shrinker`（压缩）, `optimizer`（优化）,`obfuscator`（混淆）,`preverifier`（预校验）。

- `shrinker`：		检测并移除没有用的类,成员,变量;在`optimizer`之后再次执行;

	```java
	# 关闭压缩
	-dontshrink
	```
- `optimizer`：	优化代码,删除没用的参数 , 非入口节点类会加上`private/static/final`;

	```java
	# 关闭优化
	-dontoptimize
	# 对代码进行迭代优化的次数，Android一般为5
	-optimizationpasses n 
	```
- `obfuscator`：	重命名非入口类的类名 , 变量名 , 方法名;包含反射调用的类 , 变量 , 方法 ;
	```java
	# 关闭混淆
	-dontobfuscate 
	```

- `preverifier`：	预校验代码是否符合Java1.6或者更高的规范;

- [DexGuard：	是专门用来优化混淆Android应用的。它的功能包括资源混淆，字符串加密，类加密和dex文件分割等。它是在android编译的时候直接产生Dalvik字节码。](https://link.juejin.im/?target=https%3A%2F%2Fwww.guardsquare.com%2Fdexguard)

---
### 2. 混淆配置 

1. 混淆内容在`app/proguard-rules.pro`中编写;
2. 在`build.gradle(app)`中

		buildType{
			release{
				minifyEnabled true   //开启混淆
				shrinkResources true //清除无用资源
				proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
			}
		}

---
### 3. 混淆规则 

1. 不混淆内容
	
		- 四大组件,application的子类(默认不混淆,不需要加入)
		- 自定义控件(默认不混淆,不需要加入)
		- 枚举
		- 反射的类及属性方法
		- JavaBean
		- WebView的Js调用
		- Parcelable 的子类和 Creator 静态成员变量不混淆
		- Serializable
		- R资源
		- native函数
		- xml的onClick函数
		- 监听和事件回调On*Event,On*Listener
		- 第三方库的类

2. `keep`
	
保留| 防止被移除/被重命名| 防止被重命名
:-|:-|:-
类和类成员| -`keep` | -`keepnames`
仅类成员| -`keepclassmembers`| -`keepclassmembernames`
如果拥有某成员，保留类和类成员| -`keepclasseswithmembers`| -`keepclasseswithmembernames`


		# 不混淆当前包的类名,只混淆类的成员
		-keep class com.schaffer.base.common.*

		# 不混淆当前包的类和子包的类名,只混淆类的成员
		-keep class com.schaffer.base.common.**

		# 不混淆当前类的子类
		-keep public class * extends com.schaffer.base.common.bean.BaseBean
		
		# 不混淆当前类内部成员
		-keep class com.schaffer.base.common.*{*;}
		-keep class com.schaffer.base.common.**{*;}
		
		# 不混淆内部类所有 public 内容
		-keepclassmembers class com.schaffer.base.common.[类名]$[内部类]{
			public * ;
		}

---
