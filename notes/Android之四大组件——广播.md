# BroadcastReceiver
---
<font color="red"> **`BroadcastReceiver` 没有停止的概念** </font>

![广播动态注册与订阅过程](https://i.imgur.com/tSKOkk1.png)

### BroadcastReceiver安全问题 ###
`BroadcastReceiver`设计的初衷是**从全局考虑可以方便应用程序和系统、应用程序之间、应用程序内的通信**，所以对单个应用程序而言`BroadcastReceiver`是存在安全性问题的(**恶意程序脚本不断的去发送你所接收的广播**)。

#### 发送

	通过类似sendBroadcast(Intent, String)的接口在发送广播时指定接收者
	必须具备的permission或通过Intent.setPackage设置广播仅对某个程序有效。

#### 接收
当应用程序注册了某个广播时，即便设置了IntentFilter还是会接收到来自其他应用程序的广播进行匹配判断。

	动态注册 	通过类似registerReceiver(BroadcastReceiver, IntentFilter,
		 		String, android.os.Handler)的接口指定发送者必须具备的permission。

	静态注册 	通过android:exported="false"属性表示接收者对外部应用程序不可用，
				即不接受来自外部的广播。


#### LocalBroadcastManager工具类
`android.support.v4.content.LocalBroadcastManager`工具类，可以实现在自己的进程内进行局部广播发送与注册,不用担心隐私数据泄露的问题而且高效。


	LocalBroadcastManager mLocalBroadcastManager;  
	BroadcastReceiver mReceiver;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 IntentFilter filter = new IntentFilter();  
		 filter.addAction("test");  
	
		 mReceiver = new BroadcastReceiver() {  
			@Override  
			public void onReceive(Context context, Intent intent) {  
				if (intent.getAction().equals("test")) {  
					//Do Something
				} 
			}  
		};  
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		
		//发送
		Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
		mLocalBroadcastManager.sendBroadcast(intent);
		//注册广播接收器
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);
	}
	
	
	@Override
	protected void onDestroy() {
	   mLocalBroadcastManager.unregisterReceiver(mReceiver);
	   super.onDestroy();
	} 

---