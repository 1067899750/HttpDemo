package com.example.httpdemo.mfr;

import android.content.Context;

import org.android.agoo.mezu.MeizuPushReceiver;

/**
 * 厂商通道：魅族
 * 消息广播接收者类
 */
public class MfrMzMessageReceiver extends MeizuPushReceiver {
    @Override
    public void onMessage(Context context, String s) {
        super.onMessage(context, s);
    }
}
