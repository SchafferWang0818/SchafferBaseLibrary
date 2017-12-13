#Intent 

Intent 常用构造方法：

| constructor | DEFINE |
|:----|:-----|
|`Intent() `| 构造一个空 Intent |
|`Intent(String action)`| 构造一个指定 action 的 Intent |
|`Intent(String action，Uri uri)`| 构造一个指定 action 和 uri（相当于同时设定了 data）的 Intent|
|`Intent(Context packageContext，Class<?> cls)`| 构造一个指定目标组件的 Intent，显式 Intent 的主要构造方法 |

除了`contentProvider`之外的三大组件需要的桥梁.
`Intent` 与 `IntentFilter` 有一定的联系。

## 组件
![Intent包含信息](http://upload-images.jianshu.io/upload_images/5064136-90a90f76a05437fa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)



常用的设定信息方法：

| 方法 | 描述 |
|:----|:-----|
|`setAction(String action)`|指定 action|
|`setClass(Context packageContext, Class<?> cls)`|指定目标组件类名|
|`setData(Uri data)`|设置 Data 的 uri|
|`setType(String type)`|设置 Data 的 MIME 类型|
|**`setDataAndType(Uri data, String type)`**|**同时设置 Data 的 uri 与 MIME 类型**|
|`addCategory(String category)`|添加一项 Category，**Intent 可有多个 Category**|
|`addFlags(int flags)`|**决定目标组件的启动方式**|
|`putExtra(String name, 基本类型/序列化 value)`|放入附加数据，参 2 可以是各种基本类型，及序列化后的自定义类|
|`putExtras(Bundle extras)`|把封装了数据信息的 Bundle 对象放入 Intent|

* <font color=red>若要同时设置 URI 和 MIME 类型，请勿调用 setData() 和 setType()，因为它们会**互相抵消彼此的值**。请始终使用 setDataAndType() 同时设置 URI 和 MIME 类型。</font>


### Action

[**常见Intent Action 使用案例 点击此处**](http://blog.csdn.net/ithomer/article/details/8242471)

TYPE|CONTENT|DEFINE
:-:|:-|:-
String|"android.intent.action.ADD_SHORTCUT"|动作：在系统中添加一个快捷方式。.
String|"android.intent.action.ALL_APPS"|动作：列举所有可用的应用。
String|"android.intent.action.ANSWER"|动作：处理拨入的电话。
String|"android.intent.action.BUG_REPORT"|动作：显示 activity 报告错误。
String|"android.intent.action.CALL"|动作：拨打电话，被呼叫的联系人在数据中指定。
String|"android.intent.action.CLEAR_CREDENTIALS"|动作：清除登陆凭证 (credential)。
String|"android.intent.action.DELETE"|动作：从容器中删除给定的数据。
String|"android.intent.action.DIAL"|动作：拨打数据中指定的电话号码。
String|"android.intent.action.EDIT"|动作：为制定的数据显示可编辑界面。
String|"android.intent.action.EMERGENCY_DIAL"|动作：拨打紧急电话号码。
String|"android.intent.action.LOGIN"|动作：获取登录凭证。
String|"android.intent.action.MAIN"|动作：作为主入口点启动，不需要数据。
String|"android.intent.action.PICK"|动作：从数据中选择一个项目item，将被选中的项目返回。
String|"android.intent.action.PICK_ACTIVITY"|动作：选择一个activity，返回被选择的activity的类名
String|"android.intent.action.RUN"|动作：运行数据（指定的应用），无论它（应用）是什么。
String|"android.intent.action.SENDTO"|动作：向 data 指定的接收者发送一个消息。
String|"android.intent.action.GET_CONTENT"|动作：让用户选择数据并返回。
String|"android.intent.action.INSERT"|动作：在容器中插入一个空项 (item)。
String|"android.intent.action.SETTINGS"|动作：显示系统设置。输入：无。
String|<font color=red>"**android.intent.action.VIEW**"</font>|动作：向用户显示数据。浏览网页等程序调用
String|"android.intent.action.WALLPAPER_SETTINGS"|动作：显示选择墙纸的设置界面。输入：无。
String|"android.intent.action.WEB_SEARCH"|动作：执行 web 搜索。
String|"android.intent.action.SYNC"|动作：执行数据同步。
String|"android.intent.action.SERVICE_STATE"|广播：电话服务的状态已经改变。
String|"android.intent.action.TIMEZONE_CHANGED"|广播：时区已经改变。
String|"android.intent.action.TIME_SET"|广播：时间已经改变（重新设置）。
String|"android.intent.action.TIME_TICK"|广播：当前时间已经变化（正常的时间流逝）。
String|"android.intent.action.UMS_CONNECTED"|广播：设备进入 USB 大容量存储模式。
String|"android.intent.action.UMS_DISCONNECTED"|广播：设备从 USB 大容量存储模式退出。
String|"android.intent.action.WALLPAPER_CHANGED"|广播：系统的墙纸已经改变。
String|"android.intent.action.XMPP_CONNECTED"|广播：XMPP 连接已经被建立。
String|"android.intent.action.XMPP_DI|广播：XMPP 连接已经被断开。
String|"android.intent.action.SIG_STR"|广播：电话的信号强度已经改变。
String|"android.intent.action.BATTERY_CHANGED"|广播：充电状态，或者电池的电量发生变化。
String|"android.intent.action.BOOT_COMPLETED"|广播：在系统启动后，这个动作被广播一次（只有一次）
String|"android.intent.action.DATA_ACTIVITY"|广播：电话的数据活动(data activity)状态已经改变
String|"android.intent.action.DATA_STATE"|广播：电话的数据连接状态已经改变。
String|"android.intent.action.DATE_CHANGED"|广播：日期被改变。
String|"android.server.checkin.FOTA_CANCEL"|广播：取消所有被挂起的 (pending) 更新下载。
String|"android.server.checkin.FOTA_INSTALL"|广播：更新已经被确认，马上就要开始安装。
String|"android.server.checkin.FOTA_READY"|广播：更新已经被下载，可以开始安装。
String|"android.server.checkin.FOTA_RESTART"|广播：恢复已经停止的更新下载。
String|"android.server.checkin.FOTA_UPDATE"|广播：通过 OTA 下载并安装操作系统更新。
String|"android.intent.action.MEDIABUTTON"|广播：用户按下了“Media Button”。
String|"android.intent.action.MEDIA_BAD_REMOVAL"|广播：扩展卡从SD卡插槽拔出，但是挂载点还没unmount。
String|"android.intent.action.MEDIA_EJECT"|广播：用户想要移除扩展介质（拔掉扩展卡）。
String|"android.intent.action.MEDIA_MOUNTED"|广播：扩展介质被插入，而且已经被挂载。
String|"android.intent.action.MEDIA_REMOVED"|广播：扩展介质被移除。
String|"android.intent.action.MEDIA_SCANNER_FINISHED"|广播：已经扫描完介质的一个目录。
String|"android.intent.action.MEDIA_SCANNER_STARTED"|广播：开始扫描介质的一个目录。
String|"android.intent.action.MEDIA_SHARED"|广播：扩展介质的挂载被解除 (unmount)
String|"android.intent.action.MEDIA_UNMOUNTED"|广播：扩展介质存在，但是还没有被挂载 (mount)。
String|"android.intent.action.MWI"|广播：电话的消息等待（语音邮件）状态已经改变。
String|**"android.intent.action.PACKAGE_ADDED"**|广播：设备上新安装了一个应用程序包。
String|**"android.intent.action.PACKAGE_REMOVED"**|广播：设备上删除了一个应用程序包。
String|"android.intent.action.PHONE_STATE"|广播：电话状态已经改变。
String|"android.intent.action.PROVIDER_CHANGED"|广播：更新将要（真正）被安装。
String|"android.intent.action.PROVISIONING_CHECK"|广播：要求provisioning service下载最新的设置
String|**"android.intent.action.SCREEN_OFF"**|广播：屏幕被关闭。
String|**"android.intent.action.SCREEN_ON"**|广播：屏幕已经被打开。
String|"android.intent.action.NETWORK_TICKLE_RECEIVED"|广播：设备收到了新的网络 "tickle" 通知。
String|"android.intent.action.STATISTICS_REPORT"|广播：要求 receivers 报告自己的统计信息。
String|"android.intent.action.STATISTICS_STATE_CHANGED"|广播：统计信息服务的状态已经改变。
String|"android.intent.action.CFF"|广播：语音电话的呼叫转移状态已经改变。
String|"android.intent.action.CONFIGURATION_CHANGED"|广播：设备的配置信息已经改变，参见 Resources.Configuration
int|1=0x00000001|启动标记：设置以后，新的 activity 不会被保存在历史堆栈中。
int|2=0x00000002|启动标记：设置以后，如果 activity 已经启动，而且位于历史堆栈的顶端，将不再启动（不重新启动） activity。
int|4=0x00000004|启动标记：设置以后，activity 将成为历史堆栈中的第一个新任务（栈顶）。
int|8=0x00000008|启动标记：和 NEW_TASK_LAUNCH 联合使用，禁止将已有的任务改变为前景任务 (foreground)。
int|16=0x00000010|启动标记：如果这个标记被设置，而且被一个已经存在的 activity 用来启动新的 activity，已有 activity 的回复目标 (reply target) 会被转移给新的 activity。

### Data 

- `URI`：待操作数据的引用 uri；
- `MIME（mimeType）`：待操作数据的数据类型。
[
两部分均为可选，但是要注意同时设置时应该使用 setDataAndType()方法，防止互相抵消。
**Data 内容一般由 action 决定**。例如：action 为 ACTION_VIEW，那么 Data 就可以是一个网址，也可以是图片之类的数据 uri。
同时指定 Uri 和 MIME 类型有助于 Android 系统找到接收 Intent 的最佳组件，例如：可以响应 ACTION_VIEW 的组件可能有非常多，浏览器、播放器、图片应用等等。此时设置mimeType为"image/jpeg"、"video/mp4"，则系统可以筛选出更合适的响应组件。](http://www.jianshu.com/p/19147a69e970)

- data 与 Intent-Filter 的 data 标签
	- path 和 mimeType 允许使用 * 通配符，实现部分匹配。


		Uri uri=Uri.parse("content://com.example.project:200/folder/subfolder/etc");
		<intent-filter>
	       <data 
	           android:scheme="content" android:host="com.example.project" 
	           android:port="200" android:path="/folder/subfolder/etc"/>
		</intent-filter>


###	Category
TYPE|CONTENT|DEFINE
:-:|:-|:-
String|"android.intent.category.ALTERNATIVE"|类别：说明activity是用户正在浏的数据的一个可选操作。
String|"android.intent.category.WALLPAPER"|类别：这个 activity 能过为设备设置墙纸。
String|"android.intent.category.UNIT_TEST"|类别：应该被用作单元测试（通过 test harness 运行）。
String|"android.intent.category.TEST"|类别：作为测试目的使用，不是正常的用户体验的一部分。
String|"android.intent.category.TAB"|类别：activity应该在TabActivity中作为一个tab使用
String|"android.intent.category.SAMPLE_CODE"|类别：To be used as an sample code example (not part of the normal user experience).
String|"android.intent.category.PREFERENCE"|类别：activity是一个设置面板 (preference panel)。
String|"android.intent.category.HOME"|类别：主屏幕 (activity)，设备启动后显示的第一个 activity。
String|**"android.intent.category.BROWSABLE"**|类别：能够被浏览器安全使用的 activities 必须支持这个类别。
String|"android.intent.category.DEFAULT"|类别：如果 activity 是对数据执行确省动作（点击, center press）的一个选项，需要设置这个类别。
String|"android.intent.category.DEVELOPMENT_PREFERENCE"|类别：说明 activity 是一个设置面板 (development preference panel).
String|"android.intent.category.EMBED"|类别：能够在上级（父）activity 中运行。
String|"android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST"|类别：To be used as code under test for framework instrumentation tests.
String|"android.intent.category.GADGET"|类别：这个 activity 可以被嵌入宿主 activity (activity that is hosting gadgets)。
String|**"android.intent.category.LAUNCHER"**|类别：Activity 应该被显示在顶级的 launcher 中。
String|"android.intent.category.SELECTED_ALTERNATIVE"|类别：对于被用户选中的数据，activity 是它的一个可选操作。
### Extra
TYPE|CONTENT|DEFINE
:-:|:-|:-
String|"android.intent.extra.INTENT"|附加数据：和 PICK_ACTIVITY_ACTION 一起使用时，说明用户选择的用来显示的 activity；和 ADD_SHORTCUT_ACTION 一起使用的时候，描述要添加的快捷方式。
String|"android.intent.extra.LABEL"|附加数据：大写字母开头的字符标签，和 ADD_SHORTCUT_ACTION 一起使用。
String|"android.intent.extra.TEMPLATE"|附加数据：新记录的初始化模板。

## Flag

### 常用Flag 
1. `FLAG_ACTIVITY_BROUGHT_TO_FRONT` = singleTask
2. `FLAG_ACTIVITY_CLEAR_TASK`:和`FLAG_ACTIVITY_NEW_TASK`配合使用相关联的task清空其他的activity;
3. `FLAG_ACTIVITY_CLEAR_TOP`:
	1. `FLAG_ACTIVITY_SINGLE_TOP`或者设置了启动模式时,activity存在,就清空顶部的其他activity,intent交给onNewIntent();
	2. 默认启动模式时自己也会被finish掉,然后重新创建Activity实例.

4. `FLAG_ACTIVITY_SINGLE_TOP` = singleTop:栈中存在实例会用onNewIntent();
5. `FLAG_ACTIVITY_NEW_TASK`:*当启动的activity存在launchMode = singleTask时,*和不使用addFlags效果相同.

		1. 当要启动的Activity设置了亲缘关系affinity并与默认包名不同时,会启动一个新的任务栈来放新的Activity;
		2. 当要启动的Activity设置了亲缘关系affinity并与当前栈对应的包名相同时,
			1. 这个Activity在栈中存在,需要`addFlags(FLAG_ACTIVITY_CLEAR_TOP)`来启动已经存在的Activity.
			2. 

4. `FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET`->API 21->`FLAG_ACTIVITY_NEW_DOCUMENT`
5. `FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS`:启动的Activity在栈中不保存记录.
6. `FLAG_ACTIVITY_FORWARD_RESULT` = startActivityForResult() + setResult()
### 1`.FLAG_GRANT_READ_URI_PERMISSION` ###
	临时访问读权限  intent的接受者将被授予 INTENT 数据uri 或者 在ClipData 上的读权限。

### 2.`FLAG_GRANT_WRITE_URI_PERMISSION` ###
	临时访问写权限 intent的接受者将被授予 INTENT 数据uri 或者 在ClipData 上的写权限。

### 3.`FLAG_GRANT_PERSISTABLE_URI_PERMISSION` ###
	区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现 。

###  4.`FLAG_GRANT_PREFIX_URI_PERMISSION` ###
	权限授予任何原始授权URI前缀匹配的URI。

### 5.`FLAG_DEBUG_LOG_RESOLUTION` ###
解析intent时打印log messages，展示创建最终的resolved list 找到的信息 。比如有如下代码  ：

		Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
		
		intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		
		sendBroadcast(intent);

将会按照优先级打印出系统所有注册"`android.provider.Telephony.SMS_RECEIVED`"的广播接收者。

### 6.`FLAG_FROM_BACKGROUND` ###
	指明Intent来自后台操作 ，不是来自用户直接互动。

### 7.`FLAG_ACTIVITY_BROUGHT_TO_FRONT` ###
	通常不是通过应用程序代码设置，而是通过系统如launchMode singleTask模式。

### 8.`FLAG_ACTIVITY_CLEAR_TASK` ###
	如果在通过Context.startActivity()启动activity时为Intent设置了此标识，这个标识将导致：任何与此activity相关联的task都会被清除。也就是说， 此activity将变成一个空栈中新的最底端的activity，所有的旧activity都会被finish掉，这个标识仅仅和FLAG_ACTIVITY_NEW_TASK联合起来才能使用。

### 9.`FLAG_ACTIVITY_CLEAR_TOP` ###
	当设置此标致，并且acitivity已经启动，那么不是启动一个新的activity，所有其他顶部的activity都会关闭，这个intent将被交付到（现在顶部）老的activity 做为新的intent。如果一个task由A,B,C,D组成，如果D调用startActivity（），跳到B, 然后C,D被finish掉，B接收新的intent  ，结束栈中：A,B.现在运行的B的实例或者在onNewIntent方法中接收你start的新intent，或者自己finish掉然后重启一个新的intent。如果声明启动了启动模式是“multiple”(默认)，并且你没有在这个intent中设置FLAG_ACTIVITY_SINGLE_TOP，就会finish掉然后重新创建。其他的启动模式。或者FLAG_ACTIVITY_SINGLE_TOP被设置了，intent将会传送到当前实例的onNewIntent方法中。这个启动模式也可以跟FLAG_ACTIVITY_NEW_TASK结合使用：如果用来start根activity，它将会在此task任务当前正在执行的实例bring to foreground，然后清除到跟状态。比如，当从notification manager启动一个activity。

### 10.`FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET` ###
	API21过期，被FLAG_ACTIVITY_NEW_DOCUMENT代替。

### 11.`FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS` ###
	如果设置，新的Activity不会在最近启动的Activity的占中保存。

### 12.`FLAG_ACTIVITY_FORWARD_RESULT` ###
	如果设置，并且这个Intent用于从一个存在的Activity启动一个新的Activity，那么，这个作为答复目标的Activity将会传到这个新的Activity中。这种方式下，新的Activity可以调用setResult(int)，并且这个结果值将发送给那个作为答复目标的Activity。

### 13.`FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY` ###
	这个标记通常不由应用程序代码来设置，如果是从历史中启动这个Activity，系统就会设置这个标记(长按home键) 。

###  14.`FLAG_ACTIVITY_MULTIPLE_TASK` ###
	可以跟FLAG_ACTIVITY_MULTIPLE_TASK结合使用，当只用自己的时候相当于Manifast中android.R.attr.documentLaunchMode="intoExisting"，当跟FLAG_ACTIVITY_MULTIPLE_TASK结合使用相当于 Manifast中android.R.attr.documentLaunchMode="always".

### 15.`FLAG_ACTIVITY_NEW_DOCUMENT` ###
	默认情况FLAG_ACTIVITY_NEW_DOCUMENT创建的document当用户关闭时之前tasks的entry会被remove掉，如果想保持在历史中一遍重新launch，就要用到这个flag.当使task的activity finish掉以后，历史entry将保持在界面以便用户重新打开类似顶级应用程序的历史。

###  16.`FLAG_ACTIVITY_NEW_TASK` ###
	如果设置了，这个Activity将会成为新任务历史栈的开始，如果已经有一个task运行着邀请新的activity，将不会启动新的activity；当前任务栈最后状态将会被展示在屏幕上查看FLAG_ACTIVITY_MULTIPLE_TASK ，关闭这一特性。

### 17.`FLAG_ACTIVITY_NO_ANIMATION` ###
	如果设置，将阻止系统get next activity的过渡动画。并不意味着一直不会有动画，如果另一个activity 的变化发生没有在start activity 显示之前指定，会有过渡动画。

###  18.`FLAG_ACTIVITY_NO_HISTORY` ###
	如果设置，新的activity将不会保存在历史栈中。一旦用户离开这个activity，它就会被finish掉。也可以在manifest.xml中设置activity android:hoHistory属性设置。如果设置， OnActivityResult()方法将不会再被调用 。

###  19.`FLAG_ACTIVITY_NO_USER_ACTION` ###
	onUserLeaveHint()作为activity周期的一部分，它在activity因为用户要跳转到别的activity而要退到background时使用。比如,在用户按下Home键，它将被调用。比如有电话进来（不属于用户的选择），它就不会被调用。如果设置，作为新启动的Activity进入前台时，这个标志将在Activity暂停之前阻止从最前方的Activity回调的onUserLeaveHint()。典型的，一个Activity可以依赖这个回调指明显式的用户动作引起的Activity移出后台。这个回调在Activity的生命周期中标记一个合适的点，并关闭一些Notification。 如果一个Activity通过非用户驱动的事件，如来电或闹钟，启动的，这个标志也应该传递给Context.startActivity，保证暂停的Activity不认为用户已经知晓其Notification。

### 20.`FLAG_ACTIVITY_PREVIOUS_IS_TOP `###
	如果给Intent对象设置了这个标记，并且这个Intent对象被用于从一个既存的Activity中启动一个新的Activity，这个Activity不被看作决定是否传送新的intent到top而不是start新的，通常认为使用这个flag启动的Activity会被自己立即终止。

### 21.`FLAG_ACTIVITY_RESET_TASK_IF_NEEDED` ###
	FLAG_ACTIVITY_RESET_TASK_IF_NEEDED:如果设置该属性，并且这个activity在一个新的task中正在被启动或者被带到一个已经存在的task的顶部，这时这个activity将会被作为这个task的首个页面加载。这将会导致拥有这个应用的affinities的task处于一个合适的状态(移动activity到这个task或者activity从中移出)，或者简单的重置这个task到它的初始状态

### 22.`FLAG_ACTIVITY_REORDER_TO_FRONT` ###
	如果在intent里设置交给 startActivity（）,这个flag会把已经运行过的acivity带到task历史栈的顶端。例如，一个task由A,B,C,D四个activity组成，如果D携带这个flag的intent调用startActivity()打开B，那么B就会被带到历史栈的前部，结果是:A,C,D,B.如果LAG_ACTIVITY_CLEAR_TOP 被设置，那么FLAG_ACTIVITY_REORDER_TO_FRONT将被忽略。

### 23.`FLAG_ACTIVITY_SINGLE_TOP` ###
	如果设置了，如过Activity在栈顶将不会启动。

###  24.`FLAG_ACTIVITY_TASK_ON_HOME` ###
	把当前新启动的任务置于Home任务之上，也就是按back键从这个任务返回的时候会回到home，即使这个不是他们最后看见的activity，注意这个标记必须和FLAG_ACTIVITY_NEW_TASK一起使用

### 25.`FLAG_RECEIVER_REGISTERED_ONLY` ###
	设置这个flag，发送广播只有动态注册才能调用，组件(xml 中定义action)不会被被launch

http://www.jianshu.com/p/08177910b0a2

---


