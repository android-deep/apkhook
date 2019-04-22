package ma.mhy.apkhook;

import android.accessibilityservice.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.accessibility.*;
import android.widget.*;
import android.app.*;
import android.graphics.*;

import androidx.core.app.NotificationCompat;

public class aService extends AccessibilityService implements OnTouchListener {
	WindowManager wm;
	WindowManager.LayoutParams wp;
	float x0,y0,x1,y1,x2,y2;
	TextView t;
	String s;
	float d;
	int w,h;
	
	@Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		setServiceInfo(serviceInfo);
    }

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		s=(String) event.getPackageName()+"/"+event.getClassName();
		t.setText(s);
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
            if (intent.getBooleanExtra("stop",false)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    disableSelf();
                }
                stopSelf();
				if(Build.VERSION.SDK_INT<23) {
					Activity.s(String.format("settings put secure enabled_accessibility_services %s/%s",getPackageName(),"Activity.aService"));
					Activity.s("settings put secure accessibility_enabled 0");
				}
				
            }
        }
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
		wm.removeView(t);
		super.onDestroy();
	}
	
	@Override 
    public void onCreate() {
        super.onCreate();

		if (Build.VERSION.SDK_INT >= 23)if (!Settings.canDrawOverlays(this)) {
			Toast.makeText(this, "请授权浮窗！", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
									 Uri.parse(String.format("package:%s", getPackageName()))));
		}
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		d = dm.density;
		if (dm.widthPixels < dm.heightPixels) {
			w = dm.widthPixels;
			h = dm.heightPixels;
		}
		else {
			h = dm.widthPixels;
			w = dm.heightPixels;
		}
		n();
		t=new TextView(this);
		t.setText(s);
		t.setTextSize(w/d/26);
		t.setPadding(18,0,18,2);
		t.setBackgroundDrawable(Activity.d(20,0xaa555555));
		t.setTextColor(Color.WHITE);
		t.setGravity(Gravity.CENTER);
		t.setOnTouchListener(this);
		
		wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
		wp = new WindowManager.LayoutParams();
		wp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		wp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;	
		wp.gravity = Gravity.LEFT | Gravity.TOP; 
		wp.x = 0;
		wp.y = h/30;
		wp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wp.format = PixelFormat.RGBA_8888;
//expo
		wm.addView(t, wp);
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		x1 = e.getRawX();
		y1 = e.getRawY() ; 

		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x2=x1;
				y2=y1;
				x0 = e.getX();
				y0 = e.getY();

				break;
			case MotionEvent.ACTION_MOVE:
				wp.x = (int) (x1 - x0);
				wp.y = (int) (y1 - y0);
				wm.updateViewLayout(t, wp);
				break;
			case MotionEvent.ACTION_UP:
				wp.x = (int) (x1 - x0);
				wp.y = (int) (y1 - y0);
				wm.updateViewLayout(t, wp);

				if(x2-x1==0&&x2-x1==0)
				{
					t(s);
					c(s);
				}
				x0 = y0 = 0;
				break;
		}
		return false;
	}
	
	void n() {
		PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, aService.class).putExtra("stop", true), 0);
		RemoteViews b = new RemoteViews(getPackageName(), R.layout.b);
		b.setOnClickPendingIntent(R.id.c, pi);

//		Notification n = new Notification();
//		n.icon = R.mipmap.android;
//		n.contentView = new RemoteViews(getPackageName(), R.layout.c);
//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//			n.bigContentView = b;
//			n.flags = Notification.FLAG_ONGOING_EVENT;
//		} else
//			n.contentIntent = pi;
//		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, n);


		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = null;
			if (manager != null) {
				channel = manager.getNotificationChannel("bugly");
			}
			if (channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
				Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
				intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
				intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
				startActivity(intent);
				Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
			}
		}
		Notification notification = new NotificationCompat.Builder(this, "bugly")
				//.setContentTitle("Activity")
				//.setContentText("无信息")
				.setContent(new RemoteViews(getPackageName(), R.layout.c))
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.mipmap.android)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.android))
				.setAutoCancel(true)
				.build();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			notification.bigContentView = b;
			notification.flags = Notification.FLAG_ONGOING_EVENT;
		} else
			notification.contentIntent = pi;
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notification);//n

		if (manager != null) {
			manager.notify(1, notification);
		}

	}
	void t(String s) {
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}

	void c(String s) {
		((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(s);

	}
//这里要特别声明一点的是 builder.setOnlyAlertOnce(true);这句代码，
// 因为在测试的时候发现，通知更新进度的时候，手机会一直有提示音，加上这句代码后就不会一直有提示音了。

}
