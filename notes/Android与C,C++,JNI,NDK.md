#  Android与C,C++,JNI,NDK

- 参考: 

	1. [ **Android NDK开发之从环境搭建到Demo级十步流** ](https://www.cnblogs.com/guanmanman/p/6769240.html)
	2. [ **Android Studio中jni的使用** ](https://juejin.im/post/589459ed8d6d81006c4d4c9d)
	3. [ ** ( 自己使用NDK编程出现的一个问题的解决方案 ) ** ](http://www.jb51.net/article/129667.htm)


- 目录:
	
		1. .so的兼容适配
		2. C/C++ 
		3. JNI —— (java代码和本地代码(c/c++))语言交互的桥梁
		4. NDK —— 快速开发.so文件的工具


----

### .so的兼容适配

1. 兼容性

	1. arm 包的兼容 `arm64-v8a > armeabi-v7a > armeabi`
	2. X86 包的兼容 `X86_64 > X86 > armeabi`
	3. mips包的兼容 `mips64 > mips`

2. 适配性能方面
	- `armeabi`可兼容多平台架构,具有万金油特性,但会降低性能;
	- 绝大多数设备为 `arm64-v8a` ,`armeabi-v7a` 架构,可保留` v7a` 架构文件,获得更好性能;	
3. 使用:
		
		```java
		static {
			System.loadLibrary("media_jni");//.so的名称
		}
		```

4. 编写`.so`文件的步骤

	1. 配置

		```phython
		//	\app\build.gradle
		defaultConfig {
			...
			ndk {
				moduleName "JniTest" 						 //生成的.so文件的名字
				ldLibs "log", "z", "m"						//依赖的库
				abiFilters "armeabi", "armeabi-v7a", "x86"
			}
		}
		```
		```
		//	gradle.properties
		android.useDeprecatedNdk = true
		```
		```
		//	local.properties增加ndk的配置路径
		ndk.dir=xxxx/sdk/ndk-bundle
		sdk.dir=xxxx/sdk
		```
	2. 	

---
###	Android中的C/C++	

---
### JNI

---
### NDK
java是半解释型语言，很容易被反汇编后拿到源代码文件，在开发一些重要协议时，为了安全起见，使用C语言来编写这些重要的部分，来增大<font color=red>系统的**安全性**</font>。




---