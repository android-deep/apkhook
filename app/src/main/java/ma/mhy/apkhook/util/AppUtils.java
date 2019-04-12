package ma.mhy.apkhook.util;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.util
 * 作者 mahongyin
 * 时间 2019/4/7 18:05
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppUtils {
    public static boolean checkAppInstalled(Context context, String str) {
        try {
            context.getPackageManager().getPackageInfo(str, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static int getVersionCode(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return new StringBuffer().append("Version ").append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getVersionName_No(Context context) {
        try {
            return new StringBuffer().append("Version ").append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
