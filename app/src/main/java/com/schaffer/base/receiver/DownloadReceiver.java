package com.schaffer.base.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/2/1
 * Project : SchafferBaseLibrary
 * Package : com.schaffer.base.receiver
 * Description :
 */

public class DownloadReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long download_id = intent.getLongExtra("download_id", -1);
                String mimeType = intent.getStringExtra("download_mimeType");
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri file_uri = dm.getUriForDownloadedFile(download_id);
                if (file_uri != null) {
                    try {
                        File file = new File(new URI(file_uri.toString()));
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        if (!TextUtils.isEmpty(mimeType)) {
                            install.setDataAndType(file_uri, mimeType);
                        }
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(install);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }
}
