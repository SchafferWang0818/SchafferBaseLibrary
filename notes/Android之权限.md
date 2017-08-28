# Android权限的了解和处理 #

---

	目录:
		1. 权限的了解

			- 危险权限
			- 危险权限自定义申请断后

		2. 权限处理

			1. android 6.0
			2. android 7.0
			3. android 8.0

		3. 权限的授权
		4. 其他权限


---


### 权限的了解

- 注意:
	- Android 6.0 之前的权限机制是**安装时永久性授权**;
	- 并不是所有的权限都需要运行时申请才能使用;
	- 危险权限在授予一个权限组的权限之后,组内所有的权限都被授权;

####  1. 危险权限

危险权限包含以下权限组:


	- 日历
		- READ_CALENDAR
		- WRITE_CALENDAR
	- 相机
		- CAMERA
	- 联系人
		- READ_CONTACTS
		- WRITE_CONTACTS
		- GET_ACCOUNTS
	- 定位
		- ACCESS_FINE_LOCATION
		- ACCESS_COARSE_LOCATION
	- 麦克风
		- RECORD_AUDIO
	- 电话
		- READ_PHONE_STATE
		- CALL_PHONE
		- READ_CALL_LOG
		- WRITE_CALL_LOG
		- ADD_VOICEMAIL
		- USE_SIP
		- PROCESS_OUTGOING_CALLS
	- 传感器
		- BODY_SENSORS
	- 短信
		- SEND_SMS
		- RECEIVE_SMS
		- READ_SMS
		- RECEIVE_WAP_PUSH
		- RECEIVE_MMS
	- 存储
		- READ_EXTERNAL_STORAGE
		- WRITE_EXTERNAL_STORAGE


1. 检测权限是否为允许:**返回值为0时为已允许,1时为拒绝或未申请**;
	
		- Activity#checkSelfPermission(permission)
		- PackageManager#checkPermission(permission, getPackageName()))
		
2. 申请权限

		- Activity#requestPermissions(permissions, requestCode);
		- ActivityCompat.requestPermissions(this, permissions, requestCode);

3. 权限请求结果返回

		- Activity#onRequestPermissionsResult(int requestCode,
			 @NonNull String[] permissions,
			 @NonNull int[] grantResults) 


4. 权限说明:只有当用户拒绝时没有选中"不在提示"时才会返回true;


		- Activity#shouldShowRequestPermissionRationale(@NonNull String permission)
		- ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])


#### 2. 危险权限自定义申请断后 

	String[] dangerousPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE//读写权限0
            , Manifest.permission.CAMERA//相机权限1
            , Manifest.permission.WRITE_CONTACTS//写入联系人2
            , Manifest.permission.CALL_PHONE//打电话3
            , Manifest.permission.SEND_SMS//发送短信4
            , Manifest.permission.RECORD_AUDIO//麦克风打开5
            , Manifest.permission.ACCESS_FINE_LOCATION//定位相关6
            , Manifest.permission.BODY_SENSORS//传感器 7  最小使用SDK 20
            , Manifest.permission.WRITE_CALENDAR//日历写入8
    };

     void requestPermission(String... permissions) {
        if (permissions.length > 1) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
        }
    }

    /**
     * 请求运行时权限
     *
     * @param listener
     * @param permissions
     */
    protected void requestPermission(PermissionResultListener listener, String... permissions) {
        permissionResultListener = listener;
        requestPermission(permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {//单个权限申请结果
            if (grantResults.length == 0) return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限申请成功
                if (permissionResultListener != null)
                    permissionResultListener.onSinglePermissionGranted(permissions[0]);
            } else {//权限申请失败
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {//已点不再询问
                    showSnackbar("权限已被禁止,并不再询问,请在设置中打开", Snackbar.LENGTH_LONG);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("是否打开应用设置页面?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAppDetailSettingIntent();
                        }
                    }).setNegativeButton("取消", null).create().show();
                } else {//再次询问?
                    if (permissionResultListener != null)
                        permissionResultListener.onSinglePermissionDenied(permissions[0]);
                }
            }
        }
        if (requestCode == REQUEST_CODE_PERMISSIONS) {//多个权限申请结果
            if (grantResults.length == 0) return;
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (deniedPermissions.size() != 0) {//有权限未允许
                if (permissionResultListener != null)
                    permissionResultListener.onPermissionsDenied(deniedPermissions);
            } else {//权限已被完全允许
                if (permissionResultListener != null)
                    permissionResultListener.onPermissionsGrantedAll();
            }
        }
    }

    PermissionResultListener permissionResultListener;

    interface PermissionResultListener {
        void onSinglePermissionDenied(String permission);

        void onSinglePermissionGranted(String permission);

        void onPermissionsGrantedAll();

        void onPermissionsDenied(List<String> deniedPermissions);

    }


    /**
     * 打开应用设置界面
     */
    private void getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

	@Override
    protected void onDestroy() {
        super.onDestroy();

        //...其他操作

    }


#### 3. 特殊权限

- SYSTEM_ ALERT_ WINDOW

	在 android api 23以上需要判断是否可以在其他应用上层显示弹框,需要手动申请跳转界面

		//判断是否可以上层显示并跳转
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&& !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,10);
        }


		
		
		
- WRITE_ SETTINGS
	


### 权限处理

####  android 6.0
####  android 7.0
####  android 8.0


### 权限的授权



### 其他权限 ###
