package com.besieged.musicpractice.player;

import android.support.annotation.Nullable;

import com.besieged.musicpractice.model.Song;

import java.util.List;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/30.
 */

public interface IPlayer {
    void setPlayList(List<Song> list);

    boolean play();

    boolean play(List<Song> list);

    boolean play(List<Song> list, int startIndex);

    boolean play(Song song);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPlaying();

    int getProgress();

    Song getPlayingSong();

    int getmCurrentMusicIndex();

    boolean seekTo(int progress);

    void setPlayMode(int playMode);

    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback {

        void onSwitchLast(@Nullable Song last);

        void onSwitchNext(@Nullable Song next);

        void onComplete(@Nullable Song next);

        void onPlayStatusChanged(boolean isPlaying);
    }
}
