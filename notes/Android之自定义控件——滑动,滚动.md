---

title: Android 滑动 & 滚动
categories: "android 总结"
tags: 
     - android
     - 滑动
     - 滚动
 
---
# 滑动 & 滚动

```
	滑动:

		- translationX / translationY : 动画更改原位置的相对位置的虚假效果;

		- 布局参数动态更改

			- left/right/top/bottom:

				- layout(getLeft()+offsetX,...);

				- offsetLeftAndRight(offsetX),offsetTopAndBottom(offsetY);

			- (Margin)LayoutParams 对View本身的动态增加的真实效果;

				- setMargins(offsetX,...),效果等同于offsetLeftAndRight()...;

				- lp.leftMargin = getLeft()+offsetX...,效果等同于layout();

	滚动:
		- scrollTo()/scrollBy() : 对View内部的滚动位置的更改;
			触发OnScrollChangeListener#onScrollChange
			(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
			

		- Scroller

```
---

### scrollTo()/scrollBy()  ###

<font color=red>**`scrollTo(x,y)`理解为将内容`(x,y)`的坐标移动到左上角;
`scrollBy(offsetX,offsetY)`理解为向左移动`offsetX`个单位,向上移动`offsetY`个单位。 **</font>


---
### Scroller ###
`Scroller`通过调用`Scroller # startScroll(startX,startY,offsetX,offsetY,duration)`存储滚动数据,`View # invalidate()`重绘,在`onDraw()`中调用`View # computeScroll()`获取当前的`scrollX`,`scrollY`,使用scrollTo()完成滑动,重绘进行重复操作,直到完成整个滑动.

```
	//设置滚动信息
	Scroller scroller = new Scroller(getContext());
    scroller.startScroll(startX,startY,offsetX,offsetY,duration);
    invalidate();

	/* 完成滚动并重绘直到完成滑动整个过程 */
	@Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            postInvalidate();
        }
    }
```

---