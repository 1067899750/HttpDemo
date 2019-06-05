package com.example.mylibrary.net.retrofit.interceptor;


import java.io.IOException;

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
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method(originalRequest.method(), originalRequest.body());
//        requestBuilder.addHeader("Authorization", "Bearer " + BaseConstant.TOKEN);//添加请求头信息，服务器进行token有效性验证
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}





