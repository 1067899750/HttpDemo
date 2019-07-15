package com.example.mylibrary.base;

/**
 * @Describe 网络层走的是byte 网络给我们的最终的String
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/5/13 14:06
 */
public interface ICallBack{
    /**
     * 请求成功的情况
     *
     * @param tag     请求标志
     * @param message 成功信息
     */
    void onSuccess(String tag, String message);

    /**
     * 请求失败的情况
     *
     * @param message 失败信息
     */
    void onFailed(String message);

    /**
     * 响应成功，但是出错的情况
     *
     * @param code    错误码
     * @param message 错误信息
     */
    void onError(int code, String message);
}
