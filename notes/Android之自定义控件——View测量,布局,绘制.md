# 自定义控件的测量,布局,绘制过程 #

	目录:
		- 测量
			1. 测量之MeasureSpec与LayoutParams
			2. View # onMeasure(int,int)
			3. ViewGroup # measureChildren()
			4. 测量某个View的宽高的方式
		- 布局

		- 绘制

	参考自:
			《 Android 开发艺术探索 》- Page 174
			

---
### 测量 ###


#### 1. 测量之`MeasureSpec`与`LayoutParams` ####

[**`WindowManager$LayoutParams` 友情链接 点击此处** ](https://github.com/SchafferWang0818/SchafferBaseLibrary/blob/master/notes/Android%E4%B9%8B%E6%98%BE%E7%A4%BA%E2%80%94%E2%80%94WindowManager.md#windowmanagerlayoutparams)

> 《 Android 开发艺术探索 》
> 对于 `DecorView` ，其 `MeasureSpec` 由窗口的尺寸和其自身的 `LayoutParams` 来共同决定;
> 对于普通 `View`，其 `MeasureSpec` 由<font color= red> **父容器** </font>的`MeasureSpec`和自身的 `LayoutParams` 来共同决定。
> <font color= red> **`parentSize`指父容器当前剩余的空间大小。**</font>
> 

![MeasureSpec](https://i.imgur.com/vfDqiS1.png)


```java
	//ViewGroup.java
	
    protected void measureChildWithMargins(View child,
            int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin + widthUsed, 
				lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin + heightUsed, 
				lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

	public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);

        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
        // Parent has imposed an exact size on us
        case MeasureSpec.EXACTLY:
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size. So be it.
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent has imposed a maximum size on us
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // Child wants a specific size... so be it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size, but our size is not fixed.
                // Constrain child to not be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent asked to see how big we want to be
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
                // Child wants a specific size... let him have it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size... find out how big it should
                // be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size.... find out how
                // big it should be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
        }
        //noinspection ResourceType
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

```
---
#### 2. `View # onMeasure(int,int)`####
> 1.  `MeasureSpec.getSize(measureSpec)` 就是`View`测量后的大小,在`layout()`阶段确定; 
> 2. `getSuggestedMinimumWidth/Height()`设置默认大小:
>     - 当未设置背景时,由`android:minWidth/minHeight`决定;
>     - 当已设置背景时,由背景Drawable原始大小最小值与`android:minWidth/minHeight`决定; 
> 3. <font color= red>**依照上表, 当自定义`View` 直接继承自`View`重写`onMeasure()`并设置`wrap_content`时,相当于直接填充了父容器剩余所有空间`match_parent`。可以通过`onMeasure()`设置宽高默认值解决**</font>
```java
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if( widthMode==MeasureSpec.AT_MOST 
				&& heightMode==MeasureSpec.AT_MOST){
			setMeasureDimension(mWidth , mHeight);  
		}else if(widthMode==MeasureSpec.AT_MOST){
			setMeasureDimension(mWidth , heightSize);
		}else if(heightMode==MeasureSpec.AT_MOST){
			setMeasureDimension(widthSize , mHeight);
		}
    }	  
```	


```java
	/*
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent.
     *                         The requirements are encoded with
     *                         {@link android.view.View.MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                         The requirements are encoded with
     *                         {@link android.view.View.MeasureSpec}.
     *
     * @see #getMeasuredWidth()
     * @see #getMeasuredHeight()
     * @see #setMeasuredDimension(int, int)
     * @see #getSuggestedMinimumHeight()
     * @see #getSuggestedMinimumWidth()
     * @see android.view.View.MeasureSpec#getMode(int)
     * @see android.view.View.MeasureSpec#getSize(int)
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 设置宽高的测量值
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

	/**
	 * 设置默认值
     * Utility to return a default size. Uses the supplied size if the
     * MeasureSpec imposed no constraints. Will get larger if allowed
     * by the MeasureSpec.
     *
     * @param size Default size for this view
     * @param measureSpec Constraints imposed by the parent
     * @return The size this view should be.
     */
    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }
```
---
#### 3. `ViewGroup # measureChildren()` ####



```java
    /**
	 * ViewGroup.java 
	 * 循环创建子元素的测量算子和子元素的LayoutParams通过子元素的measure()进行测量;
     * Ask all of the children of this view to measure themselves, taking into
     * account both the MeasureSpec requirements for this view and its padding.
     * We skip children that are in the GONE state The heavy lifting is done in
     * getChildMeasureSpec.
     *
     * @param widthMeasureSpec The width requirements for this view
     * @param heightMeasureSpec The height requirements for this view
     */
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = mChildrenCount;
        final View[] children = mChildren;
        for (int i = 0; i < size; ++i) {
            final View child = children[i];
            if ((child.mViewFlags & VISIBILITY_MASK) != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

```
---

#### 4. 测量某个View的宽高的方式 ####

1. `Activity/View # onWindowFocusChanged(boolean)`

	```java
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            //measureLines();
        }
    }

	```
2. `View # post(Runnable)`
	```java
	view.post(new Runnable() {
	
	    @Override
	    public void run() {
	        view.getWidth(); // 获取宽度
	        view.getHeight(); // 获取高度
	    }
	});

	```
3. `ViewTreeObserver`
	```java
	view.getViewTreeObserver().addOnGlobalLayoutListener(
	        new ViewTreeObserver.OnGlobalLayoutListener() {
	
	    @Override
	    public void onGlobalLayout() {
	        if (Build.VERSION.SDK_INT >= 16) {
	            view.getViewTreeObserver()
	                    .removeOnGlobalLayoutListener(this);
	        }
	        else {
	            view.getViewTreeObserver()
	                    .removeGlobalOnLayoutListener(this);
	        }
	        view.getWidth(); // 获取宽度
	        view.getHeight(); // 获取高度
	    }
	});

	```
4. `View # measure(int,int)`

	- 设置固定数值
		```java
		int width = View.MeasureSpec.makeMeasureSpec(100,
        		View.MeasureSpec.EXACTLY);
		int height = View.MeasureSpec.makeMeasureSpec(100,
		        View.MeasureSpec.EXACTLY);
		view.measure(width, height);
		```
	- wrap_content
		```java
		int width = View.MeasureSpec.makeMeasureSpec((1<<30)-1,
        		View.MeasureSpec.AT_MOST);
		int height = View.MeasureSpec.makeMeasureSpec((1<<30)-1,
		        View.MeasureSpec.AT_MOST);
		view.measure(width, height);
		```

---
### 布局`Layout` ###



---