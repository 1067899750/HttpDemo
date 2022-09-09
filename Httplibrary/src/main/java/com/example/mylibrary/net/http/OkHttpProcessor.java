package com.example.mylibrary.net.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;
import com.example.mylibrary.net.retrofit.dns.OKHttpDns;
import com.example.mylibrary.net.retrofit.interceptor.AddSsionInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.GzipRequestInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.HttpEventListener;
import com.example.mylibrary.net.retrofit.interceptor.LogInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.ReadCookiesInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.ReportInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.RewriteCacheControlInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.AddTokenInterceptor;
import com.example.mylibrary.untils.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * https://www.cnblogs.com/JamesWang1993/p/8593494.html
 * https://blog.csdn.net/Double2hao/article/details/83744659
 *
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/5/13 14:31
 */

public class OkHttpProcessor implements IHttpProcessor {
    private final String TAG = "OkHttpProcessor";

    private OkHttpClient mOkHttpClient;

    private Handler myHandler;

    public OkHttpProcessor(Context context) {
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AddSsionInterceptor()) //向HTTP中写入ssionId
                .addInterceptor(new ReadCookiesInterceptor()) //从HTTP中读取cookie
                .addInterceptor(new LogInterceptor())
//                .addInterceptor(new LoginInterceptor())
                .addInterceptor(new GzipRequestInterceptor())
                .addInterceptor(new RewriteCacheControlInterceptor(context))
                .addInterceptor(new AddTokenInterceptor()) //向HTTP中写入token
                .addInterceptor(new ReportInterceptor())
                .dns(new OKHttpDns(100))
                .eventListenerFactory(HttpEventListener.FACTORY)
//                .dns(new OkDns())
                .build();
//        mOkHttpClient = new OkHttpClient();
        myHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void get(final String url, Map<String, String> params, final ICallBack callBack) {
        CacheControl cc = new CacheControl.Builder()
                .noCache().build();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .cacheControl(cc)
                .build();
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        Log.d(TAG, "onFailure e ==" + e);
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailed(e.toString());
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response == null) {
                            Log.d(TAG, "onSuccess response== null");
                            return;
                        }
                        Log.d(TAG, "onSuccess response==" + response.toString());
                        if (response.isSuccessful()) {
                            final String result = response.body().string();
                            Log.d(TAG, "onSuccess result==" + result);
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess(StringUtils.getUrlTag(url), result);
                                }
                            });

                        } else {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onError(response.code(), response.message());
                                }
                            });

                        }
                    }
                });
    }

    @Override
    public void post(final String url, Map<String, String> params, final ICallBack callBack) {
        RequestBody requestbody = appendBody(params);
        CacheControl cc = new CacheControl.Builder()
                .noCache().build();
        final Request request = new Request.Builder()
                .post(requestbody)
                .cacheControl(cc)
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d(TAG, "onFailure e ==" + e);
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailed(e.toString());

                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response == null) {
                    Log.d(TAG, "onSuccess response== null");
                    return;
                }
                Log.d(TAG, "onSuccess response==" + response.toString());
                if (response.isSuccessful()) {
                    final String result = response.body().string();

                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(StringUtils.getUrlTag(url), result);
                        }
                    });
                } else {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(response.code(), response.message());

                        }
                    });

                }
            }
        });


    }

    /**
     * 快速构建参数
     *
     * @param params
     * @return
     */
    private RequestBody appendBody(Map<String, String> params) {
        FormBody.Builder body = new FormBody.Builder();
        if (params == null || params.isEmpty()) {
            return body.build();
        }

        for (Map.Entry<String, String> entry :
                params.entrySet()) {
            body.add(entry.getKey(), entry.getValue().toString());
        }
        return body.build();
    }
}
