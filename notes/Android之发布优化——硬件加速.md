# 硬件加速

	API 11 开始支持 View Canvas 使用 GPU 绘制,占用一部分 RAM ;
	API 14 以上,默认开启硬件加速;
	硬件加速影响到自定义View 和绘图操作.

## 硬件加速的级别 ##

- Application

		android:hardwareAccelerated="true"

- Activity

		android:hardwareAccelerated="true"

- Window

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,  
			   WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);  

- View

		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		//是否支持硬件加速
		View.isHardwareAccelerated()
		Canvas.isHardwareAccelerated()
		
		//Layer类型
		LAYER_TYPE_NONE:正常的使用，不会通过锁屏的缓冲区返回。
		LAYER_TYPE_HARDWARE:视图是由硬件实现为硬件机构如果应用硬件加速。
			如果应用程序没有硬件加速，这层型表现相同 LAYER_TYPE_SOFTWARE。
		LAYER_TYPE_SOFTWARE: 

			layer的类型依赖于你的需求!!

---
## 硬件加速的缺点 ##
<font color="red">

- 属性变更造成**频繁调用`invalidate()`**重新绘制所有和脏区域(重绘区域)有交集的View,**执行不必要代码**。

- 所有和脏区域(重绘区域)有交集的View的内容在**没有调用`invalidate()`情况下重绘**。可能会导致一个view只有在其它view时失效才得到正确的行为。


</font>
---












---


	参考自:
			http://blog.csdn.net/xushuaic/article/details/38975915
