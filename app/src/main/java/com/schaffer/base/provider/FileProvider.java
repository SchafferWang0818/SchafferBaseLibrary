package com.schaffer.base.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by a7352 on 2017/7/27.
 */

public class FileProvider extends ContentProvider {
//    private static final String[] COLUMNS = {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};
//    private static final String META_DATA_FILE_PROVIDER_PATHS = "android.support.FILE_PROVIDER_PATHS";
//
//    //    标签对应
//    private static final String TAG_ROOT_PATH = "root-path";
//    private static final String TAG_FILES_PATH = "files-path";
//    private static final String TAG_CACHE_PATH = "cache-path";
//    private static final String TAG_EXTERNAL = "external-path";
//    private static final String TAG_EXTERNAL_FILES = "external-files-path";
//    private static final String TAG_EXTERNAL_CACHE = "external-cache-path";
//    //  标签内部属性对应
//    private static final String ATTR_NAME = "name";
//    private static final String ATTR_PATH = "path";
//
//    private static final File DEVICE_ROOT = new File("/");

//
//    private static PathStrategy parsePathStrategy(){
//
//        return null;
//    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
