package ma.mhy.apkhook;

import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.LinearLayout.*;
import java.io.*;
import android.text.*;
import android.text.style.*;
import android.graphics.*;

public class Activity extends android.app.Activity implements OnClickListener {
	LinearLayout l,l1;
	LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,1);
	Animation a1,aa;
	Button b;
	TextView t;
	float d,z;
	int w,h,p,m;
	SpannableString ss; 
	String ver="v2.2";
	String[] s={"ROOT开启","ROOT关闭","手动开关","快速关闭"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setNavigationBarColor(0);
			getWindow().setStatusBarColor(0);
		}else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
			WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//else getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		d = dm.density;
		if(dm.widthPixels<dm.heightPixels) {
			w = dm.widthPixels;
			h = dm.heightPixels;
		}else{
			h = dm.widthPixels;
			w = dm.heightPixels;
		}
		p=w/36;
		m=w/60;
		z=w/d/20;
		lp.setMargins(m,m,m,m);
		l=new LinearLayout(this);
		l.setPadding(p,p,p,p);
		l.setGravity(Gravity.CENTER);
		l.setOnClickListener(this);
		a1=new TranslateAnimation(0, 0, h, 0);
		a1.setDuration(500);
		l.startAnimation(a1);
		l1=new LinearLayout(this);
		l1.setPadding(p,0,p,p);
		l1.setOrientation(LinearLayout.VERTICAL);
		l1.setBackgroundDrawable(d(w/10,0xbbcccccc));
		l1.setLayoutParams(lp);
		l.addView(l1);
		t=new TextView(this);
		t.setText(sb("Activity",ver)); 
		t.setTextSize(z);
		t.setTextColor(0xffff5555);
		t.setGravity(Gravity.CENTER);
		l1.addView(t);
		for(int i=0;i<s.length;i++)b(i);
		addContentView(l,new WindowManager.LayoutParams());
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case 0:
				s(String.format("settings put secure enabled_accessibility_services %s/%s",getPackageName(),"Activity.aService"));
				s("settings put secure accessibility_enabled 1");
				break;
			case 1:
				//s(String.format("settings put secure enabled_accessibility_services %s/%s",getPackageName(),"Activity.aService"));
				//s("settings put secure accessibility_enabled 0");
				s("am force-stop Activity.Activity");
				break;
			case 2:
				startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
				t("找到Activity，打开或关闭本程序！");
				break;
			case 3:
				startService(new Intent(Activity.this, aService.class).putExtra("stop", true));
				break;
			default:finish();
		}
	}
	
	void b(int i) {
		b=new Button(this);
		b.setId(i);
		b.setText(s[i]);
		b.setTextColor(0xffffffff);
		b.setTextSize(z);
		b.setBackgroundDrawable(d(w/16,0xffff5556));
		b.setPadding(p,p,p,p);
		b.setLayoutParams(lp);
		b.setOnClickListener(this);
		l1.addView(b);

	}
	
	SpannableStringBuilder sb(String b,String s)
	{
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(b);
		sb.append("\n");
		sb.append(s);
		sb.setSpan(new RelativeSizeSpan(2),0,b.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sb.setSpan(new StyleSpan(Typeface.BOLD), 0, b.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sb.setSpan(new RelativeSizeSpan(0.8f),b.length()+1,b.length()+1+s.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sb.setSpan(new StyleSpan(Typeface.ITALIC), b.length()+1,b.length()+1+s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sb;
	}
	
	void t(String s) {
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}
	
	static Drawable d(int r,int c) {
		GradientDrawable d=new GradientDrawable();
		d.setColor(c);
		d.setCornerRadius(r);
		d.setStroke(2, 0xffeeeeee);
		return d;
	}

	static java.lang.Process su;
	static void s(String s) {
		try
		{
			if (su == null)su = Runtime.getRuntime().exec("su");
			OutputStream o=su.getOutputStream();
			o.write((s + "\n").getBytes());
			o.flush();
		}
		catch (IOException e)
		{}
	}
}
