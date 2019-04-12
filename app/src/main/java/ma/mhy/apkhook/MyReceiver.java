package ma.mhy.apkhook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    MainActivity mainActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取广播的名字
        String action=intent.getAction();
        Log.e("广播",action);
        if("ma.mhy.hookapk.re".equals(action)){
            //获取广播内容
            String content=intent.getStringExtra("content");
            Log.e("test","广播接受者1号："+content);
             mainActivity = ((MyApplication) context.getApplicationContext()).mainActivity;
            mainActivity.etContent.append("注册码: "+intent.getAction()+"\n");
            Toast.makeText(context, ""+content, Toast.LENGTH_SHORT).show();
        }
if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
    Toast.makeText(context, "开机启动了", Toast.LENGTH_SHORT).show();
}
    }
}
