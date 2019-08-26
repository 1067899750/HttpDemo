package com.example.mylibrary.untils;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.franmontiel.persistentcookiejar.cache.SetCookieCache;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Token过期帮助类
 * 执行登录，刷新Token
 */
public class AuthHelper {

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private String tokenflush() {
        String password = "e10adc3949ba59abbe56e057f20f883e";
        String mobile = "18503970627";
        String cid = "ba92a56494bdd4160209f96ad9dcea49";
        StringBuffer sbRequest = new StringBuffer();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("password", password);
        if (map != null && map.size() > 0) {
            for (String key : map.keySet()) {
                sbRequest.append(key + "=" + map.get(key) + "&");
            }
        }
        String request = sbRequest.substring(0, sbRequest.length() - 1);
        try {
            //创建URL
            URL url = new URL("https://yanyangtian.purang.com/mobile/login.htm");
            //由URL的openConnection方法得到一个HttpURLConnection（需要强转）
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //设置post提交
            httpURLConnection.setRequestMethod("POST");
            //设置超时时间
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            //把请求正文通过OutputStream发出去
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(request.getBytes());
            os.flush();
            //设置参数
            Map<String, List<String>> headerMap = httpURLConnection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
                System.out.println("Key : " + entry.getKey() +
                        " ,Value : " + entry.getValue());
            }
            //重新注入Cookie
            List<String> cookieList = headerMap.get("Set-Cookie");
            HttpUrl httpUrl = HttpUrl.parse("https://yanyangtian.purang.com");
            HashSet<String> cookies = new HashSet<>();
            for (int i = 0; i < cookieList.size(); i++) {
                Log.d("---> Http", "Response---headers:key:Set-Cookie   value:" + cookieList.get(i));
//                Cookie cookie = Cookie.parse(httpUrl, cookieList.get(i));
//                cookies.add(cookie);

                if (cookieList.get(i).contains("JSESSIONID")){
                    cookies.add(cookieList.get(i));
                }

            }
//            new SetCookieCache().addAll(cookies);


            if (!cookies.isEmpty()) {
                SharedPreferences.Editor config = Utils.getContext().getSharedPreferences("config",
                        Utils.getContext().MODE_PRIVATE).edit();
                config.putStringSet("cookie", cookies);
                config.commit();
            }

            //判断响应码  200 代表成功
            if (httpURLConnection.getResponseCode() == 200) {
                //由HttpURLConnection拿到输入流
                InputStream in = httpURLConnection.getInputStream();
                StringBuffer sb = new StringBuffer();
                //根据输入流做一些IO操作
                byte[] buff = new byte[1024];
                int len = -1;
                while ((len = in.read(buff)) != -1) {
                    sb.append(new String(buff, 0, len, "utf-8"));
                }
                in.close();
                os.close();
                httpURLConnection.disconnect();
                return sb.toString();
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.e("post", "code:" + e.getMessage());
            return null;
        }
    }

    private List<Thread> threadList = new ArrayList<>();
    private boolean isLogin = true;

    public String relogin() {
        threadList.add(Thread.currentThread());
        //设置第一个进来的线程去登录
        if (threadList.size() == 1) {
            isLogin = false;
        }
        //线程锁，保证只有一个线程执行登录
        synchronized (this) {
            String result = "";
            try {
                if (isLogin) {
                    threadList.remove(Thread.currentThread());
                    return result;
                }
                //刷新token
                result = tokenflush();
                //检查登录结果
                JSONObject reJson = new JSONObject(result);
                if (reJson.optBoolean("success")) {
                    isLogin = true;//登录成功
                    threadList.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //移除当前执行的线程
            threadList.remove(Thread.currentThread());
            return result;
        }
    }
}
