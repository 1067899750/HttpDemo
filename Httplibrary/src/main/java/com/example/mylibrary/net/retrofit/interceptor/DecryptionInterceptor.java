package com.example.mylibrary.net.retrofit.interceptor;

import androidx.annotation.NonNull;

import com.example.mylibrary.untils.AES;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * AES 解密
 */
public class DecryptionInterceptor implements Interceptor {
    public static final String TAG = "---> AddAESInterceptor";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Gson gson = new Gson();
        String url = request.url().toString();

        // 获取响应体
        Response response = chain.proceed(request);
        if (url.endsWith("_ev2")) {
            try {
                // 获取响应体
                ResponseBody responseBody = response.body();
                if (responseBody != null && responseBody.contentType() != null && responseBody.contentType().toString().contains("application/json")) {
                    // 将响应体转换为字符串
                    String json = Objects.requireNonNull(response.body()).string();
                    // 解析 JSON
                    Type mapType = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> map = gson.fromJson(json, mapType);

                    // AES 解密
                    if (map.get("data") != null && map.get("data") instanceof String) {
                        String responseData = Objects.requireNonNull(map.get("data")).toString();
                        String decryptStr = AES.decrypt(responseData);
                        Response newResponse = buildResponse(response, decryptStr);
                        return newResponse;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }


    /**
     * 构建新的 Response
     */
    private Response buildResponse(Response response, String data) throws Exception {
        //  重新构造 Response 供后续使用
        ResponseBody originalBody = response.body();
        // 创建新的ResponseBody，这里可以使用ByteString来包装新的内容
        ResponseBody newBody = ResponseBody.create(originalBody.contentType(), data);
        return response.newBuilder()
                .body(newBody)
                .build();
    }

}
