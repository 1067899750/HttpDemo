package com.example.mylibrary.base;

/**
 * @Describe 网络层走的是byte 网络给我们的最终的String
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:06
 */
public interface ICallBack {
    void onSuccess(String string);
    void onFailed(String string);
}
