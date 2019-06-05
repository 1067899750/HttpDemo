package com.example.mylibrary;


import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Describe 代理类
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:16
 */
public class HttpHelper implements IHttpProcessor {
    //被代理的对象
    private static IHttpProcessor mIHttpProcessor;

    //需求得到请求参数
    private Map<String, Object> params;
    private static HttpHelper mInatance;

    private HttpHelper() {
        params = new HashMap<>();
    }

    public static HttpHelper getInstance() {
        if (mInatance == null) {
            synchronized (HttpHelper.class) {
                if (mInatance == null) {
                    mInatance = new HttpHelper();
                }
            }

        }
        return mInatance;
    }

    /**
     * 初始化立即启动, 选着不同的框架
     * 传入被代理的对象，Volly实现类 okHttp实现类 等等
     */
    public static void init(IHttpProcessor httpProcessor) {
        mIHttpProcessor = httpProcessor;
    }

    @Override
    public void get(String url, Map<String, Object> params, ICallBack callBack) {
        String finalUrl = appendParams(url, params);
        //实际进行网络请求
        mIHttpProcessor.get(finalUrl, params, callBack);
    }

    @Override
    public void post(String url, Map<String, Object> params, ICallBack callBack) {
        String finalUrl = appendParams(url, params);
        //实际进行网络请求
        mIHttpProcessor.post(finalUrl, params, callBack);
    }


    @Override
    public <K> void setRequestHttp(Class<K> k, String url, Map<String, Object> params, ICallBack callBack, int tag) {
        String finalUrl = appendParams(url, params);
        //实际进行网络请求
        mIHttpProcessor.setRequestHttp(k, finalUrl, params, callBack, tag);
    }

    private String appendParams(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        if (urlBuilder.indexOf("?") <= 0) {
            urlBuilder.append("?");

        } else {
            if (urlBuilder.toString().endsWith("?")) {
                urlBuilder.append("&");
            }
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                urlBuilder.append("&" + entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue() + "", "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return urlBuilder.toString();
    }


}























