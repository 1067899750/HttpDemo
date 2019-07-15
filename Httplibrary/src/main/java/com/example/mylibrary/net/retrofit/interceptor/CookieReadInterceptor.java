package com.example.mylibrary.net.retrofit.interceptor;


import android.content.Context;

import com.example.mylibrary.untils.SharePreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: Allen.
 * @date: 2018/7/27
 * @description: 读取cookie
 */

public class CookieReadInterceptor implements Interceptor {
    private Context mContext;

    public CookieReadInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Cookie", SharePreferencesUtils.getString(mContext, "cookiess", ""));
        return chain.proceed(builder.build());
    }
}
