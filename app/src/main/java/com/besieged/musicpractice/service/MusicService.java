package com.besieged.musicpractice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.besieged.musicpractice.model.Song;
import com.besieged.musicpractice.ui.MusicPlayerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_LAST;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_MODE_UPDATE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_NEXT;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_PAUSE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_PLAY;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_SEEK_TO;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_COMPLETE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_DURATION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_PAUSE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_STATUS_MUSIC_PLAY;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_CURRENT_POSITION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_DURATION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_INDEX;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_IS_OVER;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_MODE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_POSITION;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_SEEK_TO;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/22.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    /**
     * 播放模式
     */
    public static final int MUSIC_MODE_LIST_LOOP = 0;
    public static final int MUSIC_MODE_SINGLE_LOOP = 1;
    public static final int MUSIC_MODE_RANDOM_PLAY = 2;


    private int mCurrentMusicIndex = 0;
    private boolean mIsMusicPause = false;
    public int musicPlayMode = MUSIC_MODE_LIST_LOOP;
    private List<Song> mMusicDatas = new ArrayList<>();

    private MusicReceiver mMusicReceiver = new MusicReceiver();
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    /**
     * 广播action 数组，添加action直接在这里写
     */
    private String[] actionArrs = new String[]{
            ACTION_OPT_MUSIC_PLAY,
            ACTION_OPT_MUSIC_PAUSE,
            ACTION_OPT_MUSIC_NEXT,
            ACTION_OPT_MUSIC_LAST,
            ACTION_OPT_MUSIC_SEEK_TO,
            ACTION_OPT_MUSIC_MODE_UPDATE
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBoardCastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMusicDatas(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMusicDatas(Intent intent) {
        if (intent == null) return;
        List<Song> musicDatas = (List<Song>) intent.getSerializableExtra(MusicPlayerActivity.PARAM_MUSIC_LIST);
        int position = intent.getIntExtra(MusicPlayerActivity.PARAM_MUSIC_POSITION,0);

        mCurrentMusicIndex = position;
        mMusicDatas.addAll(musicDatas);
    }

    private void play(final int index) {
        if (index >= mMusicDatas.size()) return;
        if (mCurrentMusicIndex == index && mIsMusicPause) {
            mMediaPlayer.start();
        } else {
            mMediaPlayer.stop();
            mMediaPlayer = null;

            mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(mMusicDatas.get(index).getUrl()));
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(this);
            mCurrentMusicIndex = index;
            mIsMusicPause = false;

            int duration = mMediaPlayer.getDuration();
            sendMusicDurationBroadCast(duration);
        }
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PLAY);
    }

    private void pause() {
        mMediaPlayer.pause();
        mIsMusicPause = true;
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PAUSE);
    }

    private void stop() {
        mMediaPlayer.stop();
    }

    private void next(int index) {
        play(index);
    }
    private void next() {
        int index = MUSIC_MODE_LIST_LOOP;
        if (musicPlayMode == MUSIC_MODE_SINGLE_LOOP){
            index = mCurrentMusicIndex;
        }else if (musicPlayMode == MUSIC_MODE_RANDOM_PLAY){
            Random random = new Random();
            index = random.nextInt(mMusicDatas.size());
        }else if (musicPlayMode == MUSIC_MODE_LIST_LOOP){
            if (mCurrentMusicIndex + 1 >= mMusicDatas.size()){
                index = MUSIC_MODE_LIST_LOOP;
            }else {
                index = mCurrentMusicIndex + 1;
            }
        }
        play(index);
    }

    private void last() {
        int index = MUSIC_MODE_LIST_LOOP;
        if (musicPlayMode == MUSIC_MODE_SINGLE_LOOP){
            index = mCurrentMusicIndex;
        }else if (musicPlayMode == MUSIC_MODE_RANDOM_PLAY){
            Random random = new Random();
            index = random.nextInt(mMusicDatas.size());
        }else if (musicPlayMode == MUSIC_MODE_LIST_LOOP){
            if (mCurrentMusicIndex==0){
                index = mMusicDatas.size()-1;
            }else {
                index = mCurrentMusicIndex - 1;
            }
        }
        play(index);
    }

    private void seekTo(Intent intent) {
        if (mMediaPlayer.isPlaying()) {
            int position = intent.getIntExtra(PARAM_MUSIC_SEEK_TO, 0);
            mMediaPlayer.seekTo(position);
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        sendMusicCompleteBroadCast();
    }
    private void sendMusicCompleteBroadCast() {
        Intent intent = new Intent(ACTION_STATUS_MUSIC_COMPLETE);
        intent.putExtra(PARAM_MUSIC_IS_OVER, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicDurationBroadCast(int duration) {
        Intent intent = new Intent(ACTION_STATUS_MUSIC_DURATION);
        intent.putExtra(PARAM_MUSIC_DURATION, duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicStatusBroadCast(String action) {
        Intent intent = new Intent(action);
        if (action.equals(ACTION_STATUS_MUSIC_PLAY)) {
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION,mMediaPlayer.getCurrentPosition());
            intent.putExtra(PARAM_MUSIC_POSITION,mCurrentMusicIndex);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void initBoardCastReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        //添加action
        for (int i=0; i< actionArrs.length;i++){
            intentFilter.addAction(actionArrs[i]);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver,intentFilter);
    }

    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_OPT_MUSIC_PLAY)) {
                int index = intent.getIntExtra(PARAM_MUSIC_INDEX,-1);
                if (index != -1){
                    play(index);
                }else {
                    play(mCurrentMusicIndex);
                }
            } else if (action.equals(ACTION_OPT_MUSIC_PAUSE)) {
                pause();
            } else if (action.equals(ACTION_OPT_MUSIC_LAST)) {
                last();
            } else if (action.equals(ACTION_OPT_MUSIC_NEXT)) {
                int index = intent.getIntExtra(PARAM_MUSIC_INDEX,-1);
                if (index != -1){
                    next(index);
                }else{
                    next(mCurrentMusicIndex);
                }
            } else if (action.equals(ACTION_OPT_MUSIC_SEEK_TO)) {
                seekTo(intent);
            } else if (action.equals(ACTION_OPT_MUSIC_MODE_UPDATE)){
                musicPlayMode = intent.getIntExtra(PARAM_MUSIC_MODE,0);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
    }
}
