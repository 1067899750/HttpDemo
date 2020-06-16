package com.example.mylibrary.net.rxjava;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * @author puyantao
 * @describe
 * @create 2020/6/16 16:43
 */
public interface RxJavaService {


    @GET("/mobile/login.htm")
    Observable<Object> login(@QueryMap HashMap<String, String> params);

    @POST("/mobile/billRecord/billHomePage.htm")
    @FormUrlEncoded
    Observable<Object> billHomePage(@FieldMap HashMap<String, String> params);


}












