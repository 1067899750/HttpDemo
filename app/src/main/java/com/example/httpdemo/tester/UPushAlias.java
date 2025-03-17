package com.example.httpdemo.tester;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushAliasCallback;

public class UPushAlias {

    private static final String TAG = "UPushAlias";

    /**
     * 增加别名
     */
    public static void add(Context context, String alias, String type) {
        PushAgent.getInstance(context).addAlias(alias, type, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean success, String message) {
                String msg;
                if (success) {
                    msg = "add alias success! type:" + type + " alias:" + alias;
                } else {
                    msg = "add alias failure! msg:" + message;
                }
                toast(context, msg);
            }
        });
    }

    /**
     * 删除别名
     */
    public static void delete(Context context, String alias, String type) {
        PushAgent.getInstance(context).deleteAlias(alias, type, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean success, String message) {
                String msg;
                if (success) {
                    msg = "delete alias success! type:" + type + " alias:" + alias;
                } else {
                    msg = "delete alias failure! msg:" + message;
                }
                toast(context, msg);
            }
        });
    }

    /**
     * 绑定别名
     */
    public static void set(Context context, String alias, String type) {
        PushAgent.getInstance(context).setAlias(alias, type, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean success, String message) {
                String msg;
                if (success) {
                    msg = "set alias success! type:" + type + " alias:" + alias;
                } else {
                    msg = "set alias failure! msg:" + message;
                }
                toast(context, msg);
            }
        });
    }


    private static void toast(Context context, String msg) {
        Log.i(TAG, msg);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
