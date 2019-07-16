package com.example.httpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.httpdemo.bean.Login;
import com.example.httpdemo.untils.HttpUtil;
import com.example.mylibrary.HttpCallback;
import com.example.mylibrary.HttpHelper;
import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.untils.SharePreferencesUtils;
import com.example.mylibrary.untils.Utils;
import com.google.gson.Gson;


import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ICallBack {
    private String url = HttpUtil.GET_LOGIN;
    private String url2 = HttpUtil.GIT_USER_MERCHANT;


    private String password = "e10adc3949ba59abbe56e057f20f883e";
    private String mobile = "18503970627";
    private String cid = "ba92a56494bdd4160209f96ad9dcea49";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //测试提交数据
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.other_btn).setOnClickListener(this);

//        HttpHelper.getInstance().get(url, params, new HttpCallback<Login>() {
//            @Override
//            public void onFailed(String message) {
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//
//            }
//
//            @Override
//            public void onSuccess(Login login, String tag) {
//
//            }
//        });


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                HashMap<String, String> loginMap = new HashMap<>();
                loginMap.put("password", password);
                loginMap.put("mobile", mobile);
                loginMap.put("cid", cid);
                HttpHelper.getInstance().get(url, loginMap, this);

                break;
            case R.id.other_btn:
                HashMap<String, String> otherMap = new HashMap<>();
                HttpHelper.getInstance().get(url2, otherMap, this);

                break;
        }
    }


    @Override
    public void onSuccess(String tag, String message) {
        if (tag.equals("login")) {
            System.out.println("--->" + Thread.currentThread());
            toast(message);
        } else if (tag.equals("getIncomeReport")) {
            System.out.println("--->" + Thread.currentThread());
            toast(message);
        }
    }

    @Override
    public void onFailed(String message) {
        System.out.println("--->" + Thread.currentThread());
        toast(message);
    }

    @Override
    public void onError(int code, String message) {
        System.out.println("--->" + Thread.currentThread());
        toast(code + ":" + message);
    }


    public void toast(String string) {
        String cooker = SharePreferencesUtils.getString(Utils.getContext(), "CookiePersistence", "");
        Log.d("---> http : ", string);
        ((TextView) findViewById(R.id.text_tv)).setText(string);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }




}



























