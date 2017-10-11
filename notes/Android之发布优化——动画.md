# 动画,让应用更唯美

	分类:
		- 帧			
		- 补间		
		- 属性		
		- SVG动画

	场景:
		- 跳转动画

---

### 帧动画 ###
流程:

1. res/drawable下将多张drawable组成animation-list;
2. ImageView 设置android:src = "frame1";
3. 获取并播放;

		iv.setImageResource(R.drawable.animation1);  
        frame = (AnimationDrawable) iv.getDrawable();  
        frame.start(); 

注意:

		帧动画容易造成OOM-->使用尺寸小的图片

参考:

		http://www.jianshu.com/p/420629118c10

---


### 补间动画 ###


---

### 属性动画 ###
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

### SVG ###


---