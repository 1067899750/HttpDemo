package com.example.mylibrary.untils;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.example.mylibrary.HttpHelper;
import com.example.mylibrary.base.IHttpProcessor;
import com.example.mylibrary.net.retrofit.dns.HttpDns;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;

/**
 *
 * Description Utils初始化相关
 * Author puyantao
 * Email 1067899750@qq.com
 * Date 2019/7/15 14:40
 */
public class Utils {

    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context, IHttpProcessor httpProcessor, String baseUrl) {
        Utils.context = context.getApplicationContext();
        //初始化网络 httpHelper
        HttpHelper.init(httpProcessor);
        //初始化域名
        HttpDns.init(baseUrl);
        //初始化logger
        initLogger();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    /**
     * View获取Activity的工具
     *
     * @param view view
     * @return Activity
     */
    public static
    @NonNull
    Activity getActivity(View view) {
        Context context = view.getContext();

        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        throw new IllegalStateException("View " + view + " is not attached to an Activity");
    }

    /**
     * 全局获取String的方法
     *
     * @param id 资源Id
     * @return String
     */
    public static String getString(@StringRes int id) {
        return context.getResources().getString(id);
    }

    /**
     * 判断App是否是Debug版本
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug() {
        if (StringUtils.isSpace(context.getPackageName())) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }


    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    /**
     * 初始化logger
     */
    public static void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

}