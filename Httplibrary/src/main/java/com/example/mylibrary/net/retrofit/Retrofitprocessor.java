package com.example.mylibrary.net.retrofit;

import android.content.Context;

import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;

import org.xutils.HttpManager;

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


  


}















