package com.example.mylibrary.net.retrofit.interceptor;

import android.provider.Settings;

import com.example.mylibrary.net.retrofit.dns.HttpDns;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @describe Okhttp拦截器(修改baseurl)
 * @euthor puyantao
 * @email puyantao@purang.com
 * @create 2019/8/23 14:47
 */
public class AlterHostInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();


        HttpUrl newBaseUrl = HttpUrl.parse(HttpDns.getInstance().getIp());
        //重建新的HttpUrl，修改需要修改的url部分
        HttpUrl newFellUrl = oldHttpUrl
                .newBuilder()
                .scheme(newBaseUrl.scheme())
                .host(newBaseUrl.host())
                .port(newBaseUrl.port())
                .build();

        //重建这个request，通过builder.url(newFullUrl).build()；
        //然后返回一个response至此结束修改
        return chain.proceed(builder.url(newFellUrl).build());
    }
}














