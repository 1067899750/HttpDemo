package com.example.mylibrary.net.retrofit;

import android.content.Context;

import com.example.mylibrary.HttpHelper;
import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;
import com.example.mylibrary.net.retrofit.net.HttpManager;

import java.util.Map;

/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/6/4 13:39
 */
public class Retrofitprocessor implements IHttpProcessor {
    private Context mContext;

    public Retrofitprocessor(Context context) {
        this.mContext = context;
    }

    @Override
    public void get(String url, Map<String, Object> params, ICallBack callBack) {

    }

    @Override
    public void post(String url, Map<String, Object> params, ICallBack callBack) {

    }


    @Override
    public <K> void setRequestHttp(Class<K> k, String url, Map<String, Object> params, ICallBack callBack, int tag) {
        HttpManager.getInstance(mContext).doHttp(k, url, params, callBack, tag);
    }


}















