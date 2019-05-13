package com.example.httpdemo.http;

import com.example.httpdemo.http.base.ICallBack;
import com.example.httpdemo.http.base.IHttpProcessor;

import java.util.Map;

/**
 * @Describe 代理类
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:16
 */
public class HttpHelper implements IHttpProcessor {
    /**
     * 被代理的对象
     */
    private static IHttpProcessor mIHttpProcessor;

    private static HttpHelper instance;

    private HttpHelper(){

    }

    public static HttpHelper getInstance() {
        if (instance == null) {
            instance = new HttpHelper();
        }
        return instance;
    }

    /**
     * 初始化立即启动
     * 传入被代理的对象，Volly实现类 okHttp实现类 等等
     *
     */
    public static void init(IHttpProcessor httpProcessor){
        mIHttpProcessor = httpProcessor;
    }

    @Override
    public void get(String url, Map<String, Object> params, ICallBack callBack) {
        mIHttpProcessor.get(url, params, callBack);
    }

    @Override
    public void post(String url, Map<String, Object> params, ICallBack callBack) {
        mIHttpProcessor.post(url, params, callBack);
    }
}























