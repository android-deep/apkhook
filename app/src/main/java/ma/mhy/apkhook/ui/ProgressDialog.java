package ma.mhy.apkhook.ui;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ui
 * 作者 mahongyin
 * 时间 2019/4/7 21:53
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import android.content.Context;

public class ProgressDialog {
    private static android.app.ProgressDialog Loading;
    private static android.app.ProgressDialog Loading2;

    public static void hide() {
        if (Loading != null) {
            Loading.dismiss();
        }
        if (Loading2 != null) {
            Loading2.dismiss();
        }
    }

    public static void setProgress(int paramInt) {
        Loading2.setProgress(paramInt);
    }

    public static void show(Context paramContext) {
        Loading = new android.app.ProgressDialog(paramContext);
        Loading.setMessage("通讯中........");
        Loading.setCancelable(false);
        Loading.show();
    }

    public static void show(Context paramContext, String paramString) {
        Loading = new android.app.ProgressDialog(paramContext);
        Loading.setMessage(paramString);
        Loading.setCancelable(false);
        Loading.show();
    }

    public static void show(Context paramContext, String paramString, int paramInt) {
        Loading2 = new android.app.ProgressDialog(paramContext);
        Loading2.setMessage(paramString);
        Loading2.setMax(paramInt);
        Loading2.setProgress(0);
        Loading2.setProgressStyle(1);
        Loading2.setCancelable(false);
        Loading2.show();
    }
}

