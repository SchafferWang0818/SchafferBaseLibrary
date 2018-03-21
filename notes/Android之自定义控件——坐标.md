---

title: Android 坐标与参数/对应函数
categories: "android 总结"
tags: 
     - android
     - View坐标
     - 参数/对应函数
 
---

# View之坐标与参数/对应函数

---
	
	目录:

		1. 坐标与坐标系
			- mLeft:				以父容器左上为坐标原点,左上角X值
			- mRight:				以父容器左上为坐标原点,右下角X值
			- mTop:					以父容器左上为坐标原点,左上角Y值
			- mBottom:				以父容器左上为坐标原点,右下角Y值

			注意: offsetLeftAndRight(int offset)与offsetTopAndBottom(int offset)
			用于View的平移.直接改变以上四个值

			- translationX:			以父容器左上为坐标原点,X偏移量		
			- translationY:			以父容器左上为坐标原点,Y偏移量

			注意: 偏移量使用setTranslationX/Y(float),原相对于父容器的坐标不会发生变化,
			View通过坐标与偏移量的叠加进行绘制.

			- MotionEvent#getX():	以当前View左上为坐标原点,触摸点X值
			- MotionEvent#getY():	以当前View左上为坐标原点,触摸点Y值
			- MotionEvent#getRawX():以屏幕左上为坐标原点,触摸点X值
			- MotionEvent#getRawY():以屏幕左上为坐标原点,触摸点Y值

			注意: 与触摸事件相关.

			- mScrollX:				与View位置无关,View内容相对于原状态X方向的滚动量
			- mScrollY:				与View位置无关,View内容相对于原状态Y方向的滚动量
	
			注意: 向上/左,滚动量为正;反之为负.

			- mPaddingLeft
			- mPaddingRight
			- mPaddingTop
			- mPaddingBottom


	
		2. 参数与函数
			- width:					   getRight() - getLeft()
			- height:					  getBottom() - getTop()
			- measureWidth:				mMeasuredWidth & MEASURED_SIZE_MASK
			- measureHeight:			   mMeasuredHeight & MEASURED_SIZE_MASK

			注: >>> width/height 与 measureWidth/measureHeight 区别在于赋值时机不同,
				>>> measureWidth/measureHeight 形成于 measure 过程;
				>>> width/height 形成于 layout 过程;

			- getLocalVisibleRect(Rect)
			- getGlobalVisibleRect(Rect,Point)
			- getLocationOnScreen(int[2])
			- getLocationInWindow(int[2])



---

### 坐标与坐标系
1. 坐标示意图如下:

	![坐标示意图](http://upload-images.jianshu.io/upload_images/3551332-ceab93bf82f135a8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2. 角度示意图如下:

	![角度坐标](http://upload-images.jianshu.io/upload_images/3846387-89827b3403db8137.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3. 滚动量值示意图如下:

	![滚动量值](http://upload-images.jianshu.io/upload_images/1302497-a65d8640ea8029cb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---

### 参数与函数 ###

#### 1. 获得View相对于父容器/屏幕原点的偏移量 ####

		来源链接：http://www.jianshu.com/p/f09541b2f43b

- `getLocalVisibleRect(Rect)`: 获取View在其父控件中的可见区域相对于此View的左顶点的距离（偏移量）；
```
		Rect localRect = new Rect();
        v.getLocalVisibleRect(localRect);
        ((TextView) findViewById(R.id.local)).setText("local" + localRect.toString());
		//显示左上和右下两个点的相对坐标
```

- `getGlobalVisibleRect(Rect,Point)`: 获取View在其父控件中的可见区域相对于屏幕左顶点的距离（偏移量）；
```
		Rect globalRect = new Rect();
        Point globalOffset = new Point();
        v.getGlobalVisibleRect(globalRect, globalOffset);
        ((TextView) findViewById(R.id.global)).setText("global" + globalRect.toString());
        ((TextView) findViewById(R.id.offset)).setText("globalOffset:" + globalOffset.x + "," + globalOffset.y);
```


#### 2. 绝对坐标 ####
	
		来源链接：http://www.jianshu.com/p/665449c78c74

- `getLocationOnScreen(int[2])`：计算该视图在Android坐标系中的x，y值，获取在当前屏幕内的绝对坐标（这个值是要从屏幕顶端算起，当然包括了通知栏和状态栏的高度）

- `getLocationInWindow(int[2])`: 计算该视图在它所在的window的坐标x，y值，获取在整个window的绝对坐标


```
		例如:

		    int[] location = new int[2];
		    view.getLocationOnScreen(location);
		    int x = location[0];
		    int y = location[1];

```

---