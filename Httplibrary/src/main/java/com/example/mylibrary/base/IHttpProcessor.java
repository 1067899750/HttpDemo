package com.example.mylibrary.base;

import java.util.Map;

/**
 * @Describe  代理接口 代理和真正干活的类  的 共同实现的接口
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:03
 */
public interface IHttpProcessor {


    //Get
    void get(String url, Map<String, Object> params, ICallBack callBack);

    //Post
    void post(String url, Map<String, Object> params, ICallBack callBack);

    //Update

    //Delete

}














