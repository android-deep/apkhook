package ma.mhy.apkhook;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ma.mhy.apkhook.activity.DexActivity;
import ma.mhy.apkhook.activity.FloatActivity;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnVIP)
    Button btnVIP;
    @BindView(R.id.btnDex)
    Button btnDex;
    @BindView(R.id.btnAct)
    Button btnAct;
    private EditText etNum;
    EditText etContent;
    String num = "";
    int code;
    private ClipboardManager cm;
    private ClipData mClipData;
    SMSBroadcastReceiver mBroadcastReceiver;
    private MyReceiver myReceiver02;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        etNum = (EditText) findViewById(R.id.Number);
        etContent = (EditText) findViewById(R.id.Content);
        btnAct = findViewById(R.id.btnAct);
        MyApplication myApplication = (MyApplication) this.getApplicationContext();
        myApplication.mainActivity = this;
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);
        }//动态申请权限

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        //注册广播
//        registerReceiver(myReceiver02,intentFilter);
//    }
//
//    //在动态注册中，一定要在Activity注销时注销注册，否则会报错
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // 取消短信广播注册
//        //注销注册
//        unregisterReceiver(myReceiver02);
//    }

    //    动态注册广播
    private void registSmsReciver() {
        myReceiver02 = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("ma.mhy.hookapk.re");//.re之前是包名
        IntentFilter filter = new IntentFilter();
        filter.addAction("ma.mhy.hookapk.re");
        // 设置优先级 不然监听不到短信
        filter.setPriority(1000);
        Snackbar.make(etContent, "注册短信广播", Snackbar.LENGTH_LONG).show();
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void send(View view) {
        num = etNum.getText().toString();
        if (!num.equals("")) {
            char[] toCharArray = num.toCharArray();
            int key = 0;
            for (int i = 0; i < toCharArray.length; i++) {
                key += toCharArray[i];
            }
            code = key * 2;
            etContent.setText(code + "");
            //获取剪贴板管理器：
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
// 创建普通字符型ClipData
            mClipData = ClipData.newPlainText("Label", code + "");
// 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "机器码错误", Toast.LENGTH_SHORT).show();
        }

//发送广播   test 貌似无效
        //获取输入的文本
        String content = code + "";
        //发送广播
        Intent intent = new Intent();
        //指定广播的名字
        intent.setAction("ma.mhy.hookapk.re");
        //指定广播的内容
        intent.putExtra("content", content);
        //发送广播
        sendBroadcast(intent);
    }

    public String getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";
        final String SMS_URI_OUTBOX = "content://sms/outbox";
        final String SMS_URI_FAILED = "content://sms/failed";
        final String SMS_URI_QUEUED = "content://sms/queued";

        StringBuilder smsBuilder = new StringBuilder();

        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");      // 获取手机内部短信

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    String strType = "";
                    if (intType == 1) {
                        strType = "接收";
                    } else if (intType == 2) {
                        strType = "发送";
                    } else {
                        strType = "null";
                    }

                    smsBuilder.append("[ ");
                    smsBuilder.append(strAddress + ", ");
                    smsBuilder.append(intPerson + ", ");
                    smsBuilder.append(strbody + ", ");
                    smsBuilder.append(strDate + ", ");
                    smsBuilder.append(strType);
                    smsBuilder.append(" ]\n\n");
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if

            smsBuilder.append("getSmsInPhone has executed!");

        } catch (SQLiteException ex) {
            Log.d("TAG", ex.getMessage());
        }

        return smsBuilder.toString();
    }

    @OnClick({R.id.btnVIP, R.id.btnDex,R.id.btnAct})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnVIP:
                startActivity(new Intent(MainActivity.this, FloatActivity.class));
                break;
            case R.id.btnDex:
                startActivity(new Intent(MainActivity.this, DexActivity.class));
                break;
                case R.id.btnAct:
                startActivity(new Intent(MainActivity.this, Activity.class));
                break;
        }
    }
}