package com.example.mylibrary.net.retrofit.interceptor;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Desc:
 * @Author: puyantao
 * @CreateDate: 2022/9/9 10:01
 */
public class ReportInterceptor implements Interceptor {
    public ReportInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Long startTime = SystemClock.uptimeMillis();
        Request request = chain.request();
        HttpUrl url = request.url();

        Response response = chain.proceed(request);
        int code = response.code();
        Long endTime = SystemClock.uptimeMillis();
        Log.i("----->1", endTime - startTime + "");
        return response;
    }
}












