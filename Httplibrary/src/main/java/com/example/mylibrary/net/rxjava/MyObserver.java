package com.example.mylibrary.net.rxjava;


import com.example.mylibrary.base.ICallBack;

import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public class MyObserver<T> implements Observer<T> {

    private ICallBack mCallBack;
    private String tag;

    public MyObserver(ICallBack callBack) {
        this.mCallBack = callBack;
    }

    public MyObserver(ICallBack callBack, String tag) {
        this.tag = tag;
        this.mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        if (mCallBack != null) {
            mCallBack.onSuccess(tag,  t.toString());
        }
    }

    @Override
    public void onError(Throwable e) {
        // TODO: 2019/2/12 统一业务逻辑处理
        String err = e.getMessage();
        if (e instanceof HttpException) {

        } else if (e instanceof UnknownHostException) {
            if (mCallBack != null) {
                mCallBack.onError(404, "温馨提示：网络请求失败，请检查您的网络设置");
            }

        } else {
            if (mCallBack != null) {
                mCallBack.onError(BaseHttp.HTTP_REQUEST_ERROR_PROCEL_EXCEPTION, e.getMessage());
            }

        }
    }

    @Override
    public void onComplete() {
        // TODO: 2019/2/12 统一业务逻辑处理
    }
}
