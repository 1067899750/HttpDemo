package com.example.mylibrary.net.retrofit.net;

import android.content.Context;
import android.os.Handler;

import com.example.mylibrary.base.ICallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/6/4 14:14
 */
public class HttpManager {

    private static HttpManager instance;
    private Context mContext;

    public static HttpManager getInstance(Context context){
        if (instance == null){
            synchronized (HttpManager.class){
                if (instance == null){
                    instance = new HttpManager(context);
                }
            }
        }
        return instance;
    }

    public HttpManager(Context context) {
        mContext = context;
    }

    /**
     * 网络请求
     * @param url       请求地址
     * @param params    参数
     */
    public  <K> void doHttp(Class<K> k, String url, Map<String,Object> params, ICallBack callBack, int tag){
        if (url == null){
            throw new RuntimeException(" doHttp methodName is not allowed null!!!!!!!!!!!!!!!!");//方法名不能为空，否则抛出异常
        }

        //检查参数为nulll
        checkMap(params);
        //获取方法名
        String[] subMeth = url.split("/");
        String methodName = subMeth[subMeth.length - 1];
        if (methodName == null){
            return;
        }
        methodName = methodName.substring(0,methodName.indexOf("."));
        ApiMethods api = ApiMethods.createMethodFactory(mContext)
                .setMethodName(methodName);//设置方法名
        if (params != null){
            api.setParams(params);
        }
        api.setTag(tag)
                .setUrl(url)
                .setCallBack(callBack)
                .doHttp(k);
    }



    /**
     * 检查HashMap存在null的问题
     */
    private void checkMap(Map<String,Object> params){
        if (params == null){
            return;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String entryKey = entry.getKey();
            if (entryKey == null) {
                throw new IllegalArgumentException("Query map contained null key.");
            }
            Object entryValue = entry.getValue();
            if (entryValue == null) {
                params.put(entryKey,"");//设置默认参数
            }
        }
    }

}




