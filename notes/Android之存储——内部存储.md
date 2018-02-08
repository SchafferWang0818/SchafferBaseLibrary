# 内部存储
![存储方式](https://i.imgur.com/C9ggy4K.jpg)
### <font color="red"> 描述&注意事项 </font> ###
- 内部存储是只有当前应用可以访问到的存储方式,SP有能被外部访问的模式;
- 应用卸载后内部存储内容都将被删除;
- 包括`sharedPreferences`,`SQLite`;


### <font color="red"> 路径 </font> ###


	- /data/app: 							存放着所有安装的app的apk文件

	- /data/data/包名/shared_prefs 				APP的SP信息
	- /data/data/包名/databases 					APP的数据库信息
	- /data/data/包名/files						APP的文件信息
	- /data/data/包名/cache 						APP的缓存信息



注意: 没有root的手机不能打开内部存储的文件夹;


### <font color="red"> 使用方式 </font> ###


	内部存储的文件存储位置:	context.getFileDir();
	内部存储的缓存存储位置:	context.getCacheDir();
