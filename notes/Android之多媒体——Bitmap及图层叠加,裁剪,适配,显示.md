# Bitmap


		1. BitmapFactory.Options类解析
			1.1. inMutable
			1.2. inJustDecodeBounds、outWidth、outHeight
			1.3. outMimeType
			1.4. inPurgeable、inInputShareable
			1.5. inPreferredConfig
			1.6. inBitmap
			1.7. inScaled、inDensity、inTargetDensity、inScreenDensity
			1.8. inSampleSize

		2. Bitmap内存占用

		3. 缩放因子和Bitmap复用限制的由来
			3.1. Android4.3 API18
			3.2. Android4.4 API19


---


### 1. Options解析

- `inMutable`

		= true,bitmap可编辑,可以作为Canvas的底层Bitmap使用。
		= false,bitmap不可变。

- `inJustDecodeBounds`

		= true,不加载到内存,Native层解码了图片,未生成Java层的Bitmap;
		= false,加载到内存;


- `outWidth`、`outHeight`

		inJustDecodeBounds = true 时 Options 得到图片的原始宽高,未经缩放;
		inJustDecodeBounds = false 时 加载到内存经缩放的宽高;
	
- `outMimeType`

		String,加载图片类型,格式为"image/*",如:"image/png","image/jpeg";

- `inPurgeable`

		API19及以下，= true，BitmapFactory创建的用于存储Bitmap Pixel的内存空间，可以在系统内存不足时被回收。
		APP需要再次访问Bitmap的Pixel时（例如：绘制Bitmap或是调用getPixel)，系统会再次调用BitmapFactory decode方法重新生成Bitmap的Pixel数组。


- `inInputShareable`

		与 inPurgeable = true 结合使用
		= true ,	浅拷贝;
		= false,	深拷贝;

- `inPreferredConfig`



参数取值	| 存储 | 详解 
-|-|-
ALPHA_8	|每个像素一个字节（8位），只存储8位的透明度值|不包含颜色信息
RGB_565	|每个像素两个字节（16位），颜色通道比 红:绿:蓝 = 5 : 6 : 5| 65536 = 2^5 × 2^6 × 2^5,可用相近颜色代替
ARGB_4444	|每个像素两个字节（16位），Alpha，R，G，B四个通道每个通道用4位表示 | 已弃用
ARGB_8888	|每个像素四个字节（32位），Alpha，R，G，B四个通道每个通道用8位表示 | 完全表示32位真彩色,占用内存过大,是RGB_565模式的两倍，是ALPHA_8模式的4倍

	inPreferredConfig ≠ null，解码器会尝试使用此参数指定的颜色模式来对图片进行解码
	inPreferredConfig = null或者在解码时无法满足此参数指定的颜色模式，
			解码器会自动根据原始图片的特征以及当前设备的屏幕位深，选取合适的颜色模式来解码

<font color = "red">指定RGB_565 和不设置inPreferredConfig的效果是一样的。</font>

	参考自:http://blog.csdn.net/ccpat/article/details/46834089

- `inBitmap`

	Bitmap,API11添加用于重用已有的Bitmap
		
		- API 11 ~ API 19

			- 被复用的Bitmap的宽高必须等于被加载的Bitmap的原始宽高。（注意这里是指原始宽高，即没进行缩放之前的宽高）
			- 被加载Bitmap的Options.inSampleSize = 1。
			- 被加载Bitmap的Options.inPreferredConfig字段失效，会被被复用的Bitmap的inPreferredConfig值所覆盖（不然，所占内存可能就不一样了）

		- API 19 以上

			- 被复用的Bitmap必须是Mutable。违反此限制会返回新申请内存的Bitmap。
			- 被复用的Bitmap的内存大小（通过Bitmap.getAllocationByteCount方法获得，API19及以上才有）必须大于等于被加载的Bitmap的内存大小。违反此限制，将会导致复用失败，抛出异常IllegalArgumentException(Problem decoding into existing bitmap）

- `inScaled`、`inDensity`、`inTargetDensity`、

		表示是否进行缩放,默认 = true;
		= true , Bitmap#mDensity = inTargetDensity,缩放因子 = inTargetDensity/inDensity;

			从res加载图片时,
				inDensity = 图片所在文件夹的密度,
				inTargetDensity = 系统密度;

			从文件加载图片时,默认不缩放
				inDensity = 0;一般根据需求设置;
				inTargetDensity = 0;一般设为系统密度;

		= false, Bitmap#mDensity = inDensity或者系统默认密度160;


		

- `inScreenDensity`


- `inSampleSize`
图片宽高和像素的缩放比;

		当 inSampleSize = 2 时,
			
			宽和高设为本来的1/2,像素总数为原来的1/4。

---
	
### 2. Bitmap内存占用

	内存		= 每个像素所占字节 × 像素数 
	= 每个像素所占字节 × 原始宽度 × 缩放因子 × 原始高度 × 缩放因子
	= 每个像素所占字节 × originWidth  × originHeight
	  × (inTargetDensity/inDensity)^2


 	参考自:http://ltlovezh.com/2016/05/31/Android-Bitmap%E9%82%A3%E4%BA%9B%E4%BA%8B/
---