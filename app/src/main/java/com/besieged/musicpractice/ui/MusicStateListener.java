package com.besieged.musicpractice.ui;

/**
 * Created by wm on 2016/12/23.
 */
public interface MusicStateListener {

    /**
     * 更新歌曲状态信息
     */
     void updateTrackInfo();

     void updateTime();

     void reloadAdapter();
}
