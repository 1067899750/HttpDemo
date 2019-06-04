package com.example.httpdemo;

import android.app.Application;

import com.example.mylibrary.HttpHelper;
import com.example.mylibrary.net.http.OkHttpProcessor;


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
//        HttpHelper.init(new VolleyProcessor(this));
        HttpHelper.init(new OkHttpProcessor());
    }
}
















