package com.example.httpdemo;

import android.app.Application;

import com.example.httpdemo.http.HttpHelper;
import com.example.httpdemo.http.net.OkHttpProcessor;
import com.example.httpdemo.http.net.VolleyProcessor;

/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:22
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpHelper.init(new VolleyProcessor(this));
//        HttpHelper.init(new OkHttpProcessor());
    }
}
















