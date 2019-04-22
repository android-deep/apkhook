package ma.mhy.apkhook;

/**
 * 项目名 APKHook
 * 所在包 ma.mhy.apkhook
 * 作者 mahongyin
 * 时间 2019/4/13 13:21
 * 邮箱 mhy.work@qq.com
 * 描述 说明:保存日志文件到sdcard，目录：sdcard根目录下的crash文件夹下
 * @Override
 *     public void onClick(View v) {
 *         //在点击事件里调用这个异常
 *         throw new RuntimeException("String");
 *     }
 */
import android.app.Application;

/**
 * Created by BAIPEI on 2017/12/5.
 */

public class CrashApplication extends Application {
    private static CrashApplication  sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
//        Thread.setDefaultUncaughtExceptionHandler(crashHandler);//让程序默认处理
    }
    public static CrashApplication getInstance() {
        return  sInstance;
    }
}
