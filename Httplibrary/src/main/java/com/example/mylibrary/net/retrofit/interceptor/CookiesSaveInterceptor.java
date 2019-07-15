package com.example.mylibrary.net.retrofit.interceptor;


import android.content.Context;

import com.example.mylibrary.untils.SharePreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description:
 */

public class CookiesSaveInterceptor implements Interceptor {
    private Context mContext;

    public CookiesSaveInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            String header = originalResponse.header("Set-Cookie");
            String[] strAll = header.split(";");
            String cooker = strAll[0].substring(strAll[0].indexOf("="));
            SharePreferencesUtils.setString(mContext, "cookiess", cooker);
        }
        return originalResponse;
    }

}
