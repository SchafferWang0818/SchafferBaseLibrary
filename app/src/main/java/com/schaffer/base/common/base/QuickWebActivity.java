package com.schaffer.base.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.schaffer.base.R;
import com.schaffer.base.common.utils.AppUtils;
import com.schaffer.base.common.utils.ConvertUtils;
import com.schaffer.base.common.utils.NetworkUtils;
import com.schaffer.base.presenter.QuickWebPresenter;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/1/25
 * Project : fentu_android_new
 * Package : com.billliao.fentu.ui.activity
 * Description :
 */

public class QuickWebActivity extends BaseEmptyActivity<QuickWebActivity, QuickWebPresenter> implements View.OnClickListener {
    private ImageView mIvBack;
    private TextView mTvTitle;
    private Toolbar mToolbar;
    private FrameLayout mFrameParent;
    private WebView mWvWeb;
    private ProgressBar mPbProgress;
    boolean showProgress = false;
    private View mErrorView;

    @Override
    protected void inflateView() {
        setContentView(R.layout.activity_base);
        initView();
    }

    @Override
    public void initView() {
        super.initView();
        mIvBack = (ImageView) findViewById(R.id.layout_toolbar_iv_back);
        mIvBack.setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.layout_toolbar_tv_title);
        mToolbar = (Toolbar) findViewById(R.id.layout_toolbar_tb);
        mToolbar.setOnClickListener(this);
        mFrameParent = (FrameLayout) findViewById(R.id.layout_group_content);
        mWvWeb = new WebView(this);
        FrameLayout.LayoutParams fl0 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPbProgress = new ProgressBar(this);
        mPbProgress.setBackgroundColor(Color.WHITE);
        mPbProgress.setDrawingCacheBackgroundColor(Color.parseColor("#FF4081"));
        mPbProgress.setMax(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mPbProgress.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        mPbProgress.setClickable(false);
        FrameLayout.LayoutParams fl1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(2));
        fl1.gravity = Gravity.BOTTOM;
        FrameLayout.LayoutParams fl2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mFrameParent.addView(mWvWeb, fl0);
        mErrorView = View.inflate(this, R.layout.layout_load_error, null);
        mFrameParent.addView(mErrorView, fl2);
        mErrorView.setVisibility(View.GONE);
        mFrameParent.addView(mPbProgress, fl1);
        if (mErrorView != null) {
            mErrorView.findViewById(R.id.layout_error_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWebRefresh(v);
                }
            });
            mErrorView.findViewById(R.id.layout_error_go_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWebGoBack(v);
                }
            });
        }
        setWebSettings();
    }


    public void setWebSettings() {
        if (mWvWeb != null) {
            WebSettings settings = mWvWeb.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            settings.setSupportZoom(true);
            settings.setNeedInitialFocus(false);
            settings.setSupportMultipleWindows(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
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
            mWvWeb.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 0 || newProgress == 100) {
                        showProgress = false;
                        mPbProgress.setAlpha(0);
                    }
                    if (newProgress > 0 && newProgress < 100) {
                        if (!showProgress) {
                            showProgress = true;
                            mPbProgress.setAlpha(1);
                        }
                        mPbProgress.setProgress(newProgress);
                    }

                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (!TextUtils.isEmpty(title) && title.length() < 5) {
                        mTvTitle.setText(title.substring(0, 4) + "...");
                    } else {
                        mTvTitle.setText(AppUtils.getAppName());
                    }
                }

                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    new AlertDialog.Builder(QuickWebActivity.this)
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    return true;
                }

                @Override
                public boolean onJsTimeout() {
                    showSnackbar("网络状态不太好,重新为您刷新");
                    //todo 是否需要刷新界面
                    new AlertDialog.Builder(QuickWebActivity.this).setMessage("网络状态不太好,是否刷新?")
                            .setNegativeButton("不了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("刷新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mWvWeb.reload();
                                }
                            }).create().show();
                    return super.onJsTimeout();
                }

                @Override
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                    if (!TextUtils.isEmpty(message)) {
                        final Snackbar snackbar = Snackbar.make(mFrameParent, message, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("我知道了", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }

            });
            mWvWeb.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("tfp://")) {
                        view.loadUrl(url);
                    } else {
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(in);
                    }
                    return true;
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (mErrorView != null) {
                        mErrorView.setVisibility(View.VISIBLE);
                        showLog(">>>desc: " + description + " \nurl: " + failingUrl);
                        ((TextView) mErrorView.findViewById(R.id.layout_tv_errorInfo)).setText("网页加载失败\n错误代码:" + errorCode);
                        mErrorView.findViewById(R.id.layout_error_go_back).setVisibility(mWvWeb.canGoBack() ? View.VISIBLE : View.GONE);
                    }
                }
            });
            mWvWeb.addJavascriptInterface(new JsInterface(new WeakReference<Activity>(this)), "mAWindow");
        }
    }

    @Override
    protected QuickWebPresenter initPresenter() {
        return new QuickWebPresenter();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("INTENT_WEB_URL");
        if (TextUtils.isEmpty(url)) {
            showToast("网址不能为空");
            finish();
        } else {
            if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("tfp://")) {
                mWvWeb.loadUrl(url);
            } else {
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(in);
            }
        }
    }

    @Override
    protected void refreshData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_toolbar_iv_back:
                onBackPressed();
                break;
            case R.id.layout_toolbar_tb:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWvWeb.canGoBack()) {
            mWvWeb.goBack();
        } else {
            new AlertDialog.Builder(this).setMessage("确定要退出当前页面么?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    QuickWebActivity.super.onBackPressed();
                }
            }).setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWvWeb != null) {
            mWvWeb.resumeTimers();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWvWeb != null) {
            mWvWeb.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWvWeb != null) {
            mWvWeb.clearCache(true);
            mWvWeb.clearHistory();
            mWvWeb.destroy();
            mWvWeb = null;
        }
    }

    public void onWebRefresh(View view) {
        if (mWvWeb != null) {
            mErrorView.setVisibility(View.GONE);
            mWvWeb.reload();
        }
    }

    public void onWebGoBack(View view) {
        if (mWvWeb != null) {
            mErrorView.setVisibility(View.GONE);
            onBackPressed();
        }
    }

    @SuppressLint("JavascriptInterface")
    public static class JsInterface {

        private final WeakReference<Activity> wr;

        public JsInterface(WeakReference<Activity> wr) {
            this.wr = wr;
        }

        @JavascriptInterface
        public void closeThisWindow() {
            if (wr != null && wr.get() != null) {
                wr.get().finish();
            }
        }

    }
}
