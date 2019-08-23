package com.example.mylibrary.net.retrofit.net;

import android.text.TextUtils;

import com.example.mylibrary.R;
import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.net.retrofit.interceptor.AddCookiesInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.LogInterceptor;
import com.example.mylibrary.net.retrofit.interceptor.ReceivedCookiesInterceptor;
import com.example.mylibrary.untils.NetworkUtils;
import com.example.mylibrary.untils.StringUtils;
import com.example.mylibrary.untils.ToastUtils;
import com.example.mylibrary.untils.Utils;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <p>类说明</p>
 */
public class HttpClient {

    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 30000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;
    /*The certificate's password*/
    private static final String STORE_PASS = "6666666";
    private static final String STORE_ALIAS = "666666";
    /*用户设置的BASE_URL*/
    private static String BASE_URL = "";
    /*本地使用的baseUrl*/
    private String baseUrl = "";
    private static OkHttpClient okHttpClient;
    private Builder mBuilder;
    private Retrofit retrofit;
    private Call<ResponseBody> mCall;
    private static final Map<String, Call> CALL_MAP = new HashMap<>();
    private String urlTag; //URL 的标志

    /**
     * 获取HttpClient的单例
     *
     * @return HttpClient的唯一对象
     */
    private static HttpClient getIns() {
        return new HttpClient();
    }

    /**
     * 单例模式中的静态内部类写法
     */
    private static class HttpClientHolder {
        private static final HttpClient sInstance = new HttpClient();
    }

    private HttpClient() {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Utils.getContext()));
        ///HttpsUtil.SSLParams sslParams = HttpsUtil.getSslSocketFactory(Utils.getContext(), R.raw.cer,STORE_PASS , STORE_ALIAS);
        //缓存
        File cacheFile = new File(Utils.getContext().getCacheDir(), "cache");
        //100Mb
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        OkHttpClient.Builder builder  = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                //.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                // .hostnameVerifier(HttpsUtil.getHostnameVerifier())
                .addInterceptor(new LogInterceptor())

//                .addInterceptor(new TokenInterceptor())
//                .addInterceptor(new HeaderInterceptor())
//                .cookieJar(new CookieManger(Utils.getContext()))
//                .cookieJar(cookieJar)

                .addInterceptor(new AddCookiesInterceptor()) //这部分
                .addInterceptor(new ReceivedCookiesInterceptor()) //这部分
                .cache(cache);




        okHttpClient = builder.build();
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    private void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * 获取的Retrofit的实例，
     * 引起Retrofit变化的因素只有静态变量BASE_URL的改变。
     */
    private void getRetrofit() {
        if (!BASE_URL.equals(baseUrl) || retrofit == null) {
            baseUrl = BASE_URL;
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .build();
        }
    }

    /**
     * POST请求欧
     *
     * @param iCallBack
     */
    public void post(final ICallBack iCallBack) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class)
                .executePost(builder.url, builder.params);
        putCall(builder, mCall);
        request(builder, iCallBack);
    }


    /**
     * Git请求欧
     *
     * @param iCallBack
     */
    public void get(final ICallBack iCallBack) {
        Builder builder = mBuilder;
        if (!builder.params.isEmpty()) {
            String value = "";
            for (Map.Entry<String, String> entry : builder.params.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
                String span = value.equals("") ? "" : "&";
                String part = StringUtils.buffer(span, mapKey, "=", mapValue);
                value = StringUtils.buffer(value, part);
            }
            builder.url(StringUtils.buffer(builder.url, "?", value));
        }
        mCall = retrofit.create(ApiService.class).executeGet(builder.url);
        putCall(builder, mCall);
        request(builder, iCallBack);
    }


    private void request(final Builder builder, final ICallBack iCallBack) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showLongToastSafe(R.string.current_internet_invalid);
            iCallBack.onFailed(Utils.getString(R.string.current_internet_invalid));
            return;
        }
        mCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (200 == response.code()) {
                    try {
                        String result = response.body().string();
                        iCallBack.onSuccess(urlTag, result);
                    } catch (IOException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                if (!response.isSuccessful() || 200 != response.code()) {
                    iCallBack.onError(response.code(), response.message());
                }
                if (null != builder.tag) {
                    removeCall(builder.url);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                iCallBack.onFailed(t.getMessage());
                if (null != builder.tag) {
                    removeCall(builder.url);
                }
            }

        });
    }


    /**
     * 添加某个请求
     */
    private synchronized void putCall(Builder builder, Call call) {
        if (builder.tag == null) {
            return;
        }
        synchronized (CALL_MAP) {
            CALL_MAP.put(builder.tag.toString() + builder.url, call);
        }
    }


    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求;
     * 如果要取消某个tag单独请求，tag需要传入tag+url
     *
     * @param tag 请求标签
     */
    public synchronized void cancel(Object tag) {
        if (tag == null) {
            return;
        }
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }

    }

    /**
     * 移除某个请求
     *
     * @param url 添加的url
     */
    private synchronized void removeCall(String url) {
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }

    /**
     * Build a new HttpClient.
     * url is required before calling. All other methods are optional.
     */
    public static final class Builder {
        private String builderBaseUrl = "";
        private String url;
        private String tag;
        private Map<String, String> params = new HashMap<>();

        public Builder() {
        }

        /**
         * 请求地址的baseUrl，最后会被赋值给HttpClient的静态变量BASE_URL；
         *
         * @param baseUrl 请求地址的baseUrl
         */
        public Builder baseUrl(String baseUrl) {
            this.builderBaseUrl = baseUrl;
            return this;
        }

        /**
         * 除baseUrl以外的部分，
         * 例如："mobile/login"
         *
         * @param url path路径
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * 给当前网络请求添加标签，用于取消这个网络请求
         *
         * @param tag 标签
         */
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param params 请求参数
         */
        public Builder params(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }


        public HttpClient build() {
            if (!TextUtils.isEmpty(builderBaseUrl)) {
                BASE_URL = builderBaseUrl;
            }
            HttpClient client = HttpClient.getIns();
            client.getRetrofit();
            client.setBuilder(this);
            client.urlTag = (String) tag;
            return client;
        }
    }


}
