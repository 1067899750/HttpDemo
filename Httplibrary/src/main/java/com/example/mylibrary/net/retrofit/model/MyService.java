package com.example.mylibrary.net.retrofit.model;


import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/6/3 14:23
 */
public interface MyService {

    @FormUrlEncoded
    @POST("mobile/login.htm")
    Observable<String> getLogin(@FieldMap Map<String, Object> params);


}






