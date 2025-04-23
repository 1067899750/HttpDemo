package com.example.httpdemo;

import android.app.Application;
import android.content.Context;

import com.example.httpdemo.untils.HttpUtil;
import com.example.mylibrary.net.http.OkHttpProcessor;
import com.example.mylibrary.untils.Utils;


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
//        Utils.init(this, new RxJavaProcessor(this), HttpUtil.getMainHost());
//        Utils.init(this, new RetrofitProcessor(this), HttpUtil.getMainHost());
//        Utils.init(this, new VolleyProcessor(this), HttpUtil.getMainHost());
        Utils.init(this, new OkHttpProcessor(this), HttpUtil.getMainHost());

    }

    public static Context getContent() {
        return mContext;
    }
}
















