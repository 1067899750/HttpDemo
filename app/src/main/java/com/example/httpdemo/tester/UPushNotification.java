package com.example.httpdemo.tester;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.httpdemo.helper.PushConstants;
import com.umeng.message.PushAgent;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class UPushNotification {

    static boolean useInApp = false;

    /**
     * 发送透传消息
     */
    public static void send(Context context, String ticker, String title, String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AndroidNotification msg = new AndroidNotification(PushConstants.APP_KEY, PushConstants.APP_MASTER_SECRET);
                    msg.setDeviceToken(PushAgent.getInstance(context).getDeviceToken());
                    msg.setTicker(ticker);
                    useInApp = !useInApp;
                    if (useInApp) {
                        msg.setTitle(title + " 前台时在应用内展示");
                    } else {
                        msg.setTitle(title);
                    }
                    msg.setText(content);
                    msg.goAppAfterOpen();
                    msg.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
                    msg.setTestMode();
                    msg.setExtraField("k1", "1");
                    msg.setExtraField("k2", "ab");
                    msg.useInApp(useInApp);
                    sendImpl(context, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendImpl(final Context context, BaseNotification msg) throws Exception {
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        msg.setPredefinedKeyValue("timestamp", timestamp);

        String url = "https://msgapi.umeng.com/api/send";
        String postBody = msg.getPostBody();

        Log.i("send", postBody);
        String p_sign = "POST" + url + postBody + msg.getAppMasterSecret();
        String sign = md5(p_sign);
        url = url + "?sign=" + sign;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(postBody.getBytes());
        outputStream.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream inputStream = connection.getInputStream();
        if (inputStream != null) {
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        }

        connection.disconnect();

        JSONObject responseJson = new JSONObject(out.toString());
        String ret = responseJson.getString("ret");
        String message;
        if (!ret.equalsIgnoreCase("SUCCESS")) {
            message = "发送失败";
        } else {
            String msgId = null;
            JSONObject data = responseJson.optJSONObject("data");
            if (data != null) {
                msgId = data.optString("msg_id");
            }
            message = "发送成功" + (msgId == null ? "" : " msgId:" + msgId);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.i("send", message);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String md5(String string) throws Exception {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes());
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(i));
        }
        return sb.toString();
    }
}
