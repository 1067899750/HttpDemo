package com.example.httpdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.httpdemo.untils.HttpUtil;
import com.example.mylibrary.HttpHelper;
import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.untils.SharePreferencesUtils;
import com.example.mylibrary.untils.Utils;
import com.franmontiel.persistentcookiejar.persistence.SerializableCookie;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Cookie;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String url = HttpUtil.GET_LOGIN1;
    private String url2 = HttpUtil.GIT_USER_MERCHANT;


    private String password = "e10adc3949ba59abbe56e057f20f883e";
    private String mobile = "18503970627";
    private String cid = "09164b5a280eda07839aabbd9e3c5961";


//    private String password = "e10adc3949ba59abbe56e057f20f883e";
//    private String mobile = "18503970627";
//    private String cid = "591b8d3c615160c2890bdb1e8fa4415d";

    @Override
    protected void initView() {

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
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                HashMap<String, String> loginMap = new HashMap<>();
                loginMap.put("password", password);
                loginMap.put("mobile", mobile);
                loginMap.put("cid", cid);
                loginMap.put("loginType", "1");
                HttpHelper.getInstance().get(url, loginMap, this);
                break;

            case R.id.other_btn:
                HashMap<String, String> otherMap = new HashMap<>();
                HttpHelper.getInstance().post(url2, otherMap, this);
                break;

        }
    }

    @Override
    public void onSuccess(String tag, String message) {
        if (tag.equals("login")) {
            System.out.println("--->" + Thread.currentThread());
            toast(message);
        } else if (tag.equals("billHomePage")) {
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
        ArrayList<String> cookies = new ArrayList<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            cookies.add(new SerializableCookie().decode((String) map.get(key)).value());
        }
        ((TextView) findViewById(R.id.text_tv)).setText(string);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


}



























