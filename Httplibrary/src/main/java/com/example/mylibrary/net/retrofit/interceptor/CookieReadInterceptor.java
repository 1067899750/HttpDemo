package com.example.mylibrary.net.retrofit.interceptor;


import android.content.Context;
import android.util.Log;

import com.example.mylibrary.untils.SharePreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.Preferences;

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

        String string = SharePreferencesUtils.getString(mContext, "Cookie", "");
        List<String> cookies = Arrays.asList(string);
        for (String cookie : cookies) {
            builder.addHeader("Cookie", cookie);
            // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
            Log.v("OkHttp", "Adding Header: " + cookie);
        }

//        String Cookie = SharePreferencesUtils.getString(mContext, "Cookie", "");
//        builder.addHeader("Cookie",Cookie);
        return chain.proceed(builder.build());
    }
}
