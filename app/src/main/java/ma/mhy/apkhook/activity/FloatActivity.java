package ma.mhy.apkhook.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import ma.mhy.apkhook.R;
import ma.mhy.apkhook.helper.AccessibilityUtil;
import ma.mhy.apkhook.helper.TrackerService;

public class FloatActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_ALERT_WINDOW = 2;

    private int sclaleNum=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float);
        final ImageView image = (ImageView)findViewById(R.id.image);


        Glide.with(this).asGif().load(R.drawable.sun).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(image);


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                ObjectAnimator goneAnimation = ObjectAnimator.ofFloat(view, "translationY", 0, 100);
//        goneAnimation.addListener(CompositeFragment.this);
//                ObjectAnimator alphaGoneAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(image,"rotation",0f,360f*sclaleNum);

                //隐藏动画集合
                AnimatorSet goneSet = new AnimatorSet();
                goneSet.setDuration(1000);
                //添加动画
                goneSet.play(goneAnimation).with(rotationAnimator);
                goneSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if(AccessibilityUtil.checkAccessibility(FloatActivity.this)) {

                            tosSartService();

                            Toast.makeText(FloatActivity.this,"OK",Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(FloatActivity.this,"Defeated",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationPause(Animator animation) {
                        super.onAnimationPause(animation);
                    }

                    @Override
                    public void onAnimationResume(Animator animation) {
                        super.onAnimationResume(animation);
                    }
                });
                goneSet.start();

                sclaleNum++;




            }
        });
        ActivityCompat.requestPermissions(FloatActivity.this,new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},REQUEST_CODE_ALERT_WINDOW);

        checkOverlayPermission();


        /**
         * 打开开发者
         */
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAdbDugActivity();

            }
        });



    }

    /**
     * 开启服务
     */
    private void tosSartService() {
        startService(
                new Intent(FloatActivity.this, TrackerService.class).putExtra(TrackerService.COMMAND, TrackerService.COMMAND_OPEN)
        );
        finish();
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Uri parse = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, parse);
//                Intent intent = new Intent(Settings.TYPE_APPLICATION_OVERLAY, parse);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_CODE);
                Toast.makeText(this, "请先授予 \"Activity 栈\" 悬浮窗权限", Toast.LENGTH_LONG).show();
            }
        }
    }

//    //权限判断
//        if (Build.VERSION.SDK_INT >= 23) {
//        if(!Settings.canDrawOverlays(getApplicationContext())) {
//            //启动Activity让用户授权
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            startActivity(intent);
//            return;
//        } else {
//            //执行6.0以上绘制代码
//        }
//    } else {
//        //执行6.0以下绘制代码
//    }




    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && !Settings.canDrawOverlays(this)) {
            checkOverlayPermission();
        }

//        if (requestCode == REQUEST_CODE_ALERT_WINDOW) {
//            checkOverlayPermission();
//
//            Toast.makeText(this, "请先授予 \"Activity 栈\" 悬浮窗权限", Toast.LENGTH_LONG).show();
//        }
    }


    /**
     *   判断 adb调试模式是否打开
     */
    private void startAdbDugActivity() {

        boolean enableAdb = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);//判断adb调试模式是否打开
        if (enableAdb) {

            Toast.makeText(FloatActivity.this,"adb调试模式已经打开",Toast.LENGTH_SHORT).show();
        } else {
            startDevelopmentActivity();//跳转到开发者选项界面
        }

    }


    /**
     * 打开开发者模式界面
     */
    private void startDevelopmentActivity() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();


            try {
                ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings");
                Intent intent = new Intent();
                intent.setComponent(componentName);
                intent.setAction("android.intent.action.View");
                startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();

                try {
                    Intent intent = new Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS");//部分小米手机采用这种方式跳转
                    startActivity(intent);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }
    }

}
