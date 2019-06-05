package com.example.mylibrary.net.retrofit.net;

import android.content.Context;
import android.text.TextUtils;


import com.example.mylibrary.BuildConfig;
import com.example.mylibrary.net.retrofit.interceptor.CookieReadInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.CookiesSaveInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.GzipRequestInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.HeaderInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.LogInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.RewriteCacheControlInterceptor;
import com.example.mylibrary.net.retrofit.untils.Kits;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/6/4 14:34
 */
public class BaseHttp {
    private Map<String, Retrofit> retrofitMap = new HashMap<>();
    private Map<String, OkHttpClient> clientMap = new HashMap<>();


    //网络请求成功
    public final static int HTTP_REQUEST_SUCCESS = 1000001;
    //网络请求失败
    public final static int HTTP_REQUEST_FAILURE = 1000002;

    //网络请求出错
    public final static int HTTP_REQUEST_ERROR_PROCEL_EXCEPTION = 101;//PROCEL_EXCEPTION图片上传异常
    public final static int HTTP_REQUEST_ERROR_RESULE_DATE_NULL = 102;//返回数据为空


    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 30000;
    public static final int DEFAULT_TIME_OUT = 30000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;
    private Context mContext;

    /**
     * 获取retrofit对象
     *
     * @param kClass
     * @return
     */
    public <K> K getRetrofit(Class<K> kClass, String baseUrl, Context context) {
        mContext = context;

        //创建一个OkHttpClient并设置超时时间
        OkHttpClient okHttpClient = getClient(baseUrl);
        String url = baseUrl.substring(0, baseUrl.indexOf("/", 9) + 1);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(kClass);
    }


    private OkHttpClient getClient(String baseUrl) {
        if (Kits.Empty.check(baseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (clientMap.get(baseUrl) != null) return clientMap.get(baseUrl);

        //缓存
        File cacheFile = new File(mContext.getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        OkHttpClient.Builder builder = new OkHttpClient.Builder();


        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS);//写操作 超时时间
        builder.readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS);//读操作 超时时间
        builder.retryOnConnectionFailure(true);//错误重连


//            builder.addInterceptor(HttpContent.getBaseInterceptor()); //数据返回拦截


//        CookieJar cookieJar = provider.configCookie();
//        if (cookieJar != null) {
//            builder.cookieJar(cookieJar);
//        }

        if (BuildConfig.DEBUG) {
            //信任所有服务器地址
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    //设置为true
                    return true;
                }
            });
        }


        //创建管理器
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            //为OkHttpClient设置sslSocketFactory
            builder.sslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }


        builder.cache(cache);

        builder.addInterceptor(new LogInterceptor());
        builder.addInterceptor(new GzipRequestInterceptor());
        builder.addInterceptor(new HeaderInterceptor()); //请求拦截
        builder.addInterceptor(new RewriteCacheControlInterceptor(mContext));
        builder.addNetworkInterceptor(new RewriteCacheControlInterceptor(mContext));

        //Cookie拦截
        builder.addInterceptor(new CookieReadInterceptor(mContext));
        builder.addInterceptor(new CookiesSaveInterceptor(mContext));

        builder.dns(new Dns() {
            @Override
            public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                return null;
            }
        });

        OkHttpClient client = builder.build();


        clientMap.put(baseUrl, client);
        return client;
    }


}

