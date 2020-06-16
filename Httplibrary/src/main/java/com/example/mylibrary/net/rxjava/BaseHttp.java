package com.example.mylibrary.net.rxjava;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.example.mylibrary.BuildConfig;
import com.example.mylibrary.net.retrofit.interceptor.AddSsionInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.ReadCookiesInterceptor;
import com.example.mylibrary.untils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ZhangJie on 2019/2/12.
 */
public class BaseHttp {
    //网络请求出错
    public final static int HTTP_REQUEST_ERROR_PROCEL_EXCEPTION = 101;//PROCEL_EXCEPTION图片上传异常

    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 10000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;

    /**
     * 获取retrofit对象
     *
     * @param k
     * @return
     */
    public <K> K getBaseService(Class<K> k) {
        //缓存
        File cacheFile = new File(Utils.getContext().getCacheDir(), "cache");
        //100Mb
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        //创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.addInterceptor(new ReadCookiesInterceptor())
                .addInterceptor(new AddSsionInterceptor())
                .cache(cache);
        if (BuildConfig.DEBUG) {
            //信任所有M服务器地址
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    //设置为true
                    return true;
                }
            });
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
        }

        //添加自定义拦截器
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            builder.addInterceptor(interceptors.get(i));
        }

        //创建retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl("https://yanyangtian.purang.com")
                //请求的结果转为实体类
                .addConverterFactory(GsonConverterFactory.create())
                //适配RxJava2.0,RxJava1.x则为RxJavaCallAdapterFactory.create()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(k);
    }


    /**
     * 自定义拦截器
     */
    private List<Interceptor> interceptors = new ArrayList<>();

    public BaseHttp addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
        return this;
    }


    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}

