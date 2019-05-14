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
     * T 表示各种类型
     * Object 用着要得到class字节码的时候
     * @param object
     * @return Class<?> 需要实现的Json解析类
     */
    private Class<?> analysisClzzInfo(Object object) {
        //getGenericSuperclass 可以得到原始数据类型， 参数类型， 数据类型， 基本数据
        Type genType = getClass().getGenericSuperclass();
        //获取参数化类型
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }

}











