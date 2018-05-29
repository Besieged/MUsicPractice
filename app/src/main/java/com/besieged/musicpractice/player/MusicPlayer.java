package com.besieged.musicpractice.player;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_MODE_UPDATE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_PAUSE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_PLAY;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.ACTION_OPT_MUSIC_SEEK_TO;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_INDEX;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_MODE;
import static com.besieged.musicpractice.player.PlayerMSG.ACTION_MSG.PARAM_MUSIC_SEEK_TO;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/28.
 * 音乐播放控制类
 */

public class MusicPlayer {

    private Context mContext;
    private static MusicPlayer sInstance;
    private boolean isPlaying = false;
    LocalBroadcastManager broadcastManager;

    public MusicPlayer(Context mContext) {
        this.mContext = mContext;
        broadcastManager = LocalBroadcastManager.getInstance(mContext);
    }

    public static MusicPlayer getInstance(Context context){
        if (sInstance == null) {
            synchronized (MusicPlayer.class) {
                if (sInstance == null) {
                    sInstance = new MusicPlayer(context);
                }
            }
        }
        return sInstance;
    }


    public void playOrPause(){
        if (isPlaying()){
            pause();
        }else {
            play();
        }
    }

    public void play(){
        isPlaying = true;
        Intent i = new Intent(ACTION_OPT_MUSIC_PLAY);
        optMusic(i);
    }

    public void play(int index){
        isPlaying = true;
        Intent i = new Intent(ACTION_OPT_MUSIC_PLAY);
        i.putExtra(PARAM_MUSIC_INDEX,index);
        optMusic(i);
    }

    public void seekTo(int position){
        Intent intent = new Intent(ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(PARAM_MUSIC_SEEK_TO, position);
        broadcastManager.sendBroadcast(intent);
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void pause(){
        isPlaying = false;
        Intent intent = new Intent(ACTION_OPT_MUSIC_PAUSE);
        optMusic(intent);
    }

    public void stop(){
        isPlaying = false;
        Intent intent = new Intent(ACTION_OPT_MUSIC_PAUSE);
        optMusic(intent);
    }

    public void changeMode(int index){
        Intent intent = new Intent(ACTION_OPT_MUSIC_MODE_UPDATE);
        intent.putExtra(PARAM_MUSIC_MODE, index);
        optMusic(intent);
    }


    private void optMusic(String action){
        broadcastManager.sendBroadcast(new Intent(action));
    }
    private void optMusic(String action,Intent intent){
        intent.setAction(action);
        broadcastManager.sendBroadcast(intent);
    }
    private void optMusic(Intent intent){
        broadcastManager.sendBroadcast(intent);
    }

}
