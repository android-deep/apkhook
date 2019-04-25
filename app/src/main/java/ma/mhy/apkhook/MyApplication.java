package ma.mhy.apkhook;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk
 * 作者 mahongyin
 * 时间 2019/4/7 12:04
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {//或者MultiDexApplication或者直接
    //  <application
    //        ...
    //        android:name="android.support.multidex.MultiDexApplication">
    //        ...
    //    </application>
    public MainActivity mainActivity;
    private static MyApplication  sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        //使用LeakCanary检测内存泄漏
        if (LeakCanary.isInAnalyzerProcess(this)) {      // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        //        disableAPIDialog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "bugly";
            String channelName = "系统消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static MyApplication getInstance() {
        return  sInstance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 反射 禁止弹窗
     */
    private void disableAPIDialog(){
        if (Build.VERSION.SDK_INT < 28)
            return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}