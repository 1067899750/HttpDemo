package com.example.mylibrary.net.rxjava;

import android.os.Handler;

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
 * Created by ZhangJie on 2019/2/12.
 */

public class ApiMethods {
    //方法名
    private String methodName;
    //网络请求标记
    private String tag;
    //自定义拦截器
    private List<Interceptor> mInterceptors = new ArrayList<>();
    //参数
    private Object[] objParams;
    //回调
    private ICallBack mICallBack;


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
            for (int i = 0; i < mInterceptors.size(); i++) {
                baseHttp = baseHttp.addInterceptor(mInterceptors.get(i));
            }
            K k = baseHttp.getBaseService(cls);
            //利用反射执行对应方法体
            Method method = k.getClass().getMethod(methodName, keyObjs);
            Observable observable = (Observable) method.invoke(k, objParams);
            //执行订阅
            ApiSubscribe(observable, new MyObserver<>(mICallBack, tag));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
