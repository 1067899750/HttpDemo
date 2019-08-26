package com.example.mylibrary.net.retrofit.dns;

/**
 * @describe IP基类
 * @euthor puyantao
 * @email puyantao@purang.com
 * @create 2019/8/26 11:33
 */
public class HttpDns {
    private static HttpDns mInstance = null;
    private static String baseUrl;

    public static HttpDns getInstance() {
        if (mInstance == null) {
            synchronized (HttpDns.class) {
                if (mInstance == null) {
                    mInstance = new HttpDns();
                }
            }
        }
        return mInstance;
    }

    public static void init(String url) {
        baseUrl = url;
    }


    public static String getIp() {
        return baseUrl;
    }
}













