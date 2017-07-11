package com.schaffer.base.common.webClient;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 处理各种通知 & 请求事件
 * Created by SchafferWang on 2017/7/8.
 */

public class DefinedWebViewClient extends WebViewClient {

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
     * 错误时调用
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//        super.onReceivedError(view, errorCode, description, failingUrl);
        view.loadUrl("http://blank");
    }


}
