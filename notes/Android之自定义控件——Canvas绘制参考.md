---

title: Canvas 绘制
categories: "android 总结"
tags: 
     - android
     - Canvas 
     - 绘制
 
---
# Canvas 绘制 #

	目录:
		1. 图片文字绘制
		2. 零散内容绘制和画布调整


---
## Canvas图片文字绘制

    概要:
        1. 绘制图片
        2. 绘制文字

### 1. 绘制图片

#### -   矢量(drawPicture)
警告:需要关闭硬件加速.

- 矢量图绘制就是记录Canvas绘制的内容. 
- Picture的相关函数

        - getWidth\height():得到宽高
        - beginRecording(int width,int height):开始录制,return Canvas;
        - endRecording():结束录制;
        - draw(Canvas canvas):绘制矢量图;
    
- 矢量图的绘制有以下三种方式:
    1. mPic.draw(canvas);

        - 低版本**影响canvas的状态**,一般不使用;

    2. canvas.drawPicture(mPic);
        - **不影响canvas状态,可以设置显示矢量图的区域矩形大小,完全压缩显示.**
                
                public void drawPicture (Picture picture)
                
                public void drawPicture (Picture picture, Rect dst)
                
                public void drawPicture (Picture picture, RectF dst)                
                
    3. picture → pictureDrawable → pictureDrawable.draw(canvas),**不会缩放,按显示区域大小显示一部分**;

            // 包装成为Drawable
            PictureDrawable drawable = new PictureDrawable(mPicture);
            // 设置绘制区域 -- 注意此处所绘制的实际内容不会缩放
            drawable.setBounds(0,0,250,mPicture.getHeight());
            // 绘制
            drawable.draw(canvas);
            

#### -   位图(drawBitmap)
可能会消耗大量的内存,或者会造成内存泄露;
首先获得bitmap,然后进行绘制.

1. 获取
    1. 资源文件(drawable/mipmap/raw):
            
            Bitmap bitmap = BitmapFactory.decodeResource
            (mContext.getResources(),R.raw.bitmap);
    2. 资源文件(assets):
    
            Bitmap bitmap=null;
            try {
                InputStream is = mContext.getAssets()
                .open("bitmap.png");
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    3. 内存卡文件:
    
            Bitmap bitmap = BitmapFactory
                .decodeFile("/sdcard/.../bitmap.png");
    4. 网络文件:
    
            // 此处省略了获取网络输入流的代码
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

2. 绘制
    
        public void drawBitmap 
        1. (Bitmap bitmap, Matrix matrix, Paint paint)
        2. (Bitmap bitmap, float left, float top, Paint paint)
        3. (Bitmap bitmap, Rect src, Rect dst, Paint paint)
        4. (Bitmap bitmap, Rect src, RectF dst, Paint paint)
	
	    1. 默认绘制起点为原点.
	        `canvas.drawBitmap(bitmap,new Matrix(),new Paint());`
	    2. 确定一个绘制起点坐标.
	    3. 4.指定图片的绘制矩形区域和图片在屏幕上的绘制矩形区域,当前者宽/高度>后者宽/高度的情况下会压缩.

        canvas.translate(mWidth/2,mHeight/2);
        // 指定图片绘制区域(左上角的四分之一)
        Rect src = new Rect(0,0,bitmap.getWidth()/2,bitmap.getHeight()/2);
        // 指定图片在屏幕上显示的区域
        Rect dst = new Rect(0,0,200,400);
        // 绘制图片
        canvas.drawBitmap(bitmap,src,dst,null);

    	4. 注:可以控制两个区域的大小来完成某个动画.

### 2. 绘制文字
    
    画笔Paint文本相关函数:
    色彩	setColor,setARGB,setAlpha  设置颜色，透明度
    大小	setTextSize	               设置文本字体大小
    字体	setTypeface	               设置或清除字体样式
    样式	setStyle	               填充描边等
    对齐	setTextAlign	           左,居中,右
    测量	measureText	               测量文本大小
    
    (注意:measureText()在设置完文本各项参数后调用)
   
1. 指定文本基线位置(x,y)


        public void drawText 
        (String text, float x, float y, Paint paint)
        (String text, int start, int end, float x, float y, Paint paint)
        (CharSequence text, int start, int end, float x, float y, Paint paint)
        (char[] text, int index, int count, float x, float y, Paint paint)


    * 截取的字符串的index∈[start,end),index从0开始,
    * count指截取的char字符数;
    
2. 指定每个文字的位置(不建议使用)

        public void drawPosText 
        (String text, float[] pos, Paint paint)
        (char[] text, int index, int count, float[] pos, Paint paint)
        
    * 例:
        
            canvas.drawPosText(str,new float[]{
                  100,100,    // 第一个字符位置
                  200,200,    // 第二个字符位置
                  300,300,    // ...
                  400,400,
                  500,500},
            textPaint);
    
    * 缺点:
        1. 必须指定所有字符位置，否则直接crash掉，反人类设计
        2. 性能不佳，在大量使用的时候可能导致卡顿
        3. 不支持emoji等特殊字符，不支持字形组合与分解        
       
       
3. 指定路径来绘制文字

        public void drawTextOnPath 
        (String text, Path path, float hOffset, float vOffset, Paint paint)
        (char[] text, int index, int count, Path path, float hOffset, float vOffset, Paint paint)

---
## Canvas内容绘制

	- 绘制颜色
		- 主要函数:drawColor, drawRGB, drawARGB
		- 使用单一颜色填充整个画布
	- 绘制基本形状
		- 主要函数:drawPoint, drawPoints, drawLine, drawLines, drawRect, drawRoundRect, drawOval, drawCircle, drawArc	
		- 依次为 点、线、矩形、圆角矩形、椭圆、圆、圆弧
	- 绘制图片
		- 主要函数:drawBitmap, drawPicture
		- 绘制位图和图片
	- 绘制文本	
		- drawText, drawPosText, drawTextOnPath	
		- 依次为 绘制文字、绘制文字时指定每个文字位置、根据路径绘制文字
	- 绘制路径	
		- drawPath	
		- 绘制路径，绘制贝塞尔曲线时也需要用到该函数
	- 顶点操作	
		- drawVertices, drawBitmapMesh	
		- 通过对顶点操作可以使图像形变，drawVertices直接对画布作用、 drawBitmapMesh只对绘制的Bitmap作用
	- 画布剪裁	
		- clipPath, clipRect	
		- 设置画布的显示区域
	- 画布快照	
		- save, restore, saveLayerXxx, restoreToCount, getSaveCount	
		- 依次为 保存当前状态、回滚到上一次保存的状态、 保存图层状态、 回滚到指定状态、 获取保存次数
	- 画布变换	
		- translate, scale, rotate, skew	
		- 依次为 位移、缩放、 旋转、错切
	- Matrix(矩阵)	
		- getMatrix, setMatrix, concat	
		- 实际上画布的位移，缩放等操作的都是图像矩阵Matrix， 只不过Matrix比较难以理解和使用，故封装了一些常用的方法。


### 绘制画布颜色

	canvas.drawColor(int color);

#### 绘制一个或者多个坐标点

	canvas.drawPoint(200, 200, mPaint);    
	 //在坐标(200,200)位置绘制一个点
	canvas.drawPoints(new float[]{          
	//绘制一组点，坐标位置由float数组指定
	      500,500,
	      500,600,
	      500,700
	},mPaint);

### 绘制形状
	
#### 1. 线 ---- 每两个点确定一条线段

	canvas.drawLine(300,300,500,600,mPaint);    
	// 在坐标(300,300)(500,600)之间绘制一条直线
	canvas.drawLines(new float[]{               
	// 绘制一组线 每四数字(两个点的坐标)确定一条线
	    100,200,200,200,
	    100,300,200,300
	},mPaint);
#### 2. 矩形 ---- 两个对角线确定一个矩形

	// 第一种
	canvas.drawRect(100,100,800,400,mPaint);
	
	// 第二种
	Rect rect = new Rect(100,100,800,400);
	canvas.drawRect(rect,mPaint);
	
	// 第三种
	RectF rectF = new RectF(100,100,800,400);
	canvas.drawRect(rectF,mPaint);

	Rect和RectF的区别在于int 和 float精度,方法也不相同;

#### 3. 圆角矩形 ---- 矩形 + 内切椭圆的两个半径(半径大于矩形一半的宽度按一半来算)

	// 第一种
	RectF rectF = new RectF(100,100,800,400);
	canvas.drawRoundRect(rectF,30,30,mPaint);
	
	// 第二种
	canvas.drawRoundRect(100,100,800,400,30,30,mPaint);


#### 4. 椭圆 ---- 矩形的内切椭圆,只需给定矩形

	// 第一种
	RectF rectF = new RectF(100,100,800,400);
	canvas.drawOval(rectF,mPaint);
	
	// 第二种
	canvas.drawOval(100,100,800,400,mPaint);


#### 5. 圆 ---- 圆心 + 半径

	canvas.drawCircle(500,500,400,mPaint);  
	// 绘制一个圆心坐标在(500,500)，半径为400 的圆。

#### 6. 圆弧 ---- 矩形内切椭圆 + 开始角度 + 结束角度 + 是否使用中心
	// 第一种
	public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter, @NonNull Paint paint)
	    
	// 第二种
	public void drawArc(float left, float top, float right, float bottom, float startAngle,
	float sweepAngle, boolean useCenter, @NonNull Paint paint)

### Paint

1. 类型setStyle(int style)
	1. Paint.Style.STROKE:描边 
	2. Paint.Style.FILL:填充
	3. Paint.Style.FILL_AND_STROKE:描边+填充
2. 描边宽度

		paint.setStrokeWidth(40);
3. 抗锯齿

		setAntiAlias(true);


### Canvas


1. 快照和回滚
    1. 基础

		快照 : save();			//保存画布状态
		回滚 : restore();		//取出最后一次入栈的图层

		save就是图层入栈,restore就是图层出栈;	
	
	2. 对应的API:
		
    		save()				把当前的画布的状态进行保存，
    		或save(saveFlags)	然后放入特定的栈中

        		ALL_SAVE_FLAG				默认，保存全部状态
        		CLIP_SAVE_FLAG				保存剪辑区
        		CLIP_TO_LAYER_SAVE_FLAG		剪裁区作为图层保存
        		FULL_COLOR_LAYER_SAVE_FLAG	保存图层的全部色彩通道
        		HAS_ALPHA_LAYER_SAVE_FLAG	保存图层的alpha(不透明度)通道
        		MATRIX_SAVE_FLAG			保存Matrix信息( translate, rotate, scale, skew)
    
    		saveLayerXxx		新建一个图层，并放入特定的栈中
    
				saveLayerXxx,导致图层叠加造成计算量增倍而过度渲染.

                // 无图层alpha(不透明度)通道
                public int saveLayer 
                (RectF bounds, Paint paint)
                (RectF bounds, Paint paint, int saveFlags)
                (float left, float top, float right, float bottom, Paint paint)
                (float left, float top, float right, float bottom, Paint paint, int saveFlags)
                // 有图层alpha(不透明度)通道
                public int saveLayerAlpha (....,int alpha)

    		restore				把栈中最顶层的画布状态取出来，
    							并按照这个状态恢复当前的画布
    
    		restoreToCount		弹出指定位置及其以上所有的状态，
    							并按照指定位置的状态进行恢复
    
    		getSaveCount		获取栈中内容的数量(即保存次数)

2. 位移(不断叠加),基于上一次位置的移动.

		canvas.translate(200,200);
	
3. 旋转(不断叠加),根据**某个中心位置或者原点**旋转**某个角度**;
		
		public void rotate 
		(float degrees)
        (float degrees, float px, float py)

4. 缩放(不断叠加),**根据原点位置对x,y方向上的缩放比例,或者根据某个缩放中心的位置控制缩放比例;当缩放比例sx or sy < 0时,会进行翻转相当于中心对称翻转;**

		public void scale (float sx, float sy)

		public final void scale (float sx, float sy, float px, float py)


5. 错切(不断叠加) - skew,**在某个方向上倾斜对应的角度,填入的值为角度对应的tan值.**

		public void skew (float sx, float sy)
		
		变换之后的值:
			X = x + sx * y
			Y = sy * x + y


