package com.example.httpdemo.untils;

/**
 *
 * Description 网络请求工具
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/5/13 14:33
 */
public final class HttpUtil {
    private final static boolean isLine = true;

    /**
     * 线下 还是正式
     * @return
     */
    public static String getMainHost() {
        return isLine ? mainHost : testHost;
    }

//    http://www.weather.com.cn/data/sk/101010100.html
//    http://www.weather.com.cn/data/cityinfo/101010100.html
    public static final String mainHost = "http://www.weather.com.cn";
    public static final String testHost = "https://testpurang.purang.com";



    public static final String GET_CITY_INFO = getMainHost() + "/data/cityinfo/101010100.html";

    public static final String GET_LOGIN = getMainHost() + "/mobile/login.htm";



}
