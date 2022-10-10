package com.example.httpdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylibrary.base.ICallBack;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @Describe 基类
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/7/16 9:39
 */
public abstract class BaseActivity extends AppCompatActivity implements ICallBack {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initView();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    protected abstract void initView();
    protected abstract int getLayoutId();





}





















