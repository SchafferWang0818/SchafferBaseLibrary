package com.schaffer.base.common.webClient;

import android.net.Uri;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
 * Created by SchafferWang on 2017/7/8.
 */

public class DefinedWebChromeClient extends WebChromeClient {

    private FileInputListener inputListener;

    public DefinedWebChromeClient(FileInputListener inputListener) {
        this.inputListener = inputListener;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

        return super.onJsAlert(view, url, message, result);
    }

    /**
     * 弹出确认框
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * 弹出输入框
     *
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (inputListener != null) {
            inputListener.onShowFileChooser(filePathCallback);
        }
        return true;
    }

    //For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        if (inputListener != null) {
            inputListener.onOpenFileChooser(valueCallback);
        }
    }

    public interface FileInputListener {
        void onOpenFileChooser(ValueCallback<Uri> valueCallback);

        void onShowFileChooser(ValueCallback<Uri[]> filePathsCallback);
    }


}
