package com.example.mylibrary.base;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * @Describe 代理接口 代理和真正干活的类  的 共同实现的接口
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:03
 */
public interface IHttpProcessor {


    //Get
    void get(String url, Map<String, String> params, ICallBack callBack);

    //Post
    void post(String url, Map<String, String> params, ICallBack callBack);

}














