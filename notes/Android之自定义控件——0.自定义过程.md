---

title: View 自定义过程
categories: "android 总结"
tags: 
     - android
     - onMeasure 
     - onLayout
     - onDraw
 
---
# 自定义过程 #
包括 `View/ViewGroup` **测量布局绘制体系** 与 **事件拦截体系**。

```
	1. 测量布局绘制体系
		- onMeasure
		- onLayout
		- onDraw
	2. 事件拦截体系
		- onInterceptTouchEvent
		- onTouchEvent

```
## 测量布局绘制体系 ##

### `onMeasure()` ###

- `ViewGroup`传递测量算子给子控件,**之后可以调用子控件的`measureWidth/measureHeight`**.

	```
		measureChildren(widthMeasureSpec,heightMeasureSpec);

	```
- <font color=red face=黑体 size=5>通过判断子控件宽高和个数,和自身的 `SpecMode`判断并设置宽高参数,**建议使用默认大小或`LayoutParams`参数进行相关设置,同时考虑自身`padding`和子控件的`margin`。**</font>

	```
		int widthSpecSize  = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasureDimension(widthSpecSize,heightSpecSize);

	```



### `onLayout()` ###
- **当前函数可以使用 `measureWidth/measureHeight`.**

- 循环对子控件进行位置传递,需要叠加时需要叠加自身`padding`与 子控件的`margin`值;

	```
		//子控件的位置坐标是相对于父容器的,padding=0 时从0开始;
		childView.layout(left,top,right,bottom);

	```


### `onDraw()` ###
- **当前函数可以使用 `width/height`.**





----
## 事件拦截体系 ##

### `onInterceptTouchEvent()` ###




### `onTouchEvent()` ###



---





