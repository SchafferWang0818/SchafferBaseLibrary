# WebView 与 前端交互 #


- 参考
	- [ **Android：这是一份全面&详细的Webview使用攻略** ](https://juejin.im/post/5924dbf58d6d810058fdde43)
	- [ **最全面总结 Android WebView与 JS 的交互方式** ](https://www.jianshu.com/p/345f4d8a5cfa)

---
## WebView 与常用类  ##

### ① `WebView`  ###

#### 1.  `WebView` 函数

```java
//方式1. 加载一个网页：
webView.loadUrl("http://www.google.com/");
//方式2：加载apk包中的html页面
webView.loadUrl("file:///android_asset/test.html");
//方式3：加载手机本地的html页面
webView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");
// 方式4： 加载 HTML 页面的一小段内容
// 参数说明：
// 参数1：需要截取展示的内容
// 内容里不能出现 ’#’, ‘%’, ‘\’ , ‘?’ 这四个字符，若出现了需用 %23, %25, %27, %3f 对应来替代，否则会出现异常
// 参数2：展示内容的类型
// 参数3：字节码
WebView.loadData(String data, String mimeType, String encoding);


//激活WebView为活跃状态
webView.onResume();
//通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
webView.onPause();
//它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
webView.pauseTimers();
//恢复pauseTimers状态
webView.resumeTimers();
//销毁Webview
webView.destroy();


//是否可以后退
webView.canGoBack();
//后退网页
webView.goBack();
//是否可以前进
webView.canGoForward();
//前进网页
webView.goForward();
//如果steps为负数则为后退，正数则为前进
webView.goBackOrForward(int steps);


//针对整个应用程序,清除网页访问留下的缓存
webView.clearCache(true);
//清除当前webview访问的历史记录
webView.clearHistory();
//仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
webView.clearFormData();
```
#### 2. `WebView` 常见问题


---
### ② `WebSettings`  ###
#### 1. 函数 ####

```java
//访问的页面中要与Javascript交互，则webview必须设置支持Javascript,
//加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）,需要在onResume(),onStop()设置开关
webSettings.setJavaScriptEnabled(boolean enable);  

//设置编码格式
webSettings.setDefaultTextEncodingName("utf-8");
//支持通过JS打开新窗口 
webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

//5.0 以后 https不可以直接加载http资源
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
}

webSettings.setNeedInitialFocus(false);
webSettings.setSupportMultipleWindows(true);
//---------------------------文件、图片、缓存--------------------------------------------------------------------------

//设置可以访问文件 
webSettings.setAllowFileAccess(true); 
//支持自动加载图片
webSettings.setLoadsImagesAutomatically(true); 

//关闭webview中缓存 
webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 

//开启(离线加载) DOM storage API 功能
webSettings.setDomStorageEnabled(true);
//开启 database storage API 功能       
webSettings.setDatabaseEnabled(true);

//开启 Application Caches 功能     
webSettings.setAppCacheEnabled(true);
//设置 Application Caches 缓存目录
webSettings.setAppCachePath(cacheDirPath);

//---------------------------网页自适应与缩放--------------------------------------------------------------------------
//两者合用自适应
//将图片调整到适合webview的大小
webSettings.setUseWideViewPort(true);  
// 缩放至屏幕的大小
webSettings.setLoadWithOverviewMode(true);


//支持缩放，默认为true。是下面那个的前提。
webSettings.setSupportZoom(true); 
//设置内置的缩放控件。若为false，则该WebView不可缩放
webSettings.setBuiltInZoomControls(true); 
//隐藏原生的缩放控件
webSettings.setDisplayZoomControls(false); 


```

#### 2. 属性 ####

```java
//------------------------------WebView缓存---------------------------------------------------------------------------------
WebSettings.LOAD_NO_CACHE : 	不使用缓存，只从网络获取数据. 
WebSettings.LOAD_DEFAULT :  	根据cache-control决定是否从网络上取数据。
WebSettings.LOAD_CACHE_ONLY : 		不使用网络，只读取本地缓存数据
WebSettings.LOAD_CACHE_ELSE_NETWORK : 只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

```


---
### ③ `WebViewClient` ###

```java

/**
 * 打开网页时不调用系统浏览器， 而是在本WebView中显示
 */
public boolean shouldOverrideUrlLoading(WebView view, String url);

public boolean onLoadResource(WebView view, String url);

public void onPageStarted(WebView view, String url, Bitmap favicon);

public void onPageFinished(WebView view, String url);

/**
 * 加载页面的服务器出现错误时（如404）调用。
 */
public void onReceivedError(WebView view, int errorCode, 
			String description, String failingUrl);
/**
 * 处理https请求 webView默认是不处理https请求的
 * 调用handler.proceed()等待证书响应;
 */		
public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error);


```
---
### ④ `WebChromeClient`  ###

```java
public void onProgressChanged(WebView view, int newProgress);

public void onReceivedTitle(WebView view, String title);

public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result);

public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result);
			
public boolean onJsPrompt(WebView view, String url, String message, 
			String defaultValue, final JsPromptResult result);

```

---
### ⑤ 和Js的交互 ###

#### JavaScript 调用 Android  ####
- 通过 `WebView # addJavascriptInterface()`进行对象映射

	```java
	//定义接口
	public class AndroidtoJs extends Object {
	
	    // 定义JS需要调用的方法
	    // 被JS调用的方法必须加入@JavascriptInterface注解
	    @JavascriptInterface
	    public void hello(String msg) {
	        System.out.println("JS调用了Android的hello方法");
	    }
	}
	//使用接口
	mWebView.addJavascriptInterface(new AndroidtoJs(), "test");
	
	```
	
	```
	//JavaScript
	<script>
		function callAndroid(){
			// 由于对象映射，所以调用test对象等于调用Android映射的对象
			test.hello("js调用了android中的hello方法");
		}
	</script>
	```
	- 优点 : 使用简单
	- 缺点 : [存在严重的漏洞问题](https://www.jianshu.com/p/3a345d27cd42)

	
	
- 通过 `WebViewClient # shouldOverrideUrlLoading ()`方法回调拦截 url

	```
	<script>
		function callAndroid(){
			/*约定的url协议为：js://webview?arg1=111&arg2=222*/
			document.location = "js://webview?arg1=111&arg2=222";
		}
	
		function returnResult(result){
		    alert("result is" + result);
		}
	</script>
	
	```
	```java
	
	mWebView.setWebViewClient(new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 根据scheme（协议格式） & authority（协议名）判断（前两个参数）
			Uri uri = Uri.parse(url);                                 
			if ( uri.getScheme().equals("js")) {
				if (uri.getAuthority().equals("webview")) {
					System.out.println("js调用了Android的方法");
					// 可以在协议上带有参数并传递到Android上
					HashMap<String, String> params = new HashMap<>();
					Set<String> collection = uri.getQueryParameterNames();
					
					// 回复
					mWebView.loadUrl("javascript:returnResult(" + result + ")");
				}
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
	});
	
	```

- 通过 `WebChromeClient#onJsAlert/Confirm/Prompt()`方法回调拦截JS对话框`alert()`、`confirm()`、`prompt()` 消息
![Paste_Image](https://upload-images.jianshu.io/upload_images/944365-1385f748618af886.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)


#### Android 调用 JavaScript ####

![方式对比图](https://upload-images.jianshu.io/upload_images/944365-30f095d4c9e638fd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

- 通过`WebView # loadUrl()`

	```java
	mWebView.loadUrl("javascript:callJS()");
	//只能通过WebChromeClient类的函数进行信息回调
	
	```

- 通过`WebView # evaluateJavascript()`

	```java
	mWebView.evaluateJavascript（"javascript:callJS()", 
				new ValueCallback<String>() {
				        @Override
				        public void onReceiveValue(String value) {
				            //此处为 js 返回的结果
				        }
	});

	```
---