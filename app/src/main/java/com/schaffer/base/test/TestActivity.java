package com.schaffer.base.test;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.schaffer.base.R;
import com.schaffer.base.common.base.BasePagerSingleViewAdapter;
import com.schaffer.base.common.transformer.ViewPagerTransformer;
import com.schaffer.base.common.utils.NetworkUtils;
import com.schaffer.base.db.PersonalSQLiteHelper;
import com.schaffer.base.receiver.DownloadReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author AndroidSchaffer
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        testViewPage();
        setAnimation();
    }

    public void testViewPage() {
        ViewPager pager = (ViewPager) findViewById(R.id.test_vp_page);
        if (pager != null) {
            List<ImageView> views = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ImageView view = new ImageView(this);
                view.setImageResource(R.mipmap.ic_launcher);
                views.add(view);
            }
            pager.setAdapter(new BasePagerSingleViewAdapter(this, null, views));
            pager.setPageTransformer(false, new ViewPagerTransformer());
        }
    }


    public void test() {

        PersonalSQLiteHelper helper = new PersonalSQLiteHelper(this, "db_name", 1);
        SQLiteDatabase db0 = helper.getReadableDatabase();
        List<Pair<String, String>> attachedDbs = db0.getAttachedDbs();
        String path = attachedDbs.get(0).first + attachedDbs.get(0).second;
        /*SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);*/

        SQLiteDatabase db2 = this.openOrCreateDatabase("db_name", MODE_PRIVATE, null, null);
        db2.close();
        String path1 = db2.getPath();
        File file = new File(path1);
        if (file.exists() && file.isFile()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setAnimation() {
        LottieAnimationView viewById = findViewById(R.id.test_lav_anim);
        viewById.setAnimation("data.json");
        viewById.loop(true);
        viewById.playAnimation();
    }

    public long testDownloadManager(final Context context, String url, String childDir,
                                    boolean isPrivate, boolean useMobileNet,
                                    boolean alwaysShowNotification, String fileMimeType,
                                    HashMap<String, String> headValues,
                                    boolean callbackProgress, final Handler handler) {
        if (TextUtils.isEmpty(url) || url.trim().length() <= 0) {
            return -1;
        }
        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
         /* 是否使用私有目录 */
        if (isPrivate) {
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "/" + (TextUtils.isEmpty(childDir) ? "schaffer_downloads" : childDir));
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + (TextUtils.isEmpty(childDir) ? "schaffer_downloads" : childDir));
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
            if (NetworkUtils.isConnected() && useMobileNet) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            } else {
                Toast.makeText(context, "当前无可用网络", Toast.LENGTH_SHORT).show();
                return -1;
            }
        }
        request.setTitle("新的下载任务");
        request.setDescription(url);

        request.setNotificationVisibility(alwaysShowNotification ?
                DownloadManager.Request.VISIBILITY_VISIBLE :
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

        if (!TextUtils.isEmpty(fileMimeType)) {
            request.setMimeType(fileMimeType);
        }

        if (headValues != null && headValues.size() > 0) {
            for (String key : headValues.keySet()) {
                request.addRequestHeader(key, headValues.get(key));
            }
        }
        final long id = dm.enqueue(request);
        TimerTask task = null;
        if (callbackProgress) {
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
                            in.putExtra("uri_id", id);
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
                            b.putInt("progress", pro);
                            b.putString("title", title);
                            msg.setData(b);
                            handler.sendMessage(msg);
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

    public void install(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public long download(final Context context, DownloadFileConfig config, final Handler handler) {
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
            request.setDestinationInExternalFilesDir(this, config.saveDirType, config.saveSubPath);
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
                            in.putExtra("uri_id", id);
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
                            b.putInt("progress", pro);
                            b.putString("title", title);
                            msg.setData(b);
                            handler.sendMessage(msg);
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

        String url;
        boolean useMobileNet;
        boolean alwaysShowNotification;
        boolean isDirPrivate;
        String saveDirType = Environment.DIRECTORY_DOWNLOADS;
        String saveSubPath = "/default_name.default_type";
        String fileMimeType;
        HashMap<String, String> headValues;
        boolean callbackProgress;
        String notificationTitle;
        String notificationDesc;

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
}
