package com.schaffer.base.common.base;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.constants.Constants;
import com.schaffer.base.common.utils.NetworkUtils;
import com.schaffer.base.presenter.WebPresenter;

import java.io.File;


/**
 * @author AndroidSchaffer
 * @date 2017/9/21
 */

public class WebActivity extends BaseEmptyActivity<WebActivity, WebPresenter> {


    protected WebView webView;

    @Override
    protected void inflateView() {
        inflateContent(R.layout.activity_web);
        setToolbar(View.VISIBLE);
        initView();
        setRightText("网页打开", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url = Uri.parse(webView.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                intent.setAction("android.intent.action.VIEW");
                startActivity(intent);
            }
        });
        setRightTextColor(Color.parseColor("#323232"));
    }

    @Override
    protected WebPresenter initPresenter() {
        return new WebPresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void refreshData() {

    }

    @Override
    public void initView() {
        webView = findViewById(R.id.web_wv_web);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //js交互
        settings.setAllowFileAccess(true);
        //可访问文件
        settings.setLoadWithOverviewMode(true);
        //缩放至屏幕大小
        settings.setUseWideViewPort(true);
        //图片调整到适合webView的大小
        settings.setSupportZoom(true);
        //支持缩放
        settings.setNeedInitialFocus(false);
        settings.setSupportMultipleWindows(true);
        /* 可以用于预览文件 */
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setAllowFileAccessFromFileURLs(true);
        }

        setCacheSettings();
        //开启 Application Caches 功能
        //5.0 以后 https不可以直接加载http资源
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setActivityTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient());
        String url = this.getIntent().getStringExtra(Constants.INTENT_WEB_URL);
        String title = getIntent().getStringExtra(Constants.INTENT_WEB_TITLE);
        if (title != null) {
            setActivityTitle(title);
        }

        if (!TextUtils.isEmpty(url)) {
            if (!(url.trim().startsWith("http://")
                    || url.startsWith("https://"))) {
                webView.loadUrl("http://" + url);
            } else if (getIntent().getBooleanExtra(Constants.INTENT_WEB_PDF, false)) {
                /* 当打开一个pdf时 */
                webView.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + url);
            } else {
                webView.loadUrl(url);
            }
        }else{
            showToast("数据错误，请稍后再试");
            finish();
        }
        TextView mTvTitle = (TextView) findViewById(R.id.layout_toolbar_tv_title);
        if (mTvTitle != null) {
            mTvTitle.setFocusable(true);
            mTvTitle.setFocusableInTouchMode(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.resumeTimers();
            webView.getSettings().setJavaScriptEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webView != null) {
            webView.pauseTimers();
            webView.getSettings().setJavaScriptEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
        }
    }

    public void setCacheSettings() {
        if (webView != null) {
            WebSettings settings = webView.getSettings();
            if (NetworkUtils.isConnected()) {
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setAppCacheEnabled(true);
            try {
                File cacheDir = getCacheDir();
                settings.setAppCachePath(cacheDir.getAbsolutePath() + (cacheDir.isDirectory() ? "webCache" : "/webCache"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
