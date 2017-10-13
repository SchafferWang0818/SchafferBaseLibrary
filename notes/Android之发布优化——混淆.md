# 混淆 #

	参考自: 
		https://juejin.im/post/5947e7e8128fe1006a52d922
		https://juejin.im/entry/59df172f6fb9a04522067f98?utm_source=gold_browser_extension	
	目录:
		1.  混淆配置
		2.  混淆规则
		3.  基本混淆内容请查看当前项目的混淆文件


### 1. 混淆配置 ##

1. 混淆内容在`app/proguard-rules.pro`中编写;
2. 在build.gradle(app)中

		buildType{

			release{
				minifyEnabled true   //开启混淆
	            shrinkResources true //清除无用资源
	            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
			}

		}



---
### 2. 混淆规则 ##

1. 不混淆内容
	
		- 四大组件,application的子类
		- 自定义控件
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


2. 不混淆当前类

		-keep class com.schaffer.base.common.*

3. 不混淆当前类的子类

		-keep class com.schaffer.base.common.**
		-keep public class * extends com.schaffer.base.common.bean.BaseBean
		

4. 不混淆当前类内部方法变量

		-keep class com.schaffer.base.common.*{*;}
		-keep class com.schaffer.base.common.**{*;}


---
