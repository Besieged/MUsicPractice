package com.besieged.musicpractice.net;

import com.besieged.musicpractice.model.SearchResponse;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/7/2.
 */

public abstract class SearchCallback extends Callback<SearchResponse> {
    @Override
    public SearchResponse parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        SearchResponse resp = new Gson().fromJson(string, SearchResponse.class);
        return resp;
    }
}
