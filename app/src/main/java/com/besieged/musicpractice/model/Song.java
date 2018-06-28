package com.besieged.musicpractice.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class Song extends DataSupport implements Serializable{

    private long musicId;

    private String name;//名字

    private String title;//音乐标题

    private String artist;//艺术家

    private String url;//地址

    private long duration;//时长

    private long size;//文件大小

    private String album;//唱片

    private String image;//图片

    private byte[] imgBytes;//图片的bitmap

    public Song() {
    }

    public Song(long musicId, String name, String title, String artist, String url, long duration, long size, String album, String image, byte[] imgBytes) {
        this.musicId = musicId;
        this.name = name;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
        this.size = size;
        this.album = album;
        this.image = image;
        this.imgBytes = imgBytes;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }

    public void setImgBytes(byte[] imgBytes) {
        this.imgBytes = imgBytes;
    }
}
