package ma.mhy.apkhook;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk
 * 作者 mahongyin
 * 时间 2019/4/5 22:28
 * 邮箱 mhy.work@qq.com
 * 描述 说明:Android短信的发送和广播接收者实现短信的监听
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

//首先要声明广播
public class SMSBroadcastReceiver extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        SmsMessage msg = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object p : pdusObj) {
                msg= SmsMessage.createFromPdu((byte[]) p);
                // 短信的内容
                String msgTxt =msg.getMessageBody();//得到消息的内容
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                      // 短信的发送方
                String senderNumber = msg.getOriginatingAddress();

                if (msgTxt.equals("Testing!")) {
                    Toast.makeText(context, "success!", Toast.LENGTH_LONG).show();
                    Log.e("","success");
                    return;
                } else {
                    Toast.makeText(context, "发送人："+senderNumber+"  短信内容："+msgTxt+"接受时间："+receiveTime, Toast.LENGTH_LONG).show();
                   Log.e("","发送人："+senderNumber+"  短信内容："+msgTxt+"接受时间："+receiveTime);
                    return;
                }
            }
            return;
        }
    }




}