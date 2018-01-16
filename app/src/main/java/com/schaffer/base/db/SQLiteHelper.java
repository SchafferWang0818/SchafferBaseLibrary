package com.schaffer.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by SchafferWang on 2017/6/26.
 * 复习SQL语句,使用封装好的语句参考: http://blog.csdn.net/linglongxin24/article/details/53316096
 * 类型:NULL、INTEGER、FLOAT、STRING、BLOB
 * 创建表: CREATE TABLE IF NOT EXISTS 表名(列名  列类型(大小)  primary key autoincrement, 列名  列类型(大小)  属性,列名  列类型(大小)  属性 )
 * 删除表:     DROP TABLE IF  EXISTS 表名
 * 插入数据:
 * INSERT INTO 表名 VALUES (值,值,值...)
 * INSERT INTO 表名(列名,列名,列名...) VALUES(值,值,值...)
 * 删除数据:
 * DELETE FROM 表名 WHERE 删除的条件表达式
 * 更新数据:
 * UPDATE 表名 SET 字段名=字段值  WHERE 修改的条件表达式
 * 查询数据:
 * SELECT *            FROM 表名 WHERE 查询的条件表达式  GROUP BY 分组的字段 ORDER BY 排序的字段
 * SELECT 字段名 FROM 表名 WHERE 查询的条件表达式  GROUP BY 分组的字段 ORDER BY 排序的字段
 */

public class SQLiteHelper {


    private static SQLiteHelper instance;
    private final PersonalSQLiteHelper helper;

    public static synchronized SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context);
        }
        return instance;
    }

    private SQLiteHelper(Context context) {
        helper = new PersonalSQLiteHelper(context);
    }

    public void execSQL(String sqlStr) {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (sqlStr != null) {
            db.execSQL(sqlStr);
        }
        db.close();
    }


    public void deleteTable(String tabName) {
        execSQL("DROP TABLE IF  EXISTS " + tabName);

    }

    /**
     * 插入
     * INSERT INTO 表名 VALUES (值,值,值...)
     * INSERT INTO 表名(列名,列名,列名...) VALUES(值,值,值...)
     *
     * @param tabName     表名
     * @param insertValue 值
     */
    public void insertValue(String tabName, String insertValue) {
        if (tabName.isEmpty() || insertValue.isEmpty()) {
            return;
        }
        execSQL("INSERT INTO " + tabName + " VALUES " + insertValue);
    }

    /**
     * DELETE FROM 表名 WHERE 删除的条件表达式
     *
     * @param tabName
     * @param delWhere
     */
    public void deleteValue(String tabName, String delWhere) {
        if (tabName.isEmpty() || delWhere.isEmpty()) {
            return;
        }
        execSQL("DELETE FROM " + tabName + " WHERE " + delWhere);
    }

    /**
     * 更新数据
     *
     * @param tabName
     * @param updateValue
     * @param conditionsWhere
     */
    public void updateValue(String tabName, String updateValue, String conditionsWhere) {
        if (tabName.isEmpty() || updateValue.isEmpty() || conditionsWhere.isEmpty()) {
            return;
        }
        execSQL("UPDATE " + tabName + "SET " + updateValue + "WHERE " + conditionsWhere);
    }

    public SQLiteDatabase getDatabaseFromFile(File file) {
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        return SQLiteDatabase.openOrCreateDatabase(file, null);
    }

    public SQLiteDatabase getDatabaseFromFile(String filePathName) {
        if (filePathName.isEmpty()) {
            return null;
        }
        return getDatabaseFromFile(new File(filePathName));
    }

}
