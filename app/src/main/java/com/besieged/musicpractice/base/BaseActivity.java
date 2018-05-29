package com.besieged.musicpractice.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.besieged.musicpractice.ui.MusicStateListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_COMPLETE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_DURATION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_PAUSE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_PLAY;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_CURRENT_POSITION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_DURATION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_IS_OVER;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class BaseActivity extends AppCompatActivity {

    public Context mContext;
    private ArrayList<MusicStateListener> mMusicListener = new ArrayList<>();
//    private QuickControlsFragment fragment; //底部播放控制栏

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        makeStatusBarTransparent();
        AppManager.getInstance().addActivity(this); //添加到栈中
//        showQuickControl(true);
    }
    /**
     * @param show 显示或关闭底部播放控制栏
     */
//    protected void showQuickControl(boolean show) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        if (show) {
//            if (fragment == null) {
//                fragment = QuickControlsFragment.newInstance();
//                ft.add(R.id.bottom_container, fragment).commitAllowingStateLoss();
//            } else {
//                ft.show(fragment).commitAllowingStateLoss();
//            }
//        } else {
//            if (fragment != null)
//                ft.hide(fragment).commitAllowingStateLoss();
//        }
//    }

    /**
     * 更新播放队列
     */
    public void updateQueue() {

    }

    /**
     * 更新歌曲状态信息
     */
    public void updateTrackInfo() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.reloadAdapter();
                listener.updateTrackInfo();
            }
        }
    }

    /**
     *  fragment界面刷新
     */
    public void refreshUI() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.reloadAdapter();
            }
        }

    }

    public void updateTime() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.updateTime();
            }
        }
    }

    /**
     *  歌曲切换
     */
    public void updateTrack() {

    }



    public void updateLrc() {

    }

    /**
     * @param p 更新歌曲缓冲进度值，p取值从0~100
     */
    public void updateBuffer(int p) {

    }

    public void changeTheme() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.changeTheme();
            }
        }
    }

    /**
     * @param l 歌曲是否加载中
     */
    public void loading(boolean l){

    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicListener.remove(status);
        }
    }
    class MusicReceiver extends BroadcastReceiver {
        private WeakReference<BaseActivity> mReference;

        public MusicReceiver(BaseActivity activity) {
            this.mReference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();
            if (baseActivity!=null){
                if (action.equals(ACTION_STATUS_MUSIC_PLAY)) {
                    int currentPosition = intent.getIntExtra(PARAM_MUSIC_CURRENT_POSITION, 0);

                } else if (action.equals(ACTION_STATUS_MUSIC_PAUSE)) {
                } else if (action.equals(ACTION_STATUS_MUSIC_DURATION)) {
                    int duration = intent.getIntExtra(PARAM_MUSIC_DURATION, 0);
//                    updateMusicDurationInfo(duration);
                } else if (action.equals(ACTION_STATUS_MUSIC_COMPLETE)) {
                    boolean isOver = intent.getBooleanExtra(PARAM_MUSIC_IS_OVER, true);
//                    complete(isOver);
                }
            }
        }
    }

    /*设置透明状态栏*/
    public void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this); //从栈中移除
    }
}
