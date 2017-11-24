# Notification & RemoteViews

<font color="red">** `Notification` 与 `AppwidgetProvider`桌面控件 均运行在`SystemServer`进程中.**</font>

---
### 1. PendingIntent
PendingIntent.Flag | Define
-|-
FLAG_CANCEL_CURRENT|如果要创建的PI**已经**存在，**重新创建**之前，已存PI中的**intent将不能使用 → 之前通知点击无法打开**
FLAG_NO_CREATE|如果要创建的PI**尚未**存在，**不创建而直接返回null，不单独使用**
FLAG_ONE_SHOT|相同的PI**只能使用一次**，存在相同的PI时**不会更新，后续通知点击无法打开**
FLAG_UPDATE_CURRENT|<font color="red">**相同 → 保留 → 替换原extra**</font>

	注: 
		1. 判断PI是否相等的依据:
			1. Intent是否"相同"(相同的components和intent-filter(action、data、categories、type和flags));
			2. requestCode是否一致;


---
### 2. RemoteViews

1. 支持并设置View

		Layout:
			FrameLayout,LinearLayout,RelativeLayout,GridLayout
		View:
			TextView,Button,ImageButton,ImageView,ProgressBar,
			ViewFlipper,ListView,GridView,ViewStub,
			AnalogClock,Chronometer,StackView,AdapterViewFlipper

	除了TextView,ImageView之外,可反射设置其他属性
		setInt/Long/Boolean(int,methodName,value)

2. <font color="green">**内部机制**</font>	

	`RemoteViews`被应用于通知和桌面小部件,分别使用`NotificationManager`和`AppWidgetManager`进行管理. 
	
  

---
### 3. Notification
<font color="red">**Notification使用NotificationManger创建.**</font>
1. Flags

Flags|define
:-|:-
FLAG_SHOW_LIGHTS|三色灯提醒
FLAG_ONGOING_EVENT|	发起正在运行事件（活动中）
FLAG_INSISTENT|声音振动无限循环，直到响应 （取消或者打开）
FLAG_ONLY_ALERT_ONCE|铃声和震动均只执行一次
FLAG_AUTO_CANCEL|点击自动消失
FLAG_NO_CLEAR|只有全部清除时才会清除 
FLAG_FOREGROUND_SERVICE|表示正在运行的服务

2.Default

default|define
:-|:-
DEFAULT_VIBRATE | 添加默认震动提醒 需要 VIBRATE permission
DEFAULT_SOUND | 添加默认声音提醒
DEFAULT_LIGHTS| 添加默认三色灯提醒
DEFAULT_ALL| 添加默认以上3种全部提醒


---