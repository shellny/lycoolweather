package com.ly.coolweather.util;

import com.lzy.okgo.OkGo;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by liying on 2017/10/31.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }



}
