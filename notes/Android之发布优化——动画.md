# 动画,让应用更唯美

	分类:
		- 帧			
		- 补间		
		- 属性		
		- SVG动画

	场景
	:
		- 跳转动画

	扩展:
		- 5.0 圆形动画
		- Rotate3dAnimation
		- Airbnb Lottie
---

##1.  原生动画分类 ##
###1.1 - 帧动画 ###
流程:

1. res/drawable下将多张drawable组成animation-list;
2. ImageView 设置android:src = "frame1";
3. 获取并播放;

```java
iv.setImageResource(R.drawable.animation1);  
frame = (AnimationDrawable) iv.getDrawable();  
frame.start(); 
```

注意: 帧动画容易造成OOM-->使用尺寸小的图片

参考:

		http://www.jianshu.com/p/420629118c10

---


### 1.2 - 补间动画 ###


---

### 1.3 - 属性动画 ###
* 作用于任意对象,一个时间间隔内完成对象从一个属性值到另一个属性值的变化,要求属性值存在set和get函数;
* 默认时间间隔300ms,默认帧数10ms/帧;

常见属性动画完成方式:
	
		ObjectAnimtor.of***(object,
			对应propertyName,...(对应类型的属性值)).start();
		//***代表Int或者Float
属性动画通用的set函数
		
		setDuration(,..)
		setEvaluator(...)
		setRepeatCount(...)
		setRepeatMode(...)

属性动画集AnimatorSet
	
	1. 
		AnimatorSet set = new AnimatorSet();
		set.playTogether(.......);//作用于某对象的属性动画内容
		......//设置set的set属性
		set.start();

	2. 
		AnimatorSet set = (AnimatorSet)AnimtorInflater.loadAnimator(context,setId);
		set.setTarget(view);
		set.start();

属性动画的xml定义


	ValueAnimator	------------------	animator
	属性:
		android:duration(时长)
		android:valueFrom(起始值)
		android:valueTo(结束值)
		android:startOffset(动画延迟时间)
		android:repeatCount(重复次数)
		android:repeatMode(重复模式-repeat|reverse逆向)
		android:valueType(属性类型-intType|floatType)
	ObjectAnimator	------------------	ObjectAnimator
	属性:
		android:propertyName(作用的属性)
		android:duration
		android:valueFrom
		android:valueTo
		android:startOffset
		android:repeatCount
		android:repeatMode
		android:valueType
	AnimatorSet		------------------	set
	属性:
		android:ordering =
			together(默认,同时),sequentially(按序)
	
	

监听:

	AnimatorListener:开始,结束,取消,重复的状态监听;
	AnimatorUpdateListener:属性值变化的监听;
	
	
	
实现普通属性变化的方法

	1. 使用一个类来包装原始对象,提供set,get函数;
	2. ValueAnimtor设置AnimatorUpdateListener动态设置属性;

	注:ValueAnimtor获得当前动画进度和动画完成百分比的函数分别
		为getAnimatedValue(),getAnimatedFraction()

估值器的使用

		1. 创建类实现估值器TypeEvaluator<T>,
			实现方法evaluate(百分比,初始值,结束值),
			返回固定百分比的T类型的对应值;
		2. 创建对象调用方法赋值;
---

### 1.4 - SVG ###


---
## 3. 扩展与更新 ##

### 3.1 - 5.0 圆形动画 ###

	Animator circular = ViewAnimationUtils
		.createCircularReveal(view //target
        , centerX	//target中心点X
        , centerY	//target中心点Y
		, startRadius	//开始半径
        , endRadius);	//结束半径

注意:
	
	1. 圆形动画与遮罩层配合使用;
	2. 主题色对圆形动画无作用;

---

### 3.2 - `Rotate3dAnimation` ###

参数与函数:
	`Rotate3dAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, reverse);`
		
	fromDegrees 与 toDegrees 
		视图旋转的开始角度和结束角度，当toDegree处于90奇数倍时，视图将变得不可见。
	
	centerX与centerY 
		视图旋转的中心点。
		
	depthZ 
		Z轴移动基数，用于计算Camera在Z轴移动距离,为0时,大小将不发生变化.
	
		
	reverse 
		boolean类型，控制Z轴移动方向，达到视觉远近移动导致的视图放大缩小效果。
		
	applyTransformation() 
		根据动画播放的时间 interpolatedTime （动画start到end的过程，interpolatedTime从0.0变化到1.0），让Camera在Z轴方向上进行相应距离的移动，实现视觉上远近移动的效果。然后调用 rotateX()方法，让视图围绕Y轴进行旋转，产生3D立体旋转效果。最后再通过Matrix来确定旋转的中心点的位置。

使用:

		Rotate3dAnimation rotate = new Rotate3dAnimation(270, 360, centerX, centerY, depthZ, false);
        rotate.setDuration(duration);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());

        viewGroup.startAnimation(rotate);

注意:

		1. 当旋转后为功能的实现,一部分控件的做好显示,隐藏,事件禁止和事件禁止释放.
		2. viewGroup.startAnimation(rotate)

---
### 3.3 - `Airbnb Lottie & Adobe AE`

参考:
- [ 「**Lottie--让动画如此简单**」 ](http://mp.weixin.qq.com/s/LrkZtDZY3SE8IUQ-x1hsmQ)
- [ 「**AE制作json文件格式动画以及lottie开源库的使用**」 ](http://blog.csdn.net/jhl122/article/details/56665374)
- [ 「**Android•Lottie 动画库填坑记**」 ](https://mp.weixin.qq.com/s/ipu32zPjaHeqICgOGqct9g)
---