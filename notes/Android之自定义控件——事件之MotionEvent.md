---

title: Android MotionEvent
categories: "android 总结"
tags: 
     - android
     - MotionEvent
 
---

# MotionEvent

		1. 不常用事件;
		2. 多点触控事件;
		3. 历史数据(事件批处理);
		4. 获取事件发生的时间;
		5. 获取压力(接触面积大小);
		6. 鼠标事件;
		参考自: http://www.gcssloop.com/customview/motionevent
---

事件 |	简介
-|-
`ACTION_DOWN`			|手指 初次接触到屏幕 时触发。
`ACTION_MOVE`			|手指 在屏幕上滑动 时触发，会多次触发。
`ACTION_UP`				|手指 离开屏幕 时触发。
`ACTION_CANCEL`			|事件 **被上层拦截** 时触发。
`ACTION_OUTSIDE`		|手指 **不在控件区域** 时触发。
`ACTION_POINTER_DOWN`	|有**非主要**的手指**按下**(即按下之前已经有手指在屏幕上)。
`ACTION_POINTER_UP`		|有**非主要**的手指**抬起**(即抬起之后仍然有手指在屏幕上)。


### 不常用事件之`ACTION_CANCEL` ###
**父容器在判断了事件之后对事件处理权的收回,`childView`收到`ACTION_CANCEL`不会收到后续事件。**

> `RecyclerView`：儿砸，这里有一个 `ACTION_DOWN` 你看你要不要。
> `ItemView` ：好嘞，我看看。
> `RecyclerView`：噫？居然是移动事件 `ACTION_MOVE`，我要滚起来了，儿砸，我可能要把你送去你姑父家(缓存区)了，在这之前给你一个 `ACTION_CANCEL`，你要收好啊。
> `ItemView` ：……

---

### 不常用事件之`ACTION_OUTSIDE` ###
视图所在的`WindowManager`设置了`FLAG_WATCH_OUTSIDE_TOUCH`的`flag`后才能收到的动作。

---

### 多点触控 ###
**通过多点编号完成指针判断。第一次按下的手指特殊处理作为主指针，之后按下的手指作为辅助指针。**


方法|	简介
-|-
`ev.getActionMasked()`					|与 `getAction()` 类似，**多点触控必须使用这个方法获取事件类型**。
~~`ev.getActionIndex()`~~					|~~获取该事件是哪个指针(手指)产生的。~~
`ev.getPointerCount()`					|获取在屏幕上手指的个数。
`ev.getPointerId(int pointerIndex)`		|获取一个指针(手指)的唯一标识符ID，在手指按下和抬起之间ID始终不变。
`ev.findPointerIndex(int pointerId)`	|通过`PointerId`获取到当前状态下`PointIndex`，之后通过`PointIndex`获取其他内容。
`ev.getX(int pointerIndex )`				|获取某一个指针(手指)的X坐标
`ev.getY(int pointerIndex )`				|获取某一个指针(手指)的Y坐标


> 当多个手指在屏幕上按下的时候，会产生大量的事件，如何在获取事件类型的同时区分这些事件就是一个大问题了。一般来说我们可以通过为事件添加一个int类型的index属性来区分，但是谷歌工程师是有洁癖的，<font color=blue>**为了添加一个通常数值不会超过10的index属性就浪费一个int大小的空间简直是不能忍受的**</font>，于是工程师们将这个index属性和事件类型直接合并了。int类型共32位(0x00000000)，他们用最低8位(0x000000**ff**)表示事件类型，再往前的8位(0x0000**ff**00)表示事件编号。

手指按下	| 触发事件(数值)
-|-
第1个手指按下	| `ACTION_DOWN` (0x0000**00**00)
---			|`ACTION_POINTER_DOWN`(0x0000**00**05)
第2个手指按下	| `ACTION_POINTER_2_DOWN` (0x0000**01**05)
第3个手指按下	| `ACTION_POINTER_3_DOWN` (0x0000**02**05)
第4个手指按下	| `ACTION_POINTER_4_DOWN` (0x0000**03**05)

> 注意：
> 上面表格中用粗体标示出的数值，可以看到随着按下手指数量的增加，这个数值也是一直变化的，进而导致我们使用 `getAction()` 获取到的数值无法与标准的事件类型进行对比，为了解决这个问题，他们创建了一个 `getActionMasked()` 方法，这个方法可以清除index数值，让其变成一个标准的事件类型。
**<font color=black> 1、多点触控时必须使用 `getActionMasked()` 来获取事件类型。
2、单点触控时由于事件数值不变，使用 `getAction()` 和 `getActionMasked()` 两个方法都可以。
3、使用 `getActionIndex()` 可以获取到这个index数值。不过请注意，`getActionIndex()` 只在 down 和 up 时有效，move 时是无效的。</font>**

>目前来说获取事件类型使用 `getActionMasked()` 就行了，但是如果一定要编译时兼容古董版本的话，可以考虑使用这样的写法:

```java
	final int action = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
	                ? event.getActionMasked()
	                : event.getAction();
	switch (action){
	    case MotionEvent.ACTION_DOWN:
	        // TODO
	        break;
	}
```

---
### 历史数据(事件批处理) ###
由于我们的设备非常灵敏，手指稍微移动一下就会产生一个移动事件，所以移动事件会产生的特别频繁，为了提高效率，<font color=red>**系统会将近期的多个移动事件(move)按照事件发生的顺序进行排序打包放在同一个 MotionEvent 中**</font>，与之对应的产生了以下方法：

事件 |	简介
-|-
`ev.getHistorySize()`	|获取历史事件集合大小
`ev.getHistoricalX(int pos)`	|获取第pos个历史事件x坐标 <br>`(pos < getHistorySize())`
`ev.getHistoricalY(int pos)`	|获取第pos个历史事件y坐标 <br>`(pos < getHistorySize())`
`ev.getHistoricalX (int pin, int pos)`	|获取第pin个手指的第pos个历史事件x坐标 <br>`(pin < getPointerCount(), pos < getHistorySize() )`
`ev.getHistoricalY (int pin, int pos)	`|获取第pin个手指的第pos个历史事件y坐标 <br>`(pin < getPointerCount(), pos < getHistorySize() )`
注意：
- pin 全称是 `pointerIndex`，表示第几个手指，此处为了节省空间使用了缩写。
- <font color=red> 历史数据只有 `ACTION_MOVE` 事件。</font>
- 历史数据单点触控和多点触控均可以用。

下面是官方文档给出的一个简单使用示例：

```java
	void printSamples(MotionEvent ev) {
	     final int historySize = ev.getHistorySize();
	     final int pointerCount = ev.getPointerCount();
	     for (int h = 0; h < historySize; h++) {
	         System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
	         for (int p = 0; p < pointerCount; p++) {
	             System.out.printf("  pointer %d: (%f,%f)",
	                 ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
	         }
	     }
	     System.out.printf("At time %d:", ev.getEventTime());
	     for (int p = 0; p < pointerCount; p++) {
	         System.out.printf("  pointer %d: (%f,%f)",
	             ev.getPointerId(p), ev.getX(p), ev.getY(p));
	     }
	}
```
---
### 获取事件发生的时间 ###

方法	| 简介
-|- 
`getDownTime()`						|获取手指按下时的时间。
`getEventTime()`					|获取当前事件发生的时间。
`getHistoricalEventTime(int pos)`	|获取历史事件发生的时间。

> pos 表示历史数据中的第几个数据。( pos < getHistorySize() )
> 返回值类型为 long，单位是毫秒。

---

### 获取压力(接触面积大小) ###
MotionEvent支持获取某些输入设备(手指或触控笔)的与屏幕的接触面积和压力大小，主要有以下方法：

>描述中使用了手指，触控笔也是一样的。

方法		|	简介
-|- 
`getSize ()`								|获取第1个手指与屏幕接触面积的大小
`getSize (int pin)`	|获取第pin个手指与屏幕接触面积的大小
`getHistoricalSize (int pos)`	|获取历史数据中第1个手指在第pos次事件中的接触面积
`getHistoricalSize (int pin, int pos)`		|获取历史数据中第pin个手指在第pos次事件中的接触面积
`getPressure ()`	|获取第一个手指的压力大小
`getPressure (int pin)`	|获取第pin个手指的压力大小
`getHistoricalPressure (int pos)`	|获取历史数据中第1个手指在第pos次事件中的压力大小
`getHistoricalPressure (int pin, int pos)`	|获取历史数据中第pin个手指在第pos次事件中的压力大小

> pin 全称是 pointerIndex，表示第几个手指。`(pin < getPointerCount() )`
> pos 表示历史数据中的第几个数据。`( pos < getHistorySize() )`

注意：
1. 获取接触面积大小和获取压力大小是需要硬件支持的。
2. 非常不幸的是<font color=red>**大部分设备所使用的电容屏不支持压力检测**</font>，但能够大致检测出接触面积。
3. <font color=red>大部分设备的 `getPressure()` 是使用接触面积来模拟的</font>。
4. 由于某些未知的原因(可能系统版本和硬件问题)，某些设备不支持 `getPressure()`。
5. 系统问题造成<font color=blue>**有的设备上只有` getSize() `能用，有的设备上只有 `getPressure()` 能用，而有的则两个都不能用**</font>。

由于获取接触面积和获取压力大小受系统和硬件影响，使用的时候一定要进行数据检测，以防因为设备问题而导致程序出错。

--- 
### 鼠标事件 ###
由于触控笔事件和手指事件处理流程大致相同，与鼠标相关的几个事件：

事件 |	简介
-|- 
`ACTION_HOVER_ENTER`|指针移入到窗口或者View区域，但没有按下。
`ACTION_HOVER_MOVE`	|指针在窗口或者View区域移动，但没有按下。
`ACTION_HOVER_EXIT`	|指针移出到窗口或者View区域，但没有按下。
`ACTION_SCROLL`		|滚轮滚动，可以触发水平滚动(`AXIS_HSCROLL`)或者垂直滚动(`AXIS_VSCROLL`)

注意：

1. 这些事件类型是 安卓4.0 (API 14) 才添加的。
2. 使用 ` getActionMasked()` 获得这些事件类型。
3. 这些事件不会传递到 `onTouchEvent(MotionEvent)` 而是传递到 `onGenericMotionEvent(MotionEvent)` 。

---