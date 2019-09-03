package com.example.mylibrary.net.retrofit.interceptor;

import android.content.Context;

import com.example.mylibrary.untils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @describe 写入token
 * @euthor puyantao
 * @create 2019/9/3 9:16
 */
public class AddTokenInterceptor implements Interceptor {
    //    private static final String USER_TOKEN = "Authorization";
    private static final String USER_TOKEN = "yyttoken";

    public AddTokenInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = Utils.getContext().getSharedPreferences("config", Context.MODE_PRIVATE)
                .getString("yyttoken", null);
        Request originalRequest = chain.request();
        if (token == null || originalRequest.header(USER_TOKEN) != null) {
            return chain.proceed(originalRequest);
        }
        Request request = originalRequest.newBuilder()
                .header(USER_TOKEN, token)
                .build();

        return chain.proceed(request);
    }


}











