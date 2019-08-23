package com.example.mylibrary.net.retrofit.interceptor;

import android.content.SharedPreferences;

import com.example.mylibrary.untils.Utils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @describe
 * @euthor puyantao
 * @email 1067899750@qq.com
 * @create 2019/8/8 14:17
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            //只用ssionId,不用token
            for (String header : originalResponse.headers("Set-Cookie")) {
                if (header.contains("JSESSIONID")){
                    cookies.add(header);
                }
            }

            if (!cookies.isEmpty()) {
                SharedPreferences.Editor config = Utils.getContext().getSharedPreferences("config",
                        Utils.getContext().MODE_PRIVATE).edit();
                config.putStringSet("cookie", cookies);
                config.commit();
            }
        }

        return originalResponse;
    }
}

















