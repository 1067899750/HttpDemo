package com.example.httpdemo.untils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 应用间跳转
 */
public class ForwardUtils {

    /**
     * 判断应用是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拉起应用
     * <intent-filter>
     * <action android:name="SHIQJ" />
     * <category android:name="android.intent.category.DEFAULT" />
     * </intent-filter>
     */
    public static void skipAppOne(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("SHIQJ");//这个值一定要和B应用的action一致，否则会报错
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 拉起应用
     */
    public static void skipAppTwo(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.lightpalm.fenqia", "com.lightpalm.daidai.loan.launch.LaunchActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 拉起应用
     */
    public static void skipAppThree(Context context) {
        boolean isAppInstalled = false;
        try {
            context.getPackageManager().getPackageInfo("com.example.app", PackageManager.GET_ACTIVITIES);
            isAppInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isAppInstalled = false;
        }

        try {
            if (isAppInstalled) {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.app");
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            } else {
                Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 拉起应用
     * <p>
     * <intent-filter>
     * <data
     * android:host="B应用包名最好"
     * android:path="/shiqj"
     * android:scheme="shiqj" />
     * <action android:name="android.intent.action.VIEW" />
     * <p>
     * <category android:name="android.intent.category.DEFAULT" />
     * <category android:name="android.intent.category.BROWSABLE" />
     * </intent-filter>
     */
    public static void skipAppFour(Context context) {
        try {
            Intent intent = new Intent();
            Uri uri = Uri.parse("fenqi://loan/splash");
            intent.putExtra("", "");//这里Intent当然也可传递参数,但是一般情况下都会放到上面的URI中进行传递也就是"scheme://host/path?xx=xx"
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 启动到应用商店app详情界面
     *
     * @param appPkg    目标App的包名
     * @param marketPkg 应用商店包名
     */
    public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
