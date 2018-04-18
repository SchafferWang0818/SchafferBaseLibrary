---

title: Android之 Bitmap
categories: "android 总结"
tags: 
	- Bitmap
---
# Bitmap
### Options解析

属性 | 含义
-|-
`inMutable`|= `true`,bitmap可编辑,可以作为Canvas的底层Bitmap使用。<br>= `false`,bitmap不可变。
`inJustDecodeBounds`|= `true`,不加载到内存,`Native` 层解码了图片,未生成Java层的Bitmap;<br>= `false`,加载到内存;
`outWidth`、`outHeight`|`inJustDecodeBounds = true` 时 `Options` 得到图片的原始宽高,未经缩放;<br>`inJustDecodeBounds = false` 时 加载到内存经缩放的宽高;
`outMimeType(String)`|~~**从API21开始，属性被废弃**~~,加载图片类型,格式为"image/*",如:"image/png","image/jpeg";
`inPurgeable`|API19及以下，= `true`，`BitmapFactory` 创建的用于存储`Bitmap Pixel`的内存空间，可以在系统内存不足时被回收。<br>APP需要再次访问`Bitmap`的`Pixel`时（例如：绘制 `Bitmap` 或是调用`getPixel()`)，系统会再次调用`BitmapFactory#decode...()`重新生成`Bitmap`的`Pixel`数组。
`inInputShareable`|与 `inPurgeable = true` 结合使用<br>= `true` ,	浅拷贝;<br>= `false`,	深拷贝;<br>
`inSampleSize`|图片宽高和像素的取样值做为缩略;<br>当 `inSampleSize = 2` 时,宽 / 高方向上2个单位仅取1个单位，2×2 取 1×1。


`inPreferredConfig`	| 存储 | 详解 
-|:-|:-
`ALPHA_8`	|每个像素一个字节（8位），只存储8位的透明度值|不包含颜色信息
`RGB_565(默认)`	|**每个像素两个字节（16位）**，颜色通道比 `R:G:B = 5 : 6 : 5`| `65536 = 2^5 × 2^6 × 2^5`,可用相近颜色代替
~~`ARGB_4444`~~	|~~每个像素两个字节（16位），`Alpha，R，G，B`四个通道每个通道用4位表示~~ | **已弃用**
`ARGB_8888`	|每个像素四个字节（32位），`Alpha，R，G，B`四个通道每个通道用8位表示 | 完全表示32位真彩色,占用内存过大,是`RGB_565`模式的2倍，是`ALPHA_8`模式的4倍

	inPreferredConfig ≠ null，解码器会尝试使用此参数指定的颜色模式来对图片进行解码
	inPreferredConfig = null或者在解码时无法满足此参数指定的颜色模式，
			解码器会自动根据原始图片的特征以及当前设备的屏幕位深，选取合适的颜色模式来解码

- `inBitmap`

```
	Bitmap,API11添加用于重用已有的Bitmap
		
	- API 11 ~ API 19

		- 被复用的Bitmap的宽高必须等于被加载的Bitmap的原始宽高。（注意这里是指原始宽高，即没进行缩放之前的宽高）
		- 被加载Bitmap的Options.inSampleSize = 1。
		- 被加载Bitmap的Options.inPreferredConfig字段失效，会被被复用的Bitmap的inPreferredConfig值所覆盖（不然，所占内存可能就不一样了）

	- API 19 以上

		- 被复用的Bitmap必须是Mutable。违反此限制会返回新申请内存的Bitmap。
		- 被复用的Bitmap的内存大小（通过Bitmap.getAllocationByteCount方法获得，API19及以上才有）必须大于等于被加载的Bitmap的内存大小。违反此限制，将会导致复用失败，抛出异常IllegalArgumentException(Problem decoding into existing bitmap）
```

- `inScaled`、`inDensity`、`inTargetDensity`、`inScreenDensity`

```
	inScaled 表示是否进行缩放,默认 = true;
	= true , bitmap.mDensity = inTargetDensity,缩放因子 = inTargetDensity/inDensity;

		从res加载图片时,
			inDensity = 图片所在文件夹的密度,
			inTargetDensity = 系统密度;

		从文件加载图片时,默认不缩放
			inDensity = inTargetDensity = 0;
			inTargetDensity 一般设为系统密度;

	= false, bitmap.mDensity = inDensity或者系统默认密度160;

	inDensity = 
		drawable-ldpi：120(dpi)
		drawable-mdpi：160(dpi)
		drawable-hdpi：240(dpi)
		drawable-xhdpi：320(dpi)
		drawable-xxhdpi：480(dpi)
		drawable-xxxhdpi：640(dpi)
		drawable-nodpi：不压缩

```
#### 附录: `drawable` 与 `mipmap`

##### drawable
drawable适配过程如下:
- 在对应dpi下找到<font color=red>不缩放使用</font>;
- 找不到向更高dpi文件夹下查找,找到后<font color=red>缩放使用</font>;
- 在`-nodpi`下找到<font color=red>不缩放使用</font>;
- 找不到向更低dpi文件夹下查找,找到后<font color=red>放大使用</font>;

![适配过程](https://i.imgur.com/V4CjZH6.png)

##### mipmap
用于图标的存放,建议尺寸如下:

密度			|	建议尺寸
		-	|	-
`mdpi`		|	<font size=2>48*48</font>
`hdpi`		|	<font size=2>72*72</font>
`xhdpi`		|	<font size=2>96*96</font>
`xxhdpi`	|	<font size=2>144*144</font>
`xxxhdpi`	|	<font size=2>192*192</font>

---
	
### Bitmap内存占用

```
	Bitmap占用内存		
	= 每个像素所占字节 × 像素数 
	[从res/raw加载Bitmap, option.inScaled = true]
	= 每个像素所占字节 × (原始宽度 × 缩放因子) × (原始高度 × 缩放因子)
	= 每个像素所占字节 × originWidth  × originHeight × (inTargetDensity / inDensity)^2
	[从res/raw加载Bitmap, option.inScaled = true, 采样缩放]
	= 每个像素所占字节 × (originWidth / inSampleSize) × (inTargetDensity / inDensity)
					 × (originHeight / inSampleSize) × (inTargetDensity / inDensity)
					
	[从File加载Bitmap,默认不缩放]
	= 每个像素所占字节 × (originWidth / inSampleSize)
					 × (originHeight / inSampleSize)

	// 复用Bitmap来解码图片时, 表示新解码图片占用内存的大小（并非实际内存大小）
	= getByteCount() = getRowBytes() * getHeight()
	// 复用Bitmap来解码图片时, 表示被复用Bitmap真实占用的内存大小
	≈ getAllocationByteCount() = mBuffer.length //像素数据的字节数组的长度
```

---
### [Bitmap的压缩(点击查看参考原文)](https://juejin.im/post/58bc1f11ac502e006b0957b7#heading-7)
压缩方式有压缩、裁剪、采样三种方式。
第一种方案适用于进行纯粹的文件压缩，而不适用进行图像处理压缩；
第二种方案压缩方案适用于进行图像编辑时的压缩，就像手机自带相册的编辑功能，可以随着裁剪区域的大小进行最终的压缩；
第三种方案相对来说，适应性较强，各种场景都会符合。
#### 压缩`(Compress)` ####
<font color=red>**将`Bitmap`按百分率压缩写入到指定格式(`jpg/png/...`)文件中,以失真的代价压缩,并不改变压缩后文件读入内存中的大小.**</font>

```java
	private Bitmap getCompressedBitmap(Bitmap bitmap) {
        try {
            //创建一个用于存储压缩后Bitmap的文件
            File compressedFile = FileHelper.createFileByType(mContext, destType, "compressed");
            Uri uri = Uri.fromFile(compressedFile);
            OutputStream os = getContentResolver().openOutputStream(uri);
            Bitmap.CompressFormat format = destType == FileHelper.JPEG ?
                    Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;
		// >>>核心部分
            boolean success = bitmap.compress(format, 0, os);
		// <<<核心部分
            if (success) {
                T.showLToast(mContext, "success");
            }
            final String pathName = FileHelper.stripFileProtocol(uri.toString());
            showBitmapInfos(pathName);
            bitmap = BitmapFactory.decodeFile(pathName);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

```
#### [裁剪`(Crop)`](https://github.com/REBOOTERS/AndroidAnimationExercise/blob/master/app/src/main/java/home/smart/fly/animations/activity/CameraActivity.java) ####
裁剪时将整张缩放裁剪后,内存缩小比例 = 整体缩放比例 = 宽度缩放比例 * 高度缩放比例,存入文件也随之变化;

<font color=red>当输出宽高大于原本宽高时,将适得其反。</font>

```java
	private void CropTheImage(Uri imageUrl) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(imageUrl, "image/*");
        cropIntent.putExtra("cropWidth", "true");
        cropIntent.putExtra("outputX", cropTargetWidth);
        cropIntent.putExtra("outputY", cropTargetHeight);

		//设置输出Uri
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            File file = new File(mContext.getCacheDir(), "test1.jpg");
            copyUrl = Uri.fromFile(file);
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            cropIntent.putExtra("output", contentUri);
        } else {
            File copyFile = FileHelper.createFileByType(mContext, destType, String.valueOf(System.currentTimeMillis()));
            copyUrl = Uri.fromFile(copyFile);
            cropIntent.putExtra("output", copyUrl);
        }
        startActivityForResult(cropIntent, REQUEST_CODE_CROP_PIC);
    }

	//FileHelper.java
	public static File createFileByType(Context mContext, int type, String name) {
        if (TextUtils.isEmpty(name)) {
            name = ".pic";
        }

        switch (type) {
            case JPEG:
                name = name + ".jpg";
                break;
            case PNG:
                name = name + ".png";
                break;
            default:
                break;

        }
        return new File(getTempDirectoryPath(mContext), name);

    }
```

#### 采样`(Sample)` ####
宽高**同时同量等比缩放**，内存占用量 / 取样之前内存占用量 = `1 / (inSampleSize * inSampleSize)`。
**`inSampleSize`比1小的话会被当做1，任何`inSampleSize`的值会被取接近2的幂值。**

```java
    private Bitmap getRealCompressedBitmap(String pathName, int reqWidth, int reqHeight) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        int width = options.outWidth / 2;
        int height = options.outHeight / 2;
        int inSampleSize = 1;

        while (width / inSampleSize >= reqWidth && height / inSampleSize >= reqHeight) {
            inSampleSize = inSampleSize * 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, options);
        showBitmapInfos(pathName);
        return bitmap;
    }

	链接：https://juejin.im/post/58bc1f11ac502e006b0957b7

```
---
### [`Bitmap`与`Matrix`变形](https://juejin.im/entry/56f0d244f3609a00548c44a8) ###


---


