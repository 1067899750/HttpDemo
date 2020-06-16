package com.example.mylibrary.net.rxjava;

import android.content.Context;

import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author puyantao
 * @describe
 * @create 2020/6/16 16:29
 */
public class RxJavaProcessor implements IHttpProcessor {
    private Context mContext;

    public RxJavaProcessor(Context context) {
        mContext = context;
    }

    @Override
    public void get(String url, Map<String, String> params, ICallBack callBack) {
        //检查参数为null
        checkMap(params);
        //获取方法名
        String[] subMethod = url.split("/");
        String methodName = subMethod[subMethod.length - 1];
        if (methodName == null){
            return;
        }
        methodName = methodName.substring(0,methodName.indexOf("."));
        ApiMethods api = ApiMethods.createMethodFactory()
                .setTag(methodName)
                .setMethodName(methodName);//设置方法名
        if (params != null){
            api.setParams(params);
        }
        api.setOnRequestCallBackListener(callBack)
                .doHttp(RxJavaService.class);
    }

    @Override
    public void post(String url, Map<String, String> params, ICallBack callBack) {
        //检查参数为null
        checkMap(params);
        //获取方法名
        String[] subMethod = url.split("/");
        String methodName = subMethod[subMethod.length - 1];
        if (methodName == null){
            return;
        }
        methodName = methodName.substring(0,methodName.indexOf("."));
        ApiMethods api = ApiMethods.createMethodFactory()
                .setTag(methodName)
                .setMethodName(methodName);//设置方法名
        if (params != null){
            api.setParams(params);
        }
        api.setOnRequestCallBackListener(callBack)
                .doHttp(RxJavaService.class);
    }


    /**
     * 检查HashMap存在null的问题
     */
    public static void checkMap(Map<String, String> params){
        if (params == null){
            return;
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
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












