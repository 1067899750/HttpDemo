package com.example.httpdemo;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.httpdemo.untils.ForwardUtils;
import com.example.httpdemo.untils.MyObject;

import java.io.IOException;
import java.io.InputStream;

public class WebMainActivity extends BaseActivity {
    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_main;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initView() {
        mWebView = findViewById(R.id.web_view);

        mWebView.loadUrl("file:///android_asset/scheme.html");
        WebSettings webSettings = mWebView.getSettings();
        //①设置WebView允许调用js
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        //②将object对象暴露给Js,调用addjavascriptInterface
        mWebView.addJavascriptInterface(new MyObject(this), "myObj");

        mWebView.setWebViewClient(new WebViewClient() {
            // 拦截 appLink 拉起应用
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // 通过  Scheme 拉起应用
                ForwardUtils.goSchemeSkipAppFour(WebMainActivity.this, url);
                return true;
            }
        });
    }

    @Override
    public void onSuccess(String tag, String message) {

    }

    @Override
    public void onFailed(String message) {

    }

    @Override
    public void onError(int code, String message) {

    }

    void goHtmlToWeb() {
        try {
            InputStream inputStream = getAssets().open("scheme");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String xmlString = new String(buffer, "UTF-8"); // 将字节转换为字符串

            // 使用WebView的JavaScript接口显示XML数据
            mWebView.loadUrl("javascript:displayXML('" + Uri.encode(xmlString) + "')");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}