package com.besieged.musicpractice.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.besieged.musicpractice.MyApplication;
import com.besieged.musicpractice.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/28.
 * 音乐播放控制类
 */

public class MusicPlayer implements IPlayer, MediaPlayer.OnCompletionListener{

    private static final String TAG = "MusicPlayer";

    private Context mContext;
    private static MusicPlayer sInstance;

    // Player status
    private boolean isPaused;

    //本地广播
    LocalBroadcastManager broadcastManager;
    //播放列表
    List<Song> songList;

    private MediaPlayer mPlayer;

    // Default size 2: for service and UI
    private List<Callback> mCallbacks = new ArrayList<>(2);

    /**
     * 播放模式
     */
    public static final int MUSIC_MODE_LIST_LOOP = 0;
    public static final int MUSIC_MODE_SINGLE_LOOP = 1;
    public static final int MUSIC_MODE_RANDOM_PLAY = 2;


    private int mCurrentMusicIndex = 0;
    private boolean mIsMusicPause = false;
    public int musicPlayMode = MUSIC_MODE_LIST_LOOP;

    public MusicPlayer() {
        this.mContext = MyApplication.getContext();
        mPlayer = new MediaPlayer();
        songList = new ArrayList<>();
        mPlayer.setOnCompletionListener(this);
//        broadcastManager = LocalBroadcastManager.getInstance(mContext);
    }

    public static MusicPlayer getInstance(){
        if (sInstance == null) {
            synchronized (MusicPlayer.class) {
                if (sInstance == null) {
                    sInstance = new MusicPlayer();
                }
            }
        }
        return sInstance;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        if (songList == null){
            songList = new ArrayList<>();
        }
        this.songList = songList;
    }

//    private void optMusic(String action){
//        broadcastManager.sendBroadcast(new Intent(action));
//    }
//    private void optMusic(String action,Intent intent){
//        intent.setAction(action);
//        broadcastManager.sendBroadcast(intent);
//    }
//    private void optMusic(Intent intent){
//        broadcastManager.sendBroadcast(intent);
//    }

    public void playOrPause(){
        if (mPlayer.isPlaying()){
            pause();
        }else {
            play();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Song next = null;
        int index = 0;
        isPaused = false;
        if (musicPlayMode == MUSIC_MODE_SINGLE_LOOP){
            //单曲循环 mCurrentMusicIndex 不变
        }else if (musicPlayMode == MUSIC_MODE_RANDOM_PLAY){
            Random random = new Random();
            mCurrentMusicIndex = random.nextInt(songList.size());
        }else if (musicPlayMode == MUSIC_MODE_LIST_LOOP){
            if (mCurrentMusicIndex + 1 >= songList.size()){
                mCurrentMusicIndex = 0;
            }else {
                mCurrentMusicIndex = mCurrentMusicIndex + 1;
            }
        }
        next = getPlayingSong();
        play();
        notifyComplete(next);
    }

    @Override
    public void setPlayList(List<Song> list) {
        setSongList(list);
    }

    @Override
    public boolean play() {
        // 通过修改mCurrentMusicIndex 来控制播放哪一首
        if (isPaused) {
            mPlayer.start();
            notifyPlayStatusChanged(true);
            return true;
        }
        if (songList.size() > 0){
            Song song = songList.get(mCurrentMusicIndex);
            try {
                mPlayer.reset();
                mPlayer.setDataSource(song.getUrl());
                mPlayer.prepare();
                mPlayer.start();
                notifySongChanged(song);
                notifyPlayStatusChanged(true);
            } catch (IOException e) {
                Log.e(TAG, "play: ", e);
                notifyPlayStatusChanged(false);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean play(List<Song> list) {

        isPaused = false;
        setPlayList(list);
        mCurrentMusicIndex = 0;
        return play();
    }

    @Override
    public boolean play(List<Song> list, int startIndex) {
        isPaused = false;
        mCurrentMusicIndex = startIndex;
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(Song song) {
        isPaused = false;
        songList.removeAll(songList);
        songList.add(song);
        mCurrentMusicIndex = 0;
        return play();
    }

    @Override
    public boolean playLast() {
        isPaused = false;

        mCurrentMusicIndex = getLastIndex();
        Song last = songList.get(mCurrentMusicIndex);
        play();
        notifyPlayLast(last);
        return true;
    }

    private int getLastIndex(){
        int size = getSongList().size();
        int index = mCurrentMusicIndex;
        if (musicPlayMode == MUSIC_MODE_SINGLE_LOOP){

        }else if (musicPlayMode == MUSIC_MODE_RANDOM_PLAY){
            Random random = new Random();
            index = random.nextInt(size);
        }else{
            if (index-1 < 0){
                index = size-1;
            }else{
                index = index-1;
            }
        }
        return index;
    }

    private int getNextIndex(){
        int size = getSongList().size();
        int index = mCurrentMusicIndex;
        if (musicPlayMode == MUSIC_MODE_SINGLE_LOOP){

        }else if (musicPlayMode == MUSIC_MODE_RANDOM_PLAY){
            Random random = new Random();
            index = random.nextInt(size);
        }else{
            if (index+1 > size){
                index = 0;
            }else{
                index = index+1;
            }
        }
        return index;
    }

    @Override
    public boolean playNext() {
        isPaused = false;

        mCurrentMusicIndex = getNextIndex();
        Song next = songList.get(mCurrentMusicIndex);
        play();
        notifyPlayNext(next);
        return true;
    }

    @Override
    public boolean pause() {
        if (mPlayer.isPlaying()){
            mPlayer.pause();
            isPaused = true;
            notifyPlayStatusChanged(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void setPlayMode(int playMode) {
        if (playMode == MUSIC_MODE_SINGLE_LOOP){
            musicPlayMode = MUSIC_MODE_SINGLE_LOOP;
        }else if (playMode == MUSIC_MODE_RANDOM_PLAY){
            musicPlayMode = MUSIC_MODE_RANDOM_PLAY;
        }else{
            musicPlayMode = MUSIC_MODE_LIST_LOOP;
        }
    }

    @Override
    public int getPlayMode() {
        return musicPlayMode;
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (Callback callback : mCallbacks) {
            callback.onPlayStatusChanged(isPlaying);
        }
    }
    private void notifySongChanged(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onSongChangeed(song);
        }
    }

    private void notifyPlayLast(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onSwitchLast(song);
        }
    }

    private void notifyPlayNext(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onSwitchNext(song);
        }
    }

    private void notifyComplete(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onComplete(song);
        }
    }

    @Override
    public void removeCallbacks() {
        mCallbacks.clear();
    }

    @Override
    public void releasePlayer() {
        songList = null;
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        sInstance = null;
    }

    @Override
    public int getProgress() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public Song getPlayingSong() {
        if (songList.size() == 0){
            return null;
        }
        return songList.get(mCurrentMusicIndex);
    }

    @Override
    public int getmCurrentMusicIndex(){
        return mCurrentMusicIndex;
    }

    @Override
    public boolean seekTo(int progress) {
        Song currentSong = songList.get(mCurrentMusicIndex);
        if (currentSong != null) {
            if (currentSong.getDuration() <= progress) {
                onCompletion(mPlayer);
            } else {
                mPlayer.seekTo(progress);
            }
            return true;
        }
        return false;
    }

}
