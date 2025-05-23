package com.example.mylibrary.net.retrofit.interceptor;

import android.util.Log;

import androidx.annotation.NonNull;


import com.example.mylibrary.untils.AES;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * AES 加密
 */
public class EncryptionInterceptor implements Interceptor {
    public static final String TAG = "---> EncryptionInterceptor";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        // url 以 _ev2 结尾是需要加密传递的
        if (url.endsWith("_ev2")) {
            try {
                RequestBody requestBody = request.body();
                if (requestBody != null) {
                    MediaType mediaType = requestBody.contentType();
                    if (mediaType != null) {
                        if (isText(mediaType)) {
                            Log.d(TAG, "params : " + bodyToString(request));
                            // AES 加密
                            String paramsAes = encryptParams(strToMap(bodyToString(request)));
                            ;
                            // 重新构建 request 请求
                            Request encryptedRequest = createNewRequest(request, paramsAes);
                            return chain.proceed(encryptedRequest);
                        } else {
                            Log.d(TAG, "params : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chain.proceed(request);
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType == null) return false;

        return ("text".equals(mediaType.subtype())
                || "json".equals(mediaType.subtype())
                || "xml".equals(mediaType.subtype())
                || "html".equals(mediaType.subtype())
                || "webviewhtml".equals(mediaType.subtype())
                || "x-www-form-urlencoded".equals(mediaType.subtype()));
    }

    /**
     * 获取请求参数
     */
    private String bodyToString(Request request) {
        try {
            final Request originalRequest = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            originalRequest.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }

    /**
     * 参数加密
     */
    private String encryptParams(Map<String, String> map) {
        Set<String> key = map.keySet();
        ArrayList<String> list = new ArrayList<>(key.size());
        for (int i = 0; i < key.size(); i++) {
            String str = (String) key.toArray()[i];
            list.add(i, str);
        }
        Collections.sort(list);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < map.size(); i++) {
            sb.append(list.get(i)).append("=").append(map.get(list.get(i)));
            if (i < map.size() - 1) {
                sb.append("&");
            }
        }
        String dataStr = sb.toString();
        return AES.encrypt(dataStr);
    }

    public Request createNewRequest(Request request, String data) {
        RequestBody newBody = createNewRequestBody(request, data);
        return request.newBuilder()
                .method(request.method(), newBody)
                .build();
    }

    /**
     * 构建请求体
     */
    public RequestBody createNewRequestBody(Request request, String data) {
        // 替换请求参数
        Map<String, String> map = new HashMap<>();
        map.put("appInfo", data);
        String jsonStr = mapToTabString(map);
//        MediaType jsonType = MediaType.get("application/json; charset=utf-8");
//        Gson gson = new Gson();
//        String gsonStr = gson.toJson(map);
        MediaType jsonType = MediaType.get("application/x-www-form-urlencoded");
        return RequestBody.create(jsonStr, jsonType);
    }


    private String mapToTabString(Map<String, String> map) {
        StringBuilder formData = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                formData.append("&");
            } else {
                first = false;
            }
            try {
                formData.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return formData.toString();
    }


    /**
     * str -> map
     */
    private Map<String, String> strToMap(String parStr) {
        Map<String, String> params = new HashMap<>();
        String[] paramsAllArray = parStr.split("&");
        for (int index = 0; index < paramsAllArray.length; index++) {
            String paramsStr = paramsAllArray[index];
            String[] paramsArray = paramsStr.split("=");
            params.put(paramsArray[0], paramsArray[1]);
        }
        return params;
    }


}
