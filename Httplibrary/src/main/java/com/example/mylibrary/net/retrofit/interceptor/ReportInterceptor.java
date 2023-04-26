package com.example.mylibrary.net.retrofit.interceptor;

import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 统计网络请求时间
 */
public class ReportInterceptor implements Interceptor {
    private static final String TAG = ReportInterceptor.class.getSimpleName();
    private String type;

    public ReportInterceptor(String type) {
        this.type = type;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        long startTime = SystemClock.uptimeMillis();
        Request request = chain.request();
        Response response = chain.proceed(request);
        long stopTime = SystemClock.uptimeMillis();
        report(stopTime - startTime, response);
        return response;
    }

    private void report(long callAndTime, Response response) {
        try {
            String params = getRequestBody(response.request());
            HashMap<String, Object> mapParams = toGsonMap(params);
            String method = response.request().method();
            String url = response.request().url().url().toString();
            String responseData = getResponseData(response);
            Log.i(TAG, "responseData =  " + responseData);

            Map<String, Object> map = new HashMap<>();
            map.put("time", String.valueOf(callAndTime));
            map.put("method", method);
            map.put("url", url);
            map.put("code", String.valueOf(response.code()));
            map.put("message", response.message());
            map.put("type", type);
            map.put("guid", mapParams.get("guid"));
            // TODO: 2023/4/26 上报
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 requestBody
     *
     * @param request
     * @return
     */
    private String getRequestBody(Request request) {
        RequestBody requestBody = request.body();
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //编码设为UTF-8
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"));
        }
        //拿到request
        String requestString = buffer.clone().readString(charset);
        Log.i(TAG, "request =  " + requestString);
        return requestString;
    }


    /**
     * 拦截相应数据
     */
    private String getResponseData(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);

        Buffer buffer = source.buffer();

        //编码设为UTF-8
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"));
        }

        if (!isPlaintext(buffer)) {
            return null;
        }

        if (responseBody.contentLength() != 0) {
            //拿到request
            String responseString = buffer.clone().readString(charset);
            Log.i(TAG, "request =  " + responseString);
            return responseString;
        }
        return null;
    }

    private boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    public HashMap<String, Object> toGsonMap(String jsonStr) {
        Gson gson = new Gson();
        HashMap<String, Object> res = null;
        try {
            res = gson.fromJson(jsonStr, new TypeToken<HashMap<String, Object>>() {
            }.getType());
        } catch (Exception e) {

        }
        return res;
    }

    public static String mapToString(Map<String, Object> params) {
        Gson gson = new Gson();
        return gson.toJson(params);
    }
}


