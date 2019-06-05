package com.example.mylibrary.net.retrofit.net;

import android.content.Context;
import android.os.Handler;

import com.example.mylibrary.base.ICallBack;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import retrofit2.Retrofit;

/**
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/6/4 14:15
 */
public class ApiMethods {
    //方法名
    private String methodName;
    //网络请求标记
    private int tag;
    //自定义拦截器
    private List<Interceptor> mInterceptors = new ArrayList<>();
    //参数
    private Object[] objParams;
    private ICallBack mCallBack;
    private String url;
    private WeakReference<Context> mContextWeakReference;

    public ApiMethods(Context context) {
        mContextWeakReference = new WeakReference<>(context);
    }

    /**
     * 通过工厂统一创建
     *
     * @return
     */
    public static ApiMethods createMethodFactory(Context context) {
        return new ApiMethods(context);
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
     * 添加参数
     *
     * @param objParams
     * @return
     */
    public ApiMethods setParams(Object... objParams) {
        this.objParams = objParams;
        return this;
    }

    /**
     * 添加网络请求标记
     *
     * @param tag
     * @return
     */
    public ApiMethods setTag(int tag) {
        this.tag = tag;
        return this;
    }


    /**
     * 数据回调
     *
     * @param callBack
     * @return
     */
    public ApiMethods setCallBack(ICallBack callBack) {
        this.mCallBack = callBack;
        return this;
    }

    public ApiMethods setUrl(String url) {
        this.url = url;
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
        Class[] keyObjs = null;
        if (objParams != null) {
            //参数变换
            keyObjs = new Class[objParams.length];
            //获取参数的变量类型，反射机制执行方法体时需要
            for (int i = 0; i < objParams.length; i++) {
                if (objParams[i] == null) {
                    keyObjs[i] = null;
                } else {
                    keyObjs[i] = objParams[i].getClass();
                }
            }
        }
        try {
            BaseHttp baseHttp = new BaseHttp();
            //添加自定义拦截器
//            for (int i = 0; i < mInterceptors.size(); i++) {
//                baseHttp = baseHttp.addInterceptor(mInterceptors.get(i));
//            }
            K k = baseHttp.getRetrofit(cls, url, mContextWeakReference.get());
            //利用反射执行对应方法体
            Class clazz = k.getClass();
            Method[] methods = clazz.getMethods();

            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < methods.length; i++) {
                String name = methods[i].getName();
                strings.add(name);
            }

            methodName = "get" + methodName.substring(0, 1).toUpperCase().concat(methodName.substring(1));
            Method method = clazz.getDeclaredMethod(methodName, keyObjs);
            method.setAccessible(true);
            Observable observable = (Observable) method.invoke(k, objParams);
            //执行订阅
            ApiSubscribe(observable, new MyObserver(mCallBack, tag));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
