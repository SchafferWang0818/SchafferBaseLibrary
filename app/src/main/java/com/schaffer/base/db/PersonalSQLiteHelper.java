package com.schaffer.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by a7352 on 2017/5/14.操作
 */

class PersonalSQLiteHelper extends SQLiteOpenHelper {


    public PersonalSQLiteHelper(Context context) {
        this(context, context.getPackageName(), 1);
    }

    public PersonalSQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version < 1 ? 1 : version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS init(_id  Integer  PRIMARY KEY AUTOINCREMENT, name  varchar not null)");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("PersonalSQLiteHelper", "oldVersion = " + oldVersion + ",newVersion = " + newVersion);
    }


}
