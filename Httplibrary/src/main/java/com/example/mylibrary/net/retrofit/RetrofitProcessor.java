package com.example.mylibrary.net.retrofit;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.example.mylibrary.base.ICallBack;
import com.example.mylibrary.base.IHttpProcessor;
import com.example.mylibrary.net.retrofit.net.HttpClient;
import com.example.mylibrary.untils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * @Describe
 * @Author puyantao
 * @Email 1067899750@qq.com
 * @create 2019/6/4 13:39
 */
public class RetrofitProcessor implements IHttpProcessor {
    private Context mContext;

    public RetrofitProcessor(Context context) {
        this.mContext = context;
    }

    @Override
    public void get(String url, Map<String, String> params, final ICallBack callBack) {
        HttpClient client = new HttpClient.Builder()
                .baseUrl(getBaseUrl(url))
                .url(Uri.parse(url).getPath())
                .params(params)
                .tag(StringUtils.getUrlTag(url))
                .build();
        client.get(callBack);
    }

    @Override
    public void post(String url, Map<String, String> params, final ICallBack callBack) {
        HttpClient client = new HttpClient.Builder()
                .baseUrl(getBaseUrl(url))
                .url(Uri.parse(url).getPath())
                .params(params)
                .tag(StringUtils.getUrlTag(url))
                .build();
        client.post(callBack);
    }



    /**
     * @param url
     * @return
     */
    public String getBaseUrl(String url) {
        String domain = "";
        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            try {
                String path = Uri.parse(url).getPath();
                String host = Uri.parse(url).getHost();
                domain = url.substring(0, url.indexOf("//")) + "//" + host;
            } catch (Exception ex) {
            }
        }
        return domain;
    }


}















