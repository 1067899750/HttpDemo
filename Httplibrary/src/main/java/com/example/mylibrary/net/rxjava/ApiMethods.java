package com.example.mylibrary.net.rxjava;

import android.net.Uri;
import android.text.TextUtils;

import com.example.mylibrary.base.ICallBack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;

/**
 * @author puyantao
 * @description
 * @date 2020/6/17 9:04
 */
public class ApiMethods {
    //方法名
    private String methodName;
    //网络请求标记
    private String tag;
    //自定义拦截器
    private List<Interceptor> mInterceptors = new ArrayList<>();
    //回调
    private ICallBack mICallBack;
    //header
    private List<Map<String, Object>> headers = new ArrayList<>();
    private String url;
    Map<String, String> params;

    /**
     * 通过工厂统一创建
     *
     * @return
     */
    public static ApiMethods createMethodFactory() {
        return new ApiMethods();
    }

    /**
     * 添加方法名
     *
     * @param methodName
     * @return
     */
    public ApiMethods setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    /**
     * 添加网络请求标记
     *
     * @param tag
     * @return
     */
    public ApiMethods setTag(String tag) {
        this.tag = tag;
        return this;
    }


    /**
     * 添加自定义监听器
     *
     * @param interceptor
     * @return
     */
    public ApiMethods addInterceptor(Interceptor interceptor) {
        if (interceptor == null) {
            return this;
        }
        this.mInterceptors.add(interceptor);
        return this;
    }

    /**
     * 添加回调监听
     *
     * @param callBack
     * @return
     */
    public ApiMethods setOnRequestCallBackListener(ICallBack callBack) {
        this.mICallBack = callBack;
        return this;
    }

    public ApiMethods setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加header
     */
    public ApiMethods addHeader(String key, String value) {
        if (key != null) {
            Map<String, Object> map = new HashMap<>();
            map.put(key, value);
            headers.add(map);
        }
        return this;
    }

    /**
     * 添加headers
     */
    public ApiMethods addHeaders(List<Map<String, Object>> headerMap) {
        if (headerMap != null) {
            headers.addAll(headerMap);
        }
        return this;
    }

    public ApiMethods addParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    /**
     * 封装线程管理和订阅的过程
     */
    public void ApiSubscribe(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 执行网络请求
     * 利用泛型+反射机制统一处理网络请求（减少代码处理）
     *
     * @param cls retrfit接口类
     * @param <K>
     */
    public <K> void doHttp(Class<K> cls) {
        try {
            BaseHttp baseHttp = new BaseHttp();
            baseHttp.addHeaders(headers)
                    .addUrl(getBaseUrl(url));
            //添加自定义拦截器
            for (int i = 0; i < mInterceptors.size(); i++) {
                baseHttp = baseHttp.addInterceptor(mInterceptors.get(i));
            }
            K k = baseHttp.getBaseService(cls);
            //利用反射执行对应方法体
            Method method = k.getClass().getMethod(methodName, String.class, Map.class);
            Observable observable = (Observable) method.invoke(k, Uri.parse(url).getPath(), params);
            //执行订阅
            ApiSubscribe(observable, new MyObserver<>(mICallBack, tag));
//            ApiSubscribe(k.executePost(Uri.parse(url).getPath(), params), new MyObserver<>(mICallBack, tag));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param url
     * @return
     */
    public String getBaseUrl(String url) {
        String domain = "";
        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            try {
                String path = Uri.parse(url).getPath();
                String host = Uri.parse(url).getHost();
                domain = url.substring(0, url.indexOf("//")) + "//" + host;
            } catch (Exception ex) {
            }
        }
        return domain;
    }

}
