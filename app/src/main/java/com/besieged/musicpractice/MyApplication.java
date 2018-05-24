package com.besieged.musicpractice;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.besieged.musicpractice.utils.LogUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger.addLogAdapter(new AndroidLogAdapter());

        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.i(activity.getClass().getName(),"onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.i(activity.getClass().getName(),"onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.i(activity.getClass().getName(),"onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.i(activity.getClass().getName(),"onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.i(activity.getClass().getName(),"onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.i(activity.getClass().getName(),"onActivityDestroyed");
            }
        });
    }

    /**
     * 获取application实例
     * @return
     */
    public MyApplication getInstance(){
        return instance;
    }

}
