package com.example.mylibrary.net.retrofit.net;


import com.example.mylibrary.base.ICallBack;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 *
 * Description
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/6/4 16:29
 */

public class MyObserver implements Observer {
    private int tag;
    private ICallBack mCallBack;

    public MyObserver(ICallBack callBack, int tag) {
        this.tag  = tag;
        this.mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Object o) {
        if (mCallBack != null){
            mCallBack.onSuccess(o.toString(), tag);
        }

    }


    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException){
            if (mCallBack != null){
                mCallBack.onFailed(e.toString());
            }


        } else {
            if (mCallBack != null){
                mCallBack.onFailed(e.toString());
            }
        }
    }

    @Override
    public void onComplete() {
        // TODO: 2019/2/12 统一业务逻辑处理
    }
}
