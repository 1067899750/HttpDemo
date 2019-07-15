package com.example.httpdemo;

import android.app.Application;
import android.content.Context;

import com.example.mylibrary.HttpHelper;
import com.example.mylibrary.net.http.OkHttpProcessor;
import com.example.mylibrary.net.retrofit.Retrofitprocessor;
import com.example.mylibrary.net.volley.VolleyProcessor;


/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:22
 */
public class MyApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
//        HttpHelper.init(new VolleyProcessor(this));
//        HttpHelper.init(new OkHttpProcessor());
        HttpHelper.init(new Retrofitprocessor(this));
    }

    public static Context getContent() {
        return mContext;
    }
}
















