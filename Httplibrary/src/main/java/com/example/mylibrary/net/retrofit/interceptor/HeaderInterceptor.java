package com.example.mylibrary.net.retrofit.interceptor;


import android.content.Context;
import android.util.Log;

import com.example.mylibrary.untils.AppPageUtil;
import com.example.mylibrary.untils.SharePreferencesUtils;
import com.example.mylibrary.untils.Utils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/6/4 15:55
 */
public class HeaderInterceptor implements Interceptor {
    public HeaderInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HashSet<String> preferences = (HashSet) Utils.getContext().getSharedPreferences("config",
                Context.MODE_PRIVATE).getStringSet("cookie", null);
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Accept-Encoding", "gzip, deflate") //前端对返回数据进行解压，要去这个属性
                .addHeader("Accept", "*/*");
        if (preferences != null) {
            for (String cookie : preferences) {
                requestBuilder.addHeader("Cookie", cookie);
            }
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}





