package com.schaffer.base.common.base;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.schaffer.base.R;
import com.schaffer.base.common.constants.Constants;
import com.schaffer.base.presenter.WebPresenter;


/**
 * Created by AndroidSchaffer on 2017/9/21.
 */

public class WebActivity extends BaseActivity<WebActivity, WebPresenter> {


    protected WebView webView;

    @Override
    protected void inflateView() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public void initView() {
        webView = (WebView) findViewById(R.id.web_wv_web);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);//js交互
        settings.setAllowFileAccess(true);//可访问文件
        settings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        settings.setUseWideViewPort(true);//图片调整到适合webView的大小
        settings.setSupportZoom(true);//支持缩放
        settings.setNeedInitialFocus(false);
        settings.setSupportMultipleWindows(true);
        settings.setDomStorageEnabled(true);//开启(离线加载) DOM storage API 功能
        settings.setDatabaseEnabled(true);   //开启 database storage API 功能
        settings.setAppCacheEnabled(true);//开启 Application Caches 功能
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.length() == 0 || title.length() > 8) return;
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
            webView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        super.onBackPressed();
    }
}
