package com.besieged.musicpractice.net;

import com.besieged.musicpractice.model.Lrc;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/7/2.
 * 解析歌词
 */

public abstract class LyricCallback extends Callback<Lrc> {
    @Override
    public Lrc parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        Lrc lrc = new Gson().fromJson(string, Lrc.class);
        return lrc;
    }

}
