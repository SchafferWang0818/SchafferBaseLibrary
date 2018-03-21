---

title: Android之 外部存储
categories: "android 总结"
tags: 
	- 外部存储

---
# 外部存储
![存储方式](https://i.imgur.com/C9ggy4K.jpg)

<font color="blue">
**外部存储包括私有目录和公有目录.
私有目录有包名存在.
公有目录使用`Environment`获取**</font>

外部存储存储权限如下: 

	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

---

### <font color="red"> 存储路径和使用方式 </font> ###

-  公有目录


			Environment.getRootDirectory()
					->	/system
					//	Android的根目录

			Environment.getDataDirectory()
					->	/data	
					//	用户数据目录

			Environment.getDownloadCacheDirectory()
					->	/data/cache 
					//	下载缓存目录
	
			Environment.getExternalStorageDirectory()
					->	/storage/emulated/0
					//	主要的外部存储目录		需要使用Environment.getExternalStorageState()判断状态.

			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)			
					->	/storage/emulated/0/Alarms
	


- 私有目录,<font color="red">**清理数据和缓存的路径,应用被卸载时清理,也可自己手动清理** </font>


		- context.getExternalCacheDir()
				->/storage/emulated/0/Android/data/packageName/cache

		- context.getExternalFilesDir(Environment.DIRECTORY_ALARMS)
				->/storage/emulated/0/Android/data/packageName/files/Alarms






---


###  <font color="red">总结:

	1. 内部存储和外部存储都存在路径让其他应用读取;
	2. 内部存储/data/user/0/包名/** 或 data/data/包名/ **
	3. 外部存储文件存储都是要精确到文件夹的;
	4. 外部存储存储在外部sdCard中,使用上下文得到的是私有的,也是常清理的位置;



        showLog("Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)->"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS).getAbsolutePath());
        showLog("Environment.getDownloadCacheDirectory()->"+Environment.getDownloadCacheDirectory().getAbsolutePath());
        showLog("Environment.getDataDirectory()->"+Environment.getDataDirectory().getAbsolutePath());
        showLog("Environment.getExternalStorageDirectory()->"+Environment.getExternalStorageDirectory().getAbsolutePath());
        showLog("Environment.getRootDirectory()->"+ Environment.getRootDirectory().getAbsolutePath());
        showLog("getExternalCacheDir()->"+getExternalCacheDir().getAbsolutePath());
        showLog("getExternalFilesDir(Environment.DIRECTORY_ALARMS)->"+getExternalFilesDir(Environment.DIRECTORY_ALARMS).getAbsolutePath());
        showLog("内部存储->"+getCacheDir().getAbsolutePath());
        showLog("内部存储->"+getFilesDir().getAbsolutePath());
        Environment.getExternalStorageState();