package com.besieged.musicpractice.utils;

import com.besieged.musicpractice.net.LyricCallback;
import com.besieged.musicpractice.net.SearchCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/7/2.
 */

public class NetUtil {

    public static final String CLOUD_MUSIC_API_SEARCH = "http://music.163.com/weapi/cloudsearch/get/web";

    public static final String CLOUD_MUSIC_API_LYRIC = "http://music.163.com/api/song/lyric?os=pc&lv=-1&kv=-1&tv=-1&id=";

    public static void post_searchMusic(String searchContent, String limit, String offset, String type, SearchCallback callback){

        String data = "{\"hlpretag\":\"\",\"hlposttag\":\"\",\"s\":\""+searchContent+"\",\"type\":\""+type+"\",\"offset\":\""+offset+"\",\"total\":\"true\",\"limit\":\""+limit+"\"}";

        Map<String, String> params = EncryptUtils.encrypt(data);

        OkHttpUtils.post().url(CLOUD_MUSIC_API_SEARCH)
                .addHeader("Referer", "http://music.163.com")
                .addHeader("Origin", "http://music.163.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .params(params)
                .build()
                .execute(callback);
    }

    public static void post_getMusicInfo(String searchContent,SearchCallback callback){
        post_searchMusic(searchContent,"1","0","1",callback);
    }

    /**
     * 获取歌词信息
     * @param id 歌曲id
     * @param callback 回调方法
     */
    public static void get_getLyric(String id,LyricCallback callback){
        OkHttpUtils.get().url(CLOUD_MUSIC_API_LYRIC+id)
                .build()
                .execute(callback);
    }

}
