package com.schaffer.base.common.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;

import com.schaffer.base.receiver.DownloadReceiver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/2/1
 * Project : SchafferBaseLibrary
 * Package : com.schaffer.base.common.utils
 * Description : 包装DownloadManager
 */

public class DownloadUtils {

    /**
     * 打开或者安装文件
     *
     * @param context 当前上下文
     * @param path    文件路径
     */
    public static void install(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开或者安装文件
     *
     * @param context 当前上下文
     * @param uri     文件路径
     */
    public static void install(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 下载文件
     *
     * @param context 当前上下文
     * @param config  下载文件的配置{@link DownloadUtils.DownloadFileConfig}
     * @param handler 主线程回调下载进度所需Handler
     * @return 下载文件入队时设定的download_id
     */
    public static long download(final Context context, final DownloadFileConfig config, final Handler handler) {
        if (context == null || config == null) {
            return -1;
        }
        if (TextUtils.isEmpty(config.getUrl()) || config.getUrl().trim().length() <= 0) {
            return -1;
        }
        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(config.getUrl()));
         /* 是否使用私有目录 */
        if (config.isDirPrivate()) {
            request.setDestinationInExternalFilesDir(context.getApplicationContext(), config.saveDirType, config.saveSubPath);
        } else {
            request.setDestinationInExternalPublicDir(config.saveDirType, config.saveSubPath);
        }

        //是否允许漫游状态下，执行下载操作
        request.setAllowedOverRoaming(true);
        //是否允许“计量式的网络连接”执行下载操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            request.setAllowedOverMetered(true);
        }
        /* 是否使用数据流量 */
        if (NetworkUtils.getWifiEnabled()) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        } else {
            if (NetworkUtils.isConnected() && config.isUseMobileNet()) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            } else {
                Toast.makeText(context, "当前无可用网络", Toast.LENGTH_SHORT).show();
                return -1;
            }
        }
        request.setTitle(config.getNotificationTitle());
        request.setDescription(config.getNotificationDesc());

        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(config.isAlwaysShowNotification() ?
                DownloadManager.Request.VISIBILITY_VISIBLE :
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

        if (!TextUtils.isEmpty(config.getFileMimeType())) {
            request.setMimeType(config.getFileMimeType());
        }

        if (config.getHeadValues() != null && config.getHeadValues().size() > 0) {
            for (String key : config.getHeadValues().keySet()) {
                request.addRequestHeader(key, config.getHeadValues().get(key));
            }
        }
        final long id = dm.enqueue(request);
        TimerTask task = null;
        if (config.isCallbackProgress()) {
            Timer timer = new Timer();
            final DownloadManager.Query query = new DownloadManager.Query();
            task = new TimerTask() {
                @Override
                public void run() {
                    Cursor cursor = dm.query(query.setFilterById(id));
                    if (cursor != null && cursor.moveToFirst()) {
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(context, DownloadReceiver.class);
                            in.setAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                            in.putExtra("download_id", id);
                            in.putExtra("download_mimeType", config.getFileMimeType());
                            context.sendBroadcast(in);
                            cancel();
                        }
                        if (handler != null) {
                            int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            int pro = (bytes_downloaded * 100) / bytes_total;
                            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                            Message msg = Message.obtain();
                            Bundle b = new Bundle();
                            b.putInt("download_progress", pro);
                            b.putString("download_title", title);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                        if (!cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                }
            };
            timer.schedule(task, 0, 1000);
        }


        if (task != null) {
            task.run();
        }
        return id;
    }

    public static class DownloadFileConfig implements Parcelable {
        /**
         * 下载路径
         */
        String url;
        /**
         * 是否允许使用手机数据
         */
        boolean useMobileNet;
        /**
         * 是否永远显示 notification 进度
         */
        boolean alwaysShowNotification;
        /**
         * 存储位置是否为仅本应用可见
         */
        boolean isDirPrivate;
        /**
         * 存储类型 移步{@link Environment } 类查看
         */
        String saveDirType = Environment.DIRECTORY_DOWNLOADS;
        /**
         * 设置文件名称和文件类型
         */
        String saveSubPath = "default_name.default_type";
        /**
         * 文件类型: 用于分情况打开,默认为apk
         */
        String fileMimeType = "application/vnd.android.package-archive";
        /**
         * 添加请求下载的网络链接的http头，比如User-Agent，gzip压缩等
         */
        HashMap<String, String> headValues = new HashMap<>();
        /**
         * 是否回调进度
         */
        boolean callbackProgress;
        /**
         * 通知描述
         */
        String notificationDesc;
        /**
         * 通知标题
         */
        String notificationTitle;

        protected DownloadFileConfig(Parcel in) {
            url = in.readString();
            useMobileNet = in.readByte() != 0;
            alwaysShowNotification = in.readByte() != 0;
            isDirPrivate = in.readByte() != 0;
            saveDirType = in.readString();
            saveSubPath = in.readString();
            fileMimeType = in.readString();
            callbackProgress = in.readByte() != 0;
            notificationTitle = in.readString();
            notificationDesc = in.readString();
        }

        public static final Creator<DownloadFileConfig> CREATOR = new Creator<DownloadFileConfig>() {
            @Override
            public DownloadFileConfig createFromParcel(Parcel in) {
                return new DownloadFileConfig(in);
            }

            @Override
            public DownloadFileConfig[] newArray(int size) {
                return new DownloadFileConfig[size];
            }
        };

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUseMobileNet() {
            return useMobileNet;
        }

        public void setUseMobileNet(boolean useMobileNet) {
            this.useMobileNet = useMobileNet;
        }

        public boolean isAlwaysShowNotification() {
            return alwaysShowNotification;
        }

        public void setAlwaysShowNotification(boolean alwaysShowNotification) {
            this.alwaysShowNotification = alwaysShowNotification;
        }

        public boolean isDirPrivate() {
            return isDirPrivate;
        }

        public void setDirPrivate(boolean dirPrivate) {
            isDirPrivate = dirPrivate;
        }

        public String getSaveDirType() {
            return saveDirType;
        }

        public void setSaveDirType(String saveDirType) {
            this.saveDirType = saveDirType;
        }

        public String getSaveSubPath() {
            return saveSubPath;
        }

        public void setSaveSubPath(String saveSubPath) {
            this.saveSubPath = saveSubPath;
        }

        public String getFileMimeType() {
            return fileMimeType;
        }

        public void setFileMimeType(String fileMimeType) {
            this.fileMimeType = fileMimeType;
        }

        public HashMap<String, String> getHeadValues() {
            return headValues;
        }

        public void setHeadValues(HashMap<String, String> headValues) {
            this.headValues = headValues;
        }

        public boolean isCallbackProgress() {
            return callbackProgress;
        }

        public void setCallbackProgress(boolean callbackProgress) {
            this.callbackProgress = callbackProgress;
        }

        public String getNotificationTitle() {
            return notificationTitle;
        }

        public void setNotificationTitle(String notificationTitle) {
            this.notificationTitle = notificationTitle;
        }

        public String getNotificationDesc() {
            return notificationDesc;
        }

        public void setNotificationDesc(String notificationDesc) {
            this.notificationDesc = notificationDesc;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
            dest.writeByte((byte) (useMobileNet ? 1 : 0));
            dest.writeByte((byte) (alwaysShowNotification ? 1 : 0));
            dest.writeByte((byte) (isDirPrivate ? 1 : 0));
            dest.writeString(saveDirType);
            dest.writeString(saveSubPath);
            dest.writeString(fileMimeType);
            dest.writeByte((byte) (callbackProgress ? 1 : 0));
            dest.writeString(notificationTitle);
            dest.writeString(notificationDesc);
        }
    }


    /**
     * 判断文件的类型
     */
    public static class MediaFileUtils {
        public static String sFileExtensions;

        // Audio
        public static final int FILE_TYPE_MP3 = 1;
        public static final int FILE_TYPE_M4A = 2;
        public static final int FILE_TYPE_WAV = 3;
        public static final int FILE_TYPE_AMR = 4;
        public static final int FILE_TYPE_AWB = 5;
        public static final int FILE_TYPE_WMA = 6;
        public static final int FILE_TYPE_OGG = 7;
        private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;
        private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_OGG;

        // MIDI
        public static final int FILE_TYPE_MID = 11;
        public static final int FILE_TYPE_SMF = 12;
        public static final int FILE_TYPE_IMY = 13;
        private static final int FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID;
        private static final int LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY;

        // Video
        public static final int FILE_TYPE_MP4 = 21;
        public static final int FILE_TYPE_M4V = 22;
        public static final int FILE_TYPE_3GPP = 23;
        public static final int FILE_TYPE_3GPP2 = 24;
        public static final int FILE_TYPE_WMV = 25;
        private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
        private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_WMV;

        // Image
        public static final int FILE_TYPE_JPEG = 31;
        public static final int FILE_TYPE_GIF = 32;
        public static final int FILE_TYPE_PNG = 33;
        public static final int FILE_TYPE_BMP = 34;
        public static final int FILE_TYPE_WBMP = 35;
        private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
        private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_WBMP;

        // Playlist
        public static final int FILE_TYPE_M3U = 41;
        public static final int FILE_TYPE_PLS = 42;
        public static final int FILE_TYPE_WPL = 43;
        private static final int FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U;
        private static final int LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL;

        //静态内部类
        static class MediaFileType {

            int fileType;
            String mimeType;

            MediaFileType(int fileType, String mimeType) {
                this.fileType = fileType;
                this.mimeType = mimeType;
            }
        }

        private static HashMap<String, MediaFileType> sFileTypeMap
                = new HashMap<String, MediaFileType>();
        private static HashMap<String, Integer> sMimeTypeMap
                = new HashMap<String, Integer>();

        static void addFileType(String extension, int fileType, String mimeType) {
            sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
            sMimeTypeMap.put(mimeType, new Integer(fileType));
        }

        static {
            addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
            addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
            addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
            addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
            addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
            addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma");
            addFileType("OGG", FILE_TYPE_OGG, "application/ogg");

            addFileType("MID", FILE_TYPE_MID, "audio/midi");
            addFileType("XMF", FILE_TYPE_MID, "audio/midi");
            addFileType("RTTTL", FILE_TYPE_MID, "audio/midi");
            addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
            addFileType("IMY", FILE_TYPE_IMY, "audio/imelody");

            addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
            addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
            addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
            addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
            addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
            addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");
            addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv");

            addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
            addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
            addFileType("GIF", FILE_TYPE_GIF, "image/gif");
            addFileType("PNG", FILE_TYPE_PNG, "image/png");
            addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
            addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");

            addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl");
            addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls");
            addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");

            // compute file extensions list for native Media Scanner
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = sFileTypeMap.keySet().iterator();

            while (iterator.hasNext()) {
                if (builder.length() > 0) {
                    builder.append(',');
                }
                builder.append(iterator.next());
            }
            sFileExtensions = builder.toString();
        }

        public static final String UNKNOWN_STRING = "<unknown>";

        public static boolean isAudioFileType(int fileType) {
            return ((fileType >= FIRST_AUDIO_FILE_TYPE &&
                    fileType <= LAST_AUDIO_FILE_TYPE) ||
                    (fileType >= FIRST_MIDI_FILE_TYPE &&
                            fileType <= LAST_MIDI_FILE_TYPE));
        }

        public static boolean isVideoFileType(int fileType) {
            return (fileType >= FIRST_VIDEO_FILE_TYPE &&
                    fileType <= LAST_VIDEO_FILE_TYPE);
        }

        public static boolean isImageFileType(int fileType) {
            return (fileType >= FIRST_IMAGE_FILE_TYPE &&
                    fileType <= LAST_IMAGE_FILE_TYPE);
        }

        public static boolean isPlayListFileType(int fileType) {
            return (fileType >= FIRST_PLAYLIST_FILE_TYPE &&
                    fileType <= LAST_PLAYLIST_FILE_TYPE);
        }

        public static MediaFileType getFileType(String path) {
            int lastDot = path.lastIndexOf(".");
            if (lastDot < 0) {
                return null;
            }
            return sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
        }

        /**
         * 根据视频文件路径判断文件类型
         */
        public static boolean isVideoFileType(String path) {
            MediaFileType type = getFileType(path);
            if (null != type) {
                return isVideoFileType(type.fileType);
            }
            return false;
        }

        //根据音频文件路径判断文件类型
        public static boolean isAudioFileType(String path) {
            MediaFileType type = getFileType(path);
            if (null != type) {
                return isAudioFileType(type.fileType);
            }
            return false;
        }

        //根据mime类型查看文件类型
        public static int getFileTypeForMimeType(String mimeType) {
            Integer value = sMimeTypeMap.get(mimeType);
            return (value == null ? 0 : value.intValue());
        }

        //根据图片文件路径判断文件类型
        public static boolean isImageFileType(String path) {
            MediaFileType type = getFileType(path);
            if (null != type) {
                return isImageFileType(type.fileType);
            }
            return false;
        }
    }
}
