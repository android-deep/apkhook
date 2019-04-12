package ma.mhy.apkhook;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk
 * 作者 mahongyin
 * 时间 2019/4/7 12:04
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.app.Application;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.multidex.MultiDex;

public class MyApplication extends Application {//或者MultiDexApplication或者直接
    //  <application
    //        ...
    //        android:name="android.support.multidex.MultiDexApplication">
    //        ...
    //    </application>
    public MainActivity mainActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        //        disableAPIDialog();
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