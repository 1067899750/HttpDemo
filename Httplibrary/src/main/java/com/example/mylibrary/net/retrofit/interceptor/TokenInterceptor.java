package com.example.mylibrary.net.retrofit.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @describe token 拦截器
 * @euthor puyantao
 * @create 2019/9/3 9:16
 */
public class TokenInterceptor implements Interceptor {
    private static final String USER_TOKEN = "Authorization";
    private final String token;

    public TokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (token == null || originalRequest.header("Authorization") != null){
            return chain.proceed(originalRequest);
        }
        Request request = originalRequest.newBuilder()
                .header(USER_TOKEN, token)
                .build();

        return chain.proceed(request);
    }








}











