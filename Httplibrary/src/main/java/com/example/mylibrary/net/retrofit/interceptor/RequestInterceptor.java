package com.example.mylibrary.net.retrofit.interceptor;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    /**
     * 网络请求临时header
     */
    private List<Map<String, Object>> headers = new ArrayList<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl.Builder httpUrlBuilder = chain.request().url()
                .newBuilder();

        //重新构建Request header and params
        Request.Builder builder = chain.request()
                .newBuilder()
                /*重置携带新参数URL*/
                .url(httpUrlBuilder.build());


        //请求header
        for (Map<String, Object> header : this.headers) {
            Iterator<String> headerIterator = header.keySet().iterator();
            while (headerIterator.hasNext()) {
                String key = headerIterator.next();
                builder.addHeader(key,header.get(key).toString());
            }
        }

        Response rp = chain.proceed(builder.build());

        return rp;
    }

    /**
     * 添加header
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        if (key == null)
            return;

        Map<String, Object> map = new HashMap<>();
        map.put(key,value);
        this.headers.add(map);
    }

    /**
     * 添加headers
     * @param headerMap
     */
    public void addHeaders(List<Map<String, Object>> headerMap) {
        if (headerMap == null)
            return;

        this.headers.addAll(headerMap);
    }
}
