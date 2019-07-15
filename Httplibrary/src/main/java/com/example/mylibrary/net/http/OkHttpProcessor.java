package com.example.mylibrary.net.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
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

    public OkHttpProcessor() {
        mOkHttpClient = new OkHttpClient();
        myHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void get(String url, Map<String, String> params, final ICallBack callBack) {
        final Request request = new Request.Builder()
                .get()
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
                    Log.d(TAG, "onSuccess result==" + result);
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(0, result);
                        }
                    });

                } else {

                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailed(response.message());
                        }
                    });

                }
            }
        });
    }

    @Override
    public void post(String url, Map<String, String> params, final ICallBack callBack) {
        RequestBody requestbody = appendBody(params);

        final Request request = new Request.Builder()
                .post(requestbody)
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,final IOException e) {
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
                            callBack.onSuccess(0, result);

                        }
                    });
                } else {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailed(response.message().toString());

                        }
                    });

                }
            }
        });


    }


    /**
     * 快速构建参数
     * @param params
     * @return
     */
    private RequestBody appendBody(Map<String, String> params) {
        FormBody.Builder body = new FormBody.Builder();
        if (params == null || params.isEmpty()) {
            return body.build();
        }

        for (Map.Entry<String, String> entry:
             params.entrySet()) {
            body.add(entry.getKey(), entry.getValue().toString());
        }
        return body.build();
    }
}
