package com.besieged.musicpractice.utils;

import com.orhanobut.logger.Logger;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class LogUtils {
    public static final String TAG = "MusicPractice";
    /**
     * 是否开启debug
     * 注意：使用Eclipse打包的时候记得取消Build Automatically，否则一直是true
     */
    public static boolean isDebug= true;

    /**
     * 错误
     */
    public static void e(String tag,String msg){
        if(isDebug){
            Logger.t(tag).e(msg+"");
        }
    }
    public static void e(String msg){
        if(isDebug){
            Logger.t(TAG).e(msg+"");
        }
    }
    /**
     * 调试
     */
    public static void d(String tag,String msg){
        if(isDebug){
            Logger.t(tag).d( msg+"");
        }
    }
    public static void d(String msg){
        if(isDebug){
            Logger.t(TAG).d( msg+"");
        }
    }
    /**
     * 信息
     */
    public static void i(String tag,String msg){
        if(isDebug){
            Logger.t(tag).i( msg+"");
        }
    }
    public static void i(String msg){
        if(isDebug){
            Logger.t(TAG).i( msg+"");
        }
    }

}
