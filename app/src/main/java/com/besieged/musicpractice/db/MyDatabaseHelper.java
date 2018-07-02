package com.besieged.musicpractice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.besieged.musicpractice.utils.LogUtils;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/6/28.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public final static String SONG = "song";

    public final static String CREATE_TABLE_SONG_SQL = "CREATE TABLE song (id integer primary key autoincrement,album text, artist text, duration integer, image text, imgbytes blob, musicid integer, cloudmusicid integer, name text, size integer, title text, url text);";


    public static String DATABASE_NAME = "music";
    public static String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/musicPractice/database/"+DATABASE_NAME+".db";
    //version 数据库升级用
    public final static int VERSION = 2;

    private static MyDatabaseHelper sInstance;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDatabaseHelper(Context context) {
        this(context, DATABASE_PATH, null, VERSION);
    }

    public static MyDatabaseHelper getInstance(Context context){
        if(null==sInstance)
            sInstance = new MyDatabaseHelper(context);
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.i("onCreate=version="+db.getVersion());
        safeExecuteSql(db,CREATE_TABLE_SONG_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.i("onUpgrade=version="+oldVersion+","+newVersion);
        if (oldVersion == 1){
            safeExecuteSql(db,"alter table song add COLUMN cloudmusicid integer");
        }

    }

    void safeExecuteSql(SQLiteDatabase db, String sql){
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
