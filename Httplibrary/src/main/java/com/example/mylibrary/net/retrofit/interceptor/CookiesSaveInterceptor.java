package com.example.mylibrary.net.retrofit.interceptor;


import android.content.Context;

import com.example.mylibrary.untils.SharePreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.prefs.Preferences;

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
            ArrayList<String> cookies = new ArrayList<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            SharePreferencesUtils.setString(mContext, "Cookie", cookies.toString());
        }
        return originalResponse;
    }

}
