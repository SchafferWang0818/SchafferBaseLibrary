- Error:A problem occurred configuring project ':app'.
		> Failed to notify project evaluation listener.
		   > Tinker does not support instant run mode, please trigger build by assembleHuaweiDebug or disable instant run in 'File->Settings...'.
		   > can't find tinkerProcessHuaweiDebugManifest, you must init tinker plugin first!





- 改变build.gradle为"com.android.library"

		Error:Dependency fentu_android:zxingLibrary:unspecified on project app resolves to an APK archive which is not supported as a compilation dependency. File: D:\Workspace\fentu_android\zxingLibrary\build\outputs\apk\zxingLibrary-release-unsigned.apk


- 删除module中的applicationId

		Error:Library projects cannot set applicationId. applicationId is set to 'package_name' in default config.



- RecyclerView itemClickListener的getAdapterPosition = -1 导致的崩溃