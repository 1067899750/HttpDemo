package com.example.httpdemo;

import android.app.Application;
import android.content.Context;

import com.example.httpdemo.helper.PushConstants;
import com.example.httpdemo.untils.HttpUtil;
import com.example.httpdemo.helper.UmPushHelper;
import com.example.mylibrary.net.http.OkHttpProcessor;
import com.example.mylibrary.untils.Utils;
import com.umeng.commonsdk.UMConfigure;


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

        //友盟日志输出开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        UMConfigure.preInit(this, PushConstants.APP_KEY, PushConstants.CHANNEL);
        // todo 添加是否同意隐私政策

        //建议在子线程中初始化
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 初始化友盟
                UmPushHelper.init(getApplicationContext());
            }
        }).start();

    }

    public static Context getContent() {
        return mContext;
    }
}
















