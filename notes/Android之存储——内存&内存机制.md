---

title: Android之 内存与内存机制
categories: "android 总结"
tags: 
	- 内存
	- 内存机制

---
# 内存与内存机制 #


### 内存机制 ###


---

### 内存占用过多的原因 ###


- 明明可以少创建对象，却创建多个；
	
		1. ArrayMap/SparseArray 替代 HashMap，避免装箱解箱；
		2. StringBuilder 替代 String拼接;
		3. DefineView # onDraw()中避免创建对象造成的内存抖动。

- 明明可以复用，却不提取而重复操作；

		1. 相同的代码重复执行 → 抽取函数；
		2. 相同xml的多次填充 → xml转View；
		3. convertView不复用 → ViewHolder。

- 明明可以释放，却不释放；

		1. IO流/cursor游标；
		2. Listeners/观察者；
		3. handler.removeCallbacksAndMessages(null)；
		4. 动画(易造成内存泄露)。

- 明明可以减少内存占用大小，却不优化；

		1. 静态属性代替枚举，减少内存占用；
		2. bitmap/drawable 处理
			1. BitmapFactory#decodeResource();
			2. BitmapFactory.Options;
			3. Bitmap.Config.RBG_565/ARGB_4444;
			4. Bitmap#recycle();

- 可以使用弱引用代替使用强引用。



---
