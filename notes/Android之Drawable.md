---

title: Android之 Drawable
categories: "android 总结"
tags: 
	- Drawable

---
# Drawable #

	分类:
		1. BitmapDrawable
		2. ShapeDrawable
		3. LayerDrawable
		4. StateListDrawable
		5. LevelListDrawable
		6. TransitionDrawable
		7. InsetDrawable
		8. ScaleDrawable
		9. ClipDrawabe
		


---
### BitmapDrawable
表示的就是一张图片，可以直接引用最原始的图片，也可以通过XML来描述，对应的是 `<bitmap>` 标签。

	<bitmap xmlns:android="http://schemas.android.com/apk/res/android"  
	    android:antialias="true"  
	    android:dither="true"  
	    android:filter="true"  
	    android:gravity="fill"  
	    android:mipMap="true"  
	    android:tileMode="clamp"  
	    android:src="@mipmap/ic_launcher"/>  

属性|描述
-|:-
`android:src`|resId
`android:antialias`|开启抗锯齿功能，让图片变平滑，一定程度降低图片清晰度，但是可以忽略不计，一般都开启。
`android:dither`|是否开启抖动效果。当图片的像素配置和手机屏幕的像素配置不一致的时候，开启选项可以让高质量的图片在低质量的屏幕上保持较好的效果，图片不会过于失真。                                       在Android中创建BitMap一般会选择ARGB_8888模式，即ARGB 4个通道各占8位，这种色彩模式下，一个像素所占的大小为4个字节，一个像素的位数总和越高，图像也就越逼真。一般都开启
`android:filter`|是否开启过滤效果，当图片尺寸被拉伸或者被压缩时，开启过滤效果可以保持较好的显示效果。
`android:mipMap`|图片相关处理技术，纹理映射，使用不多。
`android:gravity`|当图片小于容器的尺寸时，此选项对图片进行定位，不同的选项可以用分隔符组合使用。<br>= " `top / bottom / left / right` "： 	图片放在容器顶/底/左/右部，不改变图大小<br>= " `center / center_vertical / center_horizontal` "：	图片在水平和垂直 / 竖直 / 水平 方向居中，不改变大小<br>= " `fill` "：  	图片在水平和竖直方向填充容器，这就是默认值 <br>= " `fill_vertical / fill_horizontal`	 "：  	图片竖直 / 水平方向填充容器 <br>= " `clip_vertical / clip_horizontal` "：  	竖直 / 水平方向的裁剪，较少使用
`android:tileMode`|平铺模式，默认disable关闭状态<br>="`disable`": 关闭平铺<br>="`clamp`": 将图片四周的像素扩展到周围区域<br>="`repeat`": 重复平铺<br>="`mirror`": 镜像平铺

---
### ShapeDrawable
实体类对应的是`GradientDrawable`。


---
### LayerDrawable
`LayerDrawable`对应的XML标签是`<layer-list>`，表示一种把不同的`Drawable`摆放在不同层次显示一种叠加效果。一个`layer-list`可以包含多个`<item>`标签，每一个`<item>`代表显示一层`Drawable`。一个`<item>`的结构比较简单，常用的属性有`android:top/left/right/bottom `,分别表示在该位置的偏移量，单位为像素。
还可以通过`Drawable`来引用一个已经存在的`Drawable`资源或者图片。也可以自定义`Drawable`。默认情况，`layer-list`中所有的`Drawable`都会被缩放至`View`的大小，对于`bitmap`来说，通过`gravity`来控制图片的显示效果。

---
### StateListDrawable
`StateListDrawable`对应的是`<selector>`标签。**默认的item一般放在最后并且不添加任何状态**，这样当系统在之前的item无法选择的时候，就会匹配默认的item，因为item的默认状态不附带任何状态，所以它可以适配任何状态。

```
	<selector xmlns:android="http://schemas.android.com/apk/res/android" 
	    android:constantSize="true" 
	    android:dither="true" 
	    android:variablePadding="true">
	    <item android:drawable="@drawable/bitmapdrawable"
	        android:state_pressed="true"
	        android:state_focused="true"
	        android:state_hovered="true"
	        android:state_selected="true"
	        android:state_checkable="true"
	        android:state_checked="true"
	        android:state_enabled="true"
	        android:state_activated="true"
	        android:state_window_focused="true"/>
	</selector>

```
属性|define
-|:-
`android:constantSize`|  `StateListDrawable`的固有大小是否不随着其状态的改变而改变。<br>不同的`drawable`存在不同的大小，true表示固有大小不变，false则会随着状态的改变而改变。
`android:dither`| 是否开启抖动效果，为了获取最好的显示效果。默认值为true。
`android:variablePadding`| `StateListDrawable`的`padding`表示是否随着其状态的改变而改变，true表示会随着状态改变而改变，false表示内部所有`Drawable`的`padding`的最大值。默认值false，不建议修改此选项。

---
### LevelListDrawable
用于多种情况设置不同图片，对应于xml文件中的`<level-list>`标签。通过`minLevel`和`maxLevel`来设置等级，0~10000之间 

```
	<level-list xmlns:Android="http://schemas.android.com/apk/res/android">
	    <item android:maxLevel="0" android:drawable="@drawable/battery_0" />
	    <item android:maxLevel="1" android:drawable="@drawable/battery_1" />
	    <item android:maxLevel="2" android:drawable="@drawable/battery_2" />
	    <item android:maxLevel="3" android:drawable="@drawable/battery_3" />
	    <item android:maxLevel="4" android:drawable="@drawable/battery_4" />
	</level-list>
```

使用方法:

	imageView.getDrawable().setImageLevel(1);

---
### TransitionDrawable
![Image](http://img.blog.csdn.net/20170315144111644?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzI3Nzc0MA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
TransitionDrawable对应标签，transition是过渡，转变的意思， 对应于andnorid中的淡入淡出效果 

```

	<!-- 定义 -->
	<?xml version="1.0" encoding="utf-8"?>
	<transition xmlns:android="http://schemas.android.com/apk/res/android" >
	    <item android:drawable="@drawable/shape_drawable_gradient_linear"/>
	    <item android:drawable="@drawable/shape_drawable_gradient_radius"/>
	</transition>
	
	<?xml version="1.0" encoding="utf-8"?>
	<shape xmlns:android="http://schemas.android.com/apk/res/android"
	    android:shape="rectangle" >
	
	    <gradient
	        android:centerColor="#00ff00"
	        android:endColor="#0000ff"
	        android:startColor="#ff0000"
	        android:centerX="0.5"
	        android:centerY="0.5"
	        android:angle="0"
	        android:type="linear" />
	
	</shape>
	
	<?xml version="1.0" encoding="utf-8"?>
	<shape xmlns:android="http://schemas.android.com/apk/res/android"
	    android:shape="rectangle" >
	
	    <gradient
	        android:centerColor="#00ff00"
	        android:endColor="#0000ff"
	        android:startColor="#ff0000"
	        android:gradientRadius="60"
	        android:type="radial" />
	
	</shape>

	<!-- 使用 -->
	<TextView
	    android:id="@+id/test_transition"
	    android:layout_width="100dp"
	    android:layout_height="100dp"
	    android:background="@drawable/transition_drawable"
	    android:gravity="center"
	    android:text="" />

	View v = findViewById(R.id.test_transition);
	TransitionDrawable drawable = (TransitionDrawable) v.getBackground();
	drawable.startTransition(1000);

```

	拓展： 
	Android Transition Framwork 主要用来做三件事： 
	1. Activity间的转场动画； 
	2. 不同Activity或Fragment间元素共享，让交互更连贯； 
	3. 同一个Activity之间一些View的变换动画。 
	参见：http://www.jianshu.com/p/0af52be90ae6


---
### InsetDrawable
`insetDrawable`对应标签`<inset>`，它可将其他Drawable内嵌到自己当中，适用于当一个view希望自己的背景比自己小的情况.4个属性分别代表上下左右内凹的间距.


```

	<inset xmlns:android="http://schemas.android.com/apk/res/android"
	    android:insetBottom="15dp"
	    android:insetLeft="15dp"
	    android:insetRight="15dp"
	    android:insetTop="15dp" >
	
	    <shape android:shape="rectangle" >
	        <solid android:color="#ff0000" />
	    </shape>
	</inset>


```

---
### ScaleDrawable 
ScaleDrawable对应于xml文件中的`<scale>`标签，可以根据自己的level将指定的drawable缩放到一定比例。

```
	<scale xmlns:android="http://schemas.android.com/apk/res/android"
	    android:drawable="@color/blue"
	    android:level="1"
	    android:scaleGravity="center"
	    android:scaleHeight="20%"
	    android:scaleWidth="20%" />
```
属性|define
-|:-
`android:scaleGravity`|相当于gravity属性，缩放之后的效果。
`android:scaleHeight/scaleWidth`|表示Drawable的缩放比例，20%就是缩放为原来的80%
` android:level`|**0 < level <= 10000,级别越大,drawable 显示越大,10000时没有缩放效果**

---
### ClipDrawabe
`ClipDrawabe`对应于`<clip>`标签，他可以根据自己当前的等级(`level`)来裁剪一个`Drawable`，裁剪方向可以通过`android:clipOrientation`和`android:gravity`两个属性共同控制。

```
	<clip xmlns:android="http://schemas.android.com/apk/res/android" 
	android:clipOrientation="vertical\horizontal" 
	android:drawable="@drawable/bitmapdrawable" 
	android:gravity="bottom|top|left|right|center|fill|center_vertical|center_horizontal|fill_vertical|fill_horizontal|clip_vertical|clip_horizontal" />

```
- top：将内部的Drawable放在容器的顶部，不改变大小，如果为竖直裁剪，就从底部开始裁剪。
- bottom：将内部的Drawable放在容器的底部，不改变大小，如果为竖直裁剪，就从顶部开始裁剪。
- left：默认值。内部Drawable放在容器左边，不改变大小，如果为水平裁剪，就从右边开始裁剪。
- right：内部Drawable放在容器右边，不改变大小，如果为水平裁剪，就从左边开始裁剪。
- center_vertical：Drawable在容器中竖直居中，不改变大小，竖直裁剪的时候上下同时开始裁剪。
- center_horizontal：Drawable水平居中，不改变大小，水平裁剪的时候从左右两边开始裁剪。
- center：Drawable在水平和竖直方向居中，不改变大小，水平裁剪的时候从左右开始裁剪，竖直裁剪的时候从上下开始裁剪。
- fill：Drawable在竖直和水平方向填充容器，仅当level=0的时候才有裁剪行为。
- clip_vertical：附加选项，竖直方向的裁剪，少使用。
- clip_horizontal：附加选项，水平方向的裁剪，少使用。
- fill_vertical：Drawable在竖直方向填充，如果为竖直裁剪，仅当ClipDrawable的等级为0时（level=0，完全不可见），才会有裁剪行为。
- fill_horizontal：Drawable在水平方向填充，如果为水平裁剪，仅当ClipDrawable等级为0时，才能有裁剪行为。

---