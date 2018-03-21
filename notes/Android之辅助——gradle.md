---

title: Android之 Gradle
categories: "android 总结"
tags: 
	- Gradle

---
# Gradle

	参考博客与文献:
		- https://www.cniao5.com/forum/thread/9b50d2fc11d111e782d600163e0230fa
		- http://blog.csdn.net/l664675249/article/details/50556133


项目文件结构

![项目文件结构](http://cniao5-imgs.qiniudn.com/o_1bc49l7oi1paa1am6fo919nesh57.png)

目录文件|定义
-|-
`.gradle`|gradle项目产生文件（自动编译工具产生的文件）
`.idea`|IDEA项目文件（开发工具产生的文件）
**app**|其中一个 module，复用父项目的设置，可与父项目拥有相同的配置文件
`build|自动构建时生成文件的地方
**gradle \**|自动完成 gradle 环境支持文件夹
`.gitignore`|git源码管理文件
**build.gradle**|gradle 项目自动编译的配置文件
<font color=red>**gradle.properties**</font>|gradle 运行环境配置文件
`gradlew`|自动完成 gradle 环境的 linux mac 脚本，配合 gradle 文件夹使用
`gradlew.bat`|自动完成 gradle 环境的 windows 脚本，配合 gradle 文件夹使用
`local.properties`|Android SDK NDK 环境路径配置
`*.iml`|IDEA 项目文件
**setting.gradle**|gradle 项目的子项目包含文件
---

### <font color=red>`gradle.properties`</font> in Project ###
一个配置全局的管理文件。<font color=red>**可以配置用于全局的属性的文件。**</font>
```
# Project-wide Gradle settings.

# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.

# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html

# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
# JVM 内存调整
org.gradle.jvmargs=-Xmx1536m

# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true
# Project-wide Gradle settings.

#添加ndk支持(按需添加)
android.useDeprecatedNdk=true
# 应用版本名称
VERSION_NAME=1.0.0
# 应用版本号
VERSION_CODE=100
# 支持库版本
SUPPORT_LIBRARY=24.2.1
# MIN_SDK_VERSION
ANDROID_BUILD_MIN_SDK_VERSION=14
# TARGET_SDK_VERSION
ANDROID_BUILD_TARGET_SDK_VERSION=24
# BUILD_SDK_VERSION
ANDROID_BUILD_SDK_VERSION=24
# BUILD_TOOLS_VERSION
ANDROID_BUILD_TOOLS_VERSION=24.0.3

```
配置属性使用示例:(注: "`as int`" 用于将String类型转换成int类型)

```
android {
    compileSdkVersion project.ANDROID_BUILD_SDK_VERSION as int
    buildToolsVersion project.ANDROID_BUILD_TOOLS_VERSION

    defaultConfig {
        applicationId project.APPLICATION_ID
        versionCode project.VERSION_CODE as int
        versionName project.VERSION_NAME
        minSdkVersion project.ANDROID_BUILD_MIN_SDK_VERSION as int
        targetSdkVersion project.ANDROID_BUILD_TARGET_SDK_VERSION as int
    }
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    //这里注意是双引号
    compile "com.android.support:appcompat-v7:${SUPPORT_LIBRARY}"
    compile "com.android.support:design:${SUPPORT_LIBRARY}"
    compile "com.android.support:recyclerview-v7:${SUPPORT_LIBRARY}"
    compile "com.android.support:support-annotations:${SUPPORT_LIBRARY}"
    compile "com.android.support:cardview-v7:${SUPPORT_LIBRARY}"
    compile "com.android.support:support-v4:${SUPPORT_LIBRARY}"
}

```


---
### build.gradle in Module

```
apply plugin: 'com.android.application'
//apply plugin: 'realm-android'
apply from: 'tinker-support.gradle'

```
- 描述 Gradle 所引入的插件，这里表示该 module 是一个 Android Application。插件里面包含了 Android 项目相关的所有工具。


```
android {
	//编译的 SDK 版本
    compileSdkVersion rootProject.ext.android.compileSdkVersion
	//编译工具版本 
    buildToolsVersion rootProject.ext.android.buildToolsVersion	
    defaultConfig {
        applicationId "com.billliao.fentu"
        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion
        versionCode rootProject.ext.android.versionCode
        versionName rootProject.ext.android.versionName
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled true
        manifestPlaceholders = [
                JPUSH_PKGNAME: "com.billliao.fentu",
                JPUSH_APPKEY : "f87de2dd52b40938233b7add"
        ]
        vectorDrawables.useSupportLibrary = true
        ndk {
            abiFilters 'armeabi', 'x86', 'armeabi-v7a'//, 'x86_64', 'arm64-v8a', 'mips', 'mips64'
        }
    }
    signingConfigs {
        config {
            try {
                keyAlias 'fentu'
                keyPassword 'yglfentu'
                storeFile file('C:/appkey/fentu.jks')
                storePassword 'yglfentu'
            } catch (ex) {
                throw new InvalidUserDataException(ex.toString())
            }
        }
        debug_config {
            try {
                keyAlias 'fentu'
                keyPassword 'yglfentu'
                storeFile file('C:/appkey/fentu.jks')
                storePassword 'yglfentu'
            } catch (ex) {
                throw new InvalidUserDataException(ex.toString())
            }
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.config
            buildConfigField "boolean", "LOG_DEBUG", "false"
            zipAlignEnabled true
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        debug {
            buildConfigField "boolean", "LOG_DEBUG", "true"
            versionNameSuffix "-debug"
            minifyEnabled false
            zipAlignEnabled false
            shrinkResources false
            signingConfig signingConfigs.debug_config
        }
    }
    lintOptions {
        checkReleaseBuilds false     //在打包Release版本的时候进行(多语言等)检测
        abortOnError false              //打包报错是否停止打包
    }
    packagingOptions {
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/notice.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/DEPENDENCIES.txt'
        exclude 'META-INF/DEPENDENCIES'
    }
    sourceSets {
        main {
            jniLibs.srcDirs = ['libs']
            assets.srcDirs = ['src/main/assets', 'src/main/assets/']
        }
    }
	
	//productFlavors里的配置会覆盖defaultConfig里的配置
    productFlavors {
        yingyongbao {}
        taobao {}
        slzerohelper {}
        xiaomi {}
        huawei {}
        oppo {}
        vivo {}
        productFlavors.all { flavor ->
            flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name]
        }
    }
}

```
- 描述了该 Android module 构建过程中所用到的所有参数。默认情况下，IDE 会自动创建 compileSdkVersion、buildToolsVersion 两个参数，分别对应编译的 SDK 版本和 Android build tools 版本。
- 在 android 代码块内，系统还默认创建了两个代码块——defaultConfig 和 buildTypes
	- 动态的在build时配置AndroidManifest.xml里的项目，defaultConfig里的配置可以覆盖manifest里的配置。
	- buildTypes 配置如何构建和打包你的App，默认有debug和release两个类型。debug类型包含调试时的信息，并且有debug key签名。release默认是不含签名的。




```
dependencies {
	// 依赖libs/下所有jar包
    compile fileTree(include: ['*.jar'], dir: 'libs')

	compile files('libs/alipaySdk-20170623-proguard.jar')
    compile files('libs/umeng-analytics-7.4.0.jar')
    compile files('libs/umeng-common-1.4.0.jar')
    compile files('libs/AMap3DMap_5.6.0_AMapSearch_5.5.0_AMapLocation_3.6.1_20171128.jar')

    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    testCompile 'junit:junit:4.12'

	// 远程的二进制依赖
	compile 'com.android.support:appcompat-v7:19.0.1'
    compile rootProject.ext.dependencies["appcompat-v7"]
    debugCompile rootProject.ext.dependencies["leak"]
    releaseCompile rootProject.ext.dependencies["leak_no"]
	
	//模块依赖
    compile project(':scan')

}


```
- 描述了该 Android module 构建过程中所依赖的所有库，库可以是**`以 jar 的形式`**进行依赖，或者是使用 Android 推荐的 **`aar 形式`**进行依赖。
- aar 相对于 jar 具有不可比拟的优势，不仅配置依赖更简单，而且可以将图片的资源文件放入 aar 中供主项目依赖，几乎等于依赖了源码。<font color=red>Gradle 对于依赖关系的处理，就是向调用者屏蔽所有的依赖关系，**主项目只需要依赖该 aar 库项目，而不需要知道 aar 项目对于其他库的依赖**。</font>
---

### build.gradle in Project ###

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
apply from: "dependencySetting.gradle"

//指定了使用 jcenter 代码仓库，同时声明依赖的 Android Gradle 插件的版本。
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.2'
        classpath "com.tencent.bugly:tinker-support:1.0.8"
        classpath "io.realm:realm-gradle-plugin:2.0.2"
    }
}

//为项目整体配置的一些属性。
allprojects {
    repositories {
        jcenter()
        maven {
            url "https://maven.google.com"
        }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

```



---
### `gradle \ gradle-wrapper.propertes` 配置解析  ###

```
# Mon Dec 28 10:00:20 PST 2015
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-2.14.1-all.zip


# GRADLE_USER_HOME (Windows) C:\Users\<user_name>\.gradle\；(Linux) ~/.gradle 。
# distributionBase / distributionPath ，是解压 gradle-2.14.1-all.zip 之后的文件的存放位置。
# zipStoreBase / zipStorePath ，是下载的 gradle-2.14.1-all.zip 所存放的位置。
# distributionUrl 是要下载的 gradle 的地址
# C:\Users\AndroidSchaffer\.gradle\wrapper\dists\gradle-2.14.1-all\8bnwg5hd3w55iofp58khbp6yv\gradle-2.14.1\
# 上述文件夹中的\8bnwg5hd3w55iofp58khbp6yv\层级是根据 解压路径 字符串计算 md5 值得来的


```
---
### local.properties ###
用于SDK , NDK 本地路径配置。

```
	## This file is automatically generated by Android Studio.
	# Do not modify this file -- YOUR CHANGES WILL BE ERASED!
	#
	# This file must *NOT* be checked into Version Control Systems,
	# as it contains information specific to your local configuration.
	#
	# Location of the SDK. This is only used by Gradle.
	# For customization when using a Version Control System, please read the
	# header note.
	#Fri Aug 25 13:30:56 CST 2017
	ndk.dir=D\:\\Android\\sdk\\ndk-bundle
	sdk.dir=D\:\\Android\\sdk


```
---

### setting.gradle ###

用于本地项目判断存在的`module`名称.

```
	include ':app', ':scan'

```

---

