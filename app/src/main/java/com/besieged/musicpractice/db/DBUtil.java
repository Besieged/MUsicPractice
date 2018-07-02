package com.besieged.musicpractice.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.besieged.musicpractice.MyApplication;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/6/28.
 */

public class DBUtil {

    public static SQLiteDatabase getDatabase(){
        MyDatabaseHelper helper = new MyDatabaseHelper(MyApplication.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        return db;
    }

    //返回-1，key不存在，返回-2，数据库更新失败
    public static long insert(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        while(db.isDbLockedByOtherThreads())
            ;
        long ret = db.insert(table, nullColumnHack, values);
        return ret;
    }

    public static Cursor queryAll(SQLiteDatabase db,String table){
        while(db.isDbLockedByOtherThreads())
            ;
        Cursor cur = query(db,table, null, null, null, null, null, "id ASC");
        return cur;
    }

    public static Cursor query(SQLiteDatabase db,String table,String[] columns,String selection,String[] selectionArgs, String groupBy, String having, String orderBy){
        while(db.isDbLockedByOtherThreads())
            ;
        Cursor cur = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cur;
    }
    public static Cursor query(SQLiteDatabase db, String table, String[] columns, String selection,
                               String[] selectionArgs, String groupBy, String having,
                               String orderBy, String limit) {
        while(db.isDbLockedByOtherThreads())
            ;
        Cursor cur = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return cur;
    }

    //返回其他值数据库更新失败
    public static final synchronized int update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs){
        while(db.isDbLockedByOtherThreads())
            ;
        int ret = db.update(table, values, whereClause, whereArgs);
        return ret;
    }

}
