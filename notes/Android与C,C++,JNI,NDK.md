#  Android与C,C++,JNI,NDK


----

	目录:
	
		1. .so的兼容适配
		2. Android中的C/C++
		3. JNI —— 语言交互的桥梁
		4. NDK —— 快速开发.so文件的工具


----

### .so的兼容适配

1. 兼容性

	1. arm 包的兼容

			arm64-v8a > armeabi-v7a > armeabi

	2. X86 包的兼容

			X86_64 > X86 > armeabi

	3. mips包的兼容

			mips64 > mips

2. 适配

		1. armeabi可兼容多平台架构,具有万金油特性,但会降低性能;
		2. 绝大多数设备为 arm64-v8a ,armeabi-v7a 架构,可保留 v7a 架构文件,获得更好性能;
		
3. 使用

	1. 在jniLibs下:

			static {
                   System.loadLibrary("media_jni");//.so的名称
            }

	2. 放在libs 下:

			https://juejin.im/post/589459ed8d6d81006c4d4c9d

---
###	Android中的C/C++	





---
### JNI




---
### NDK





---