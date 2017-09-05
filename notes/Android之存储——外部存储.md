# 外部存储

<font color="blue">
**外部存储包括私有目录和公有目录.
私有目录有包名存在.
公有目录使用`Environment`获取**</font>

外部存储存储权限如下: 

	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

---

### <font color="red"> 存储路径和使用方式 </font> ###

1. 公有目录


		- Environment.getExternalStoragePublicDirectory(String)





2. 私有目录

		- context.getExternalCacheDir()
		- context.getExternalFileDir(String) 








---