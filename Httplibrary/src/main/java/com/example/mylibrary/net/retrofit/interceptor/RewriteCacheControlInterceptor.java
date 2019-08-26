package com.example.mylibrary.net.retrofit.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.example.mylibrary.untils.Kits;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Describe  保存 cookie
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/6/4 15:48
 */
public class RewriteCacheControlInterceptor implements Interceptor {
    private WeakReference<Context> mReference;
    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;

    public RewriteCacheControlInterceptor(Context context) {
        mReference = new WeakReference<>(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String cacheControl = request.cacheControl().toString();
        if (!Kits.NetWork.isNetConnected(mReference.get())) {
            request = request.newBuilder()
                    .cacheControl(TextUtils.isEmpty(cacheControl) ? CacheControl
                            .FORCE_NETWORK : CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        if (Kits.NetWork.isNetConnected(mReference.get())) {
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" +
                            CACHE_STALE_SEC)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
