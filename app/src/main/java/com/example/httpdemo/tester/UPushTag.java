package com.example.httpdemo.tester;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushTagCallback;
import com.umeng.message.common.inter.ITagManager;

import java.util.Arrays;
import java.util.List;

public class UPushTag {

    private static final String TAG = "UPushTag";

    /**
     * 增加标签
     */
    public static void add(Context context, String... tag) {
        PushAgent.getInstance(context).getTagManager().addTags(new UPushTagCallback<ITagManager.Result>() {
            @Override
            public void onMessage(boolean success, ITagManager.Result result) {
                String msg;
                if (success) {
                    msg = "add tag success! " + Arrays.asList(tag) + " result:" + result.toString();
                } else {
                    msg = "add tag failure! " + result.toString();
                }
                toast(context, msg);
            }
        }, tag);
    }

    /**
     * 删除标签
     */
    public static void delete(Context context, String... tag) {
        PushAgent.getInstance(context).getTagManager().deleteTags(new UPushTagCallback<ITagManager.Result>() {
            @Override
            public void onMessage(boolean success, ITagManager.Result result) {
                String msg;
                if (success) {
                    msg = "delete tag success! " + Arrays.asList(tag) + " result:" + result.toString();
                } else {
                    msg = "delete tag failure! " + result.toString();
                }
                toast(context, msg);
            }
        }, tag);
    }

    /**
     * 查询标签
     */
    public static void getAll(Context context) {
        PushAgent.getInstance(context).getTagManager().getTags(new UPushTagCallback<List<String>>() {
            @Override
            public void onMessage(boolean success, List<String> tags) {
                String msg;
                if (success) {
                    msg = "get all tag success! " + tags.toString();
                } else {
                    msg = "get all tag failure! " + tags.toString();
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
