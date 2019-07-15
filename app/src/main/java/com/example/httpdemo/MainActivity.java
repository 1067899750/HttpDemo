package com.example.httpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.httpdemo.bean.Login;
import com.example.httpdemo.bean.RotateBean;
import com.example.httpdemo.untils.HttpUtil;
import com.example.mylibrary.HttpCallback;
import com.example.mylibrary.HttpHelper;
import com.google.gson.Gson;


import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String url = HttpUtil.GET_LOGIN;

    private HashMap<String, String> params = new HashMap<>();


    private String password = "e10adc3949ba59abbe56e057f20f883e";
    private String mobile = "18503970627";
    private String cid = "ba92a56494bdd4160209f96ad9dcea49";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        params.put("password", password);
        params.put("mobile", mobile);
        params.put("cid", cid);

        //测试提交数据
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpHelper.getInstance().get(url, params, new HttpCallback<Login>() {

                    @Override
                    public void onSuccess(Login rotateBean, int tag) {
                        System.out.println("--->" + Thread.currentThread());
                        Gson gson = new Gson();
                        String str = gson.toJson(rotateBean);
                        toast(str);
                    }


                    @Override
                    public void onFailed(String string) {
                        System.out.println("--->" + Thread.currentThread());
                        toast(string);
                    }

                    @Override
                    public void onError(int code, String message) {
                        System.out.println("--->" + Thread.currentThread());
                        toast(message);
                    }
                });





            }
        });



    }


    public void toast(String string) {
        Log.d("---> http : " , string);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


}



























