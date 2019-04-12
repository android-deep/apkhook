package ma.mhy.apkhook.ui;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.ui
 * 作者 mahongyin
 * 时间 2019/4/7 21:55
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import ma.mhy.apkhook.MainActivity;
import ma.mhy.apkhook.MyApplication;

public class mToast {

    public static void Show(final Context context, final String paramString) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Toast.makeText(context, paramString, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void ShowDialog(final Context paramContext, final String paramString) {
        ((Activity)paramContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Builder localBuilder = new Builder(paramContext);
                localBuilder.setTitle("通信异常");
                localBuilder.setMessage(paramString);
                localBuilder.setPositiveButton("确定", (OnClickListener)null);
                localBuilder.show();
            }
        });
    }
}

