package com.example.mylibrary.net.rxjava;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.example.mylibrary.BuildConfig;
import com.example.mylibrary.net.retrofit.interceptor.AddSsionInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.ReadCookiesInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.RequestInterceptor;
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


public class BaseHttp {
    //网络请求出错
    public final static int HTTP_REQUEST_ERROR_PROCEL_EXCEPTION = 101;//PROCEL_EXCEPTION图片上传异常

    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 10000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;
    //请求拦截
    private static RequestInterceptor requestInterceptor = new RequestInterceptor();
    private String url;
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
                .addInterceptor(mRewriteCacheControlInterceptor)
                .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(requestInterceptor)
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
                .baseUrl(url)
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

    public BaseHttp addUrl(String url){
        this.url = url;
        return this;
    }

    /**
     * 添加header
     *
     * @param key
     * @param value
     */
    public BaseHttp addHeader(String key, String value) {
        requestInterceptor.addHeader(key, value);
        return this;
    }

    /**
     * 添加headers
     *
     * @param headerMaps
     */
    public BaseHttp addHeaders(List<Map<String, Object>> headerMaps) {
        requestInterceptor.addHeaders(headerMaps);
        return this;
    }
    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 云端响应头拦截器，用来配置缓存策略
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheControl = request.cacheControl().toString();
            if (!isNetConnected(Utils.getContext())) {
                request = request.newBuilder()
                        .cacheControl(TextUtils.isEmpty(cacheControl) ? CacheControl
                                .FORCE_NETWORK : CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (isNetConnected(Utils.getContext())) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" +
                                CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

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

