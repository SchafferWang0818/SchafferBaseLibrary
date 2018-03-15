package com.schaffer.base.common.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.schaffer.base.R;
import com.schaffer.base.common.constants.Constants;
import com.schaffer.base.common.utils.FileIOUtils;
import com.schaffer.base.common.utils.ImageUtils;
import com.schaffer.base.common.utils.LtUtils;
import com.schaffer.base.common.utils.NetworkUtils;
import com.schaffer.base.common.webclient.DefinedWebChromeClient;
import com.schaffer.base.common.webclient.DefinedWebViewClient;
import com.schaffer.base.ui.activity.ImgsShowActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SchafferWang on 2017/7/8.
 */

public class BaseWebActivity extends AppCompatActivity {
    private static final String TAG = "Schaffer-BaseWebActiviy";
    private WebView mWvWeb;
    private FrameLayout mFrameContent;
    private static final int REQUEST_CODE_PHOTO_CAMERA_DEFAULT = 13;
    private static final int REQUEST_CODE_PHOTO_CAMERA_COMPRESS = 14;
    private static final int REQUEST_CODE_PHOTO_ALBUM = 15;
    private static final int REQUEST_CODE_PERMISSION = 19;
    private static final int REQUEST_CODE_PERMISSIONS = 20;
    private List<Bitmap> mBitmapList;
    private boolean isGetPicture;
    private String tempPath;
    static final String INTENT_DATA_IMG_PATHS = "img_paths";
    static final String INTENT_DATA_IMG_RES = "img_resIds";
    static final String INTENT_DATA_IMG_CURRENT_INDEX = "img_current";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mBitmapList = new ArrayList<>();
        setToolbar();
        inflateAndFindView();
        initWebSettings();
        initWebClients();
        String url = this.getIntent().getStringExtra(Constants.INTENT_WEB_URL);
        if (!TextUtils.isEmpty(url)) {
            if (!(url.trim().startsWith("http://")
                    || url.startsWith("https://"))) {
                mWvWeb.loadUrl("http://" + url);
            } else if (getIntent().getBooleanExtra(Constants.INTENT_WEB_PDF, false)) {
                /* 当打开一个pdf时 */
                mWvWeb.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + url);
            } else {
                mWvWeb.loadUrl(url);
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(in);
            }
        } else {
            showToast("数据错误，请稍后再试");
            finish();
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.layout_toolbar_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void inflateAndFindView() {
        mFrameContent = (FrameLayout) findViewById(R.id.layout_group_content);
        mWvWeb = new WebView(this.getApplicationContext());
        mFrameContent.addView(mWvWeb, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initWebSettings() {
        WebSettings settings = mWvWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setNeedInitialFocus(false);
        settings.setSupportMultipleWindows(true);
        /* 可以用于预览文件 */
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setAllowFileAccessFromFileURLs(true);
        }

        //5.0 以后 https不可以直接加载http资源
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setCacheSettings();
    }

    private void initWebClients() {
        mWvWeb.setWebViewClient(new DefinedWebViewClient() {
            @Override
            protected void callbackErrorInfo(int errorCode, String description, String failingUrl) {
                showSnackbar("加载失败,错误码:" + errorCode, Snackbar.LENGTH_SHORT);
            }
        });
        mWvWeb.setWebChromeClient(new DefinedWebChromeClient(inputListener) {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                showJsAlert(message, result);
                return true;
            }
        });
    }

    private ValueCallback<Uri> mFilePathCallback;
    private ValueCallback<Uri[]> mFilePathArrayCallback;
    private Uri mPictureResults;
    private Uri[] mPictureArrayResults;
    private DefinedWebChromeClient.FileInputListener inputListener = new DefinedWebChromeClient.FileInputListener() {
        @Override
        public void onOpenFileChooser(ValueCallback<Uri> valueCallback) {
            mFilePathCallback = valueCallback;
        }

        @Override
        public void onShowFileChooser(ValueCallback<Uri[]> filePathsCallback) {
            mFilePathArrayCallback = filePathsCallback;
        }
    };


    /**
     * 取消操作
     */
    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
        if (mFilePathArrayCallback != null) {
            mFilePathArrayCallback.onReceiveValue(null);
            mFilePathArrayCallback = null;
        }
    }

    /**
     * 压缩图片并设值
     *
     * @param path 路径
     */
    private void compressAndSetValue(String path) {
        Bitmap smallBitmap = ImageUtils.getSmallBitmap(path);
        mBitmapList.add(smallBitmap);
        ImageUtils.saveBitmap(smallBitmap, 70, path, Bitmap.CompressFormat.JPEG);
        File file = new File(path);
        if (file != null) {
            if (mFilePathArrayCallback != null) {
                mPictureArrayResults = new Uri[]{Uri.fromFile(file)};
                mFilePathArrayCallback.onReceiveValue(mPictureArrayResults);
                mFilePathArrayCallback = null;
            } else if (mFilePathCallback != null) {
                mPictureResults = Uri.fromFile(file);
                mFilePathCallback.onReceiveValue(mPictureResults);
                mFilePathCallback = null;
            }
            isGetPicture = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHOTO_ALBUM || requestCode == REQUEST_CODE_PHOTO_CAMERA_DEFAULT || requestCode == REQUEST_CODE_PHOTO_CAMERA_COMPRESS) {
                cancelFilePathCallback();
            }
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_PHOTO_CAMERA_COMPRESS:
                //摄像机获取压缩图
                if (data != null && data.getData() != null) {
                    /* Bundle bundle = data.getExtras();Bitmap bitmap = (Bitmap) bundle.get("data");*/
                    if (TextUtils.isEmpty(tempPath)) {
                        compressAndSetValue(tempPath);
                        tempPath = null;
                    } else {
                        cancelFilePathCallback();
                    }
                } else {
                    cancelFilePathCallback();
                }

                break;
//            case REQUEST_CODE_PHOTO_CAMERA_DEFAULT://摄像机获取原图
//
//
//                break;
            case REQUEST_CODE_PHOTO_ALBUM:
                //相册获取
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    String path = Environment.getRootDirectory().getAbsolutePath() + System.currentTimeMillis() + ".jpg";
                    try {
                        FileIOUtils.writeFileFromIS(path, getContentResolver().openInputStream(uri));
                        compressAndSetValue(path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    cancelFilePathCallback();
                }
                break;
            default:

                break;
        }

    }

    /**
     * 显示javaScript弹框信息
     *
     * @param message 信息内容
     * @param result  返回结果
     */
    private void showJsAlert(String message, final JsResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseWebActivity.this);
        builder.setTitle("来自网页的消息");
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 与Js交互并调用JavaScript函数
     *
     * @param functionName 函数名称必须有"()"或者有参
     */
    protected void callJsFunction(final String functionName) {
        mWvWeb.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWvWeb.evaluateJavascript("javascript:" + functionName, new ValueCallback<String>() {
                        //例如: javascript:callJS()
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                        }
                    });
                } else {
                    mWvWeb.loadUrl("javascript:" + functionName);
                }
            }
        });
    }


    /**
     * 从相机获取原图
     */
    /*public void getPhotoFromCameraDefault() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".png";
        Uri uri = Uri.fromFile(new File(tempPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_PHOTO_CAMERA_DEFAULT);
    }*/

    /**
     * 从相机获取压缩图
     */
    private void getPhotoFromCameraCompress() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".png";
        Uri uri = Uri.fromFile(new File(tempPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_PHOTO_CAMERA_COMPRESS);
    }


    /**
     * 设置javaScript调用android函数的接口,调用时当打开客户端为android时使用jsName.方法名()
     *
     * @param url        需要跳转的网页链接
     * @param oInterface 接口基类的子类
     * @param jsName     接口类在js中的名称
     */
    protected void setJsCallInterface(String url, JsInterface oInterface, String jsName) {
        mWvWeb.addJavascriptInterface(oInterface, jsName);
        mWvWeb.loadUrl(url);
// for example:
//        mWvWeb.addJavascriptInterface(new JavaScriptInterface(), "android");
//        mWvWeb.loadUrl(web);
    }


    //@SuppressLint("JavascriptInterface")
    public static class JsInterface {

        private final Context context;

        public JsInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void onBack() {

        }

        @JavascriptInterface
        public void onFinish() {

        }

        @JavascriptInterface
        public Object onReturn() {
            return null;
        }

        /**
         * 获取图片时需要 设置当前类的代用名为"android"
         *
         * @param pathStr      所有图片路径
         * @param currentIndex 当前点击的图片顺序
         */
        @JavascriptInterface
        public void getImgsPath2Show(String pathStr, int currentIndex) {
            if (TextUtils.isEmpty(pathStr.trim())) {
                return;
            }
            String[] paths = pathStr.split("\n");
            int index = currentIndex >= paths.length ? 0 : currentIndex;
            Intent intent = new Intent(context, ImgsShowActivity.class);
            ArrayList<String> pathList = new ArrayList<>();
            for (int i = 0; i < paths.length; i++) {
                pathList.add(paths[i]);
            }
            intent.putStringArrayListExtra(INTENT_DATA_IMG_PATHS, pathList);
            intent.putExtra(INTENT_DATA_IMG_CURRENT_INDEX, index);
            context.startActivity(intent);
        }

    }


    /**
     * 已弃用的防止内存泄漏方案
     *
     * @param windowManager
     */
    @Deprecated
    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);
            if (null == configCallback) {
                return;
            }
            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        if (mWvWeb != null) {
            mWvWeb.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWvWeb.clearHistory();
            mWvWeb.clearCache(true);
            mFrameContent.removeView(mWvWeb);
            mWvWeb.destroy();
            mWvWeb = null;
        }


        super.onDestroy();
    }

    public void showLog(String msg) {
        LtUtils.w(TAG, msg);
    }

    public void showLog(int resId) {
        showLog(getString(resId));
    }

    public void showToast(String msg) {
        showLog(msg);
        LtUtils.showToastShort(this, msg);
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showSnackbar(String content, int duration) {
        if (duration != Snackbar.LENGTH_SHORT && duration != Snackbar.LENGTH_LONG) {
            return;
        }
        Snackbar.make(mFrameContent, content, duration).show();
    }

    @Override
    public void onBackPressed() {
        if (mWvWeb.canGoBack()) {
            mWvWeb.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWvWeb != null) {
            mWvWeb.resumeTimers();
            mWvWeb.getSettings().setJavaScriptEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWvWeb != null) {
            mWvWeb.pauseTimers();
            mWvWeb.getSettings().setJavaScriptEnabled(false);
        }
    }


    public void setCacheSettings() {
        if (mWvWeb != null) {
            WebSettings settings = mWvWeb.getSettings();
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
