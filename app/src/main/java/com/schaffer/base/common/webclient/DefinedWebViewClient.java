package com.schaffer.base.common.webclient;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 处理各种通知 & 请求事件
 *
 * @author SchafferWang
 * @date 2017/7/8
 */

public abstract class DefinedWebViewClient extends WebViewClient {

    /**
     * 打开网页时不调用系统浏览器， 而是在本WebView中显示
     *
     * @param view WebView
     * @param url  Path
     * @return boolean
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if ((url.contains("android_asset") && url.contains("file:///")) || url.contains("http://") || url.contains("content://")){
        view.loadUrl(url);
//        }
        return true;
    }

    /**
     * 加载开始
     *
     * @param view
     * @param url
     * @param favicon
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    /**
     * 加载结束
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.loadUrl("javascript:new function() {\n" +
                "\t\t\t\tvar imgs = document.getElementsByTagName('img');\n" +
                "\t\t\t\tvar paths = \"\";\n" +
                "\n" +
                "\t\t\t\tfor(var i = 0; i < imgs.length; i++) {\n" +
                "\t\t\t\t\tpaths += imgs[i].src + \"\\n\";\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\tfor(var i = 0; i < imgs.length; i++) {\n" +
                "\t\t\t\t\timgs[i].onclick = function() {\n" +
                "\t\t\t\t\t\t//点击出现显示图片界面\n" +
                "\t\t\t\t\t\tvar u = navigator.userAgent;\n" +
                "\t\t\t\t\t\tvar isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端\n" +
                "//\t\t\t\t\t\tvar isiOS = !!u.match(/\\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端\n" +
                "\t\t\t\t\t\tif(isAndroid) {\n" +
                "\t\t\t\t\t\t\twindow.android.getImgsPath2Show(paths,i);\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t}");
    }

    /**
     * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
     *
     * @param view
     * @param url
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    /**
     * 加载页面的服务器出现错误时（如404）调用。
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        callbackErrorInfo(errorCode, description, failingUrl);
    }

    /**
     * 加载失败时回调
     *
     * @param errorCode   错误码
     * @param description 错误描述
     * @param failingUrl  加载失败的链接
     */
    protected abstract void callbackErrorInfo(int errorCode, String description, String failingUrl);


    /**
     * 处理https请求 webView默认是不处理https请求的
     *
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        /* 等待证书响应 */
        handler.proceed();
        // handler.cancel();      //表示挂起连接，为默认方式
        // handler.handleMessage(null);    //可做其他处理
    }
}
