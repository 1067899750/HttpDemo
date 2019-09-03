package com.example.mylibrary.net.retrofit.interceptor;

import android.util.Log;

import com.example.mylibrary.untils.AuthHelper;
import com.example.mylibrary.untils.Utils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Describe ssionId 或 token 拦截自动登录
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/7/16 11:40
 */
public class LoginInterceptor implements Interceptor {
    private static final String TAG = "---> Http";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Log.d(TAG, "response.code=" + response.code());

        //根据和服务端的约定判断token过期
        if (isTokenExpired(response)) {
            Log.d(TAG, "自动刷新Token,然后重新请求数据");
            //同步请求方式，获取最新的Token
            new AuthHelper().relogin();

            Request newRequest = chain.request()
                    .newBuilder()
                    .header("Cookie", getNewToken())
                    .build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
        if (response.code() == 401) {
            return true;
        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String getNewToken() throws IOException {
        // 通过获取token的接口，同步请求接口
        String newToken = "";
        HashSet<String> preferences = (HashSet) Utils.getContext().getSharedPreferences("config",
                Utils.getContext().MODE_PRIVATE).getStringSet("cookie", null);
        if (preferences != null) {
            for (String cookie : preferences) {
                return cookie;
            }
        }
        return newToken;
    }

}
