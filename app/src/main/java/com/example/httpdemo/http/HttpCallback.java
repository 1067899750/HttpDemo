package com.example.httpdemo.http;

import com.example.httpdemo.http.base.ICallBack;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * Description 利用泛型实现通用的回调接口
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/5/13 14:32
 */

public abstract class HttpCallback<Result> implements ICallBack {

    @Override
    public void onSuccess(String result) {
        Gson gson = new Gson();
        Class<?> cls = analysisClzzInfo(this);

        Result objResult = (Result) gson.fromJson(result, cls);
        onSuccess(objResult);
    }

    public abstract void onSuccess(Result result);

    /**
     * 利用反射获得类的信息
     * @param object
     * @return Class<?> 需要实现的Json解析类
     */
    private Class<?> analysisClzzInfo(Object object) {

        Type genType = getClass().getGenericSuperclass();

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        return (Class<?>) params[0];
    }

}
