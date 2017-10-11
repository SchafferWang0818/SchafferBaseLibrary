#Intent Flag


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



## 常用Flag ##
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
