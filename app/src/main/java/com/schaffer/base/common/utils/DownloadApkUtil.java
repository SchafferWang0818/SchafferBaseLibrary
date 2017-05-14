package com.schaffer.base.common.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;

import net.ezcx.ptaxi.apublic.constant.Constant;
import net.ezcx.ptaxi.apublic.receiver.DownloadReceiver;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by EZ on 2017/2/20.
 */

public class DownloadApkUtil {

    private volatile static DownloadApkUtil instance;
    private  DownloadReceiver mDownloadReceiver;
    private  DownloadManager mDownloadManager;

    private DownloadApkUtil() {
    }

    public static DownloadApkUtil getInstance() {
        if (instance == null) {
            synchronized (DownloadApkUtil.class) {
                if (instance == null) {
                    instance = new DownloadApkUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 下载APK
     * @param context application
     * @param url 下载链接
     */
    public void downloadApk(Context context, String url) {

        if (mDownloadManager == null)
            mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        long referenceId = (long) SPUtils.get(context, Constant.SP_DOWNLOAD_ID, (long) -1);
        if (referenceId != -1) {//移除下载中的任务
            SPUtils.remove(context, Constant.SP_DOWNLOAD_ID);
            mDownloadManager.remove(referenceId);
        }

        Uri uri = Uri.parse(url);
        String path = FileUtil.getExternalStoragePath() + File.separator + "ptaxi";
        LUtil.e(path);
        if (FileUtil.mkdirIfNotFound(path)) {
            File file = new File(path, Constant.APK_NAME);
            if (file.exists()){
                file.delete();
            }
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("PTaxi")
                    .setDescription("更新版本")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir("ptaxi", Constant.APK_NAME)
                    .setVisibleInDownloadsUi(true)
                    .allowScanningByMediaScanner();
            long reference = mDownloadManager.enqueue(request);
            SPUtils.put(context, Constant.SP_DOWNLOAD_ID, reference);

            if (mDownloadReceiver == null) {
                mDownloadReceiver = new DownloadReceiver();
                context.registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
            ToastSingleUtil.showShort(context, "开始下载，在通知栏查看进度...");
        }
    }

}
