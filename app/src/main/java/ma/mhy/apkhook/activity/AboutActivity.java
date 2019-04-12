package ma.mhy.apkhook.activity;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.activity
 * 作者 mahongyin
 * 时间 2019/4/7 22:57
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import ma.mhy.apkhook.BuildConfig;
import ma.mhy.apkhook.Constant;
import ma.mhy.apkhook.R;
import ma.mhy.apkhook.util.AppUtils;

public class AboutActivity extends AppCompatActivity implements OnClickListener {
    Context context;
    private Toolbar mToolbarAbout;
    private ScrollView mScrollAbout;
    private ImageView mImgCardAbout1;
    private TextView mTvCardAbout11,buidtime;
    private TextView mTvAboutVersion;
    private CardView mCardAbout2;
    private TextView mTvCardAbout21;
    private View mViewCardAbout2Line;
    private LinearLayout mLlCardAbout2Email;
    private LinearLayout mLlCardAbout2Website;
    private LinearLayout mLlCardAboutSourceLicenses;
    private FloatingActionButton mFabAboutShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_about);
        mToolbarAbout = findViewById(R.id.toolbar_about);
        mToolbarAbout.setTitle ( "关于" );
        setSupportActionBar ( mToolbarAbout );
        if( getSupportActionBar ( ) != null ) {
            getSupportActionBar ( ).setDisplayHomeAsUpEnabled ( true );
        }
        getWindow().setNavigationBarColor(getResources().getColor (R.color.colorPrimary));
        initView();
        buidtime.setText("Build "+ BuildConfig.apkBuildTime);
    }
    public void initView() {
buidtime=findViewById(R.id.buidtime);
        mScrollAbout = findViewById(R.id.scroll_about);
        mImgCardAbout1 = findViewById(R.id.img_card_about_1);
        mTvCardAbout11 = findViewById(R.id.tv_card_about_1_1);
        mTvAboutVersion = findViewById(R.id.tv_about_version);
        mCardAbout2 = findViewById(R.id.card_about_2);
        mTvCardAbout21 = findViewById(R.id.tv_card_about_2_1);
        mViewCardAbout2Line = findViewById(R.id.view_card_about_2_line);
        mLlCardAbout2Email = findViewById(R.id.ll_card_about_2_email);
        mLlCardAbout2Website = findViewById(R.id.ll_card_about_2_website);
        mLlCardAboutSourceLicenses = findViewById(R.id.ll_card_about_source_licenses);
        mFabAboutShare = findViewById(R.id.fab_about_share);
        mLlCardAbout2Email.setOnClickListener(this);
        mLlCardAbout2Website.setOnClickListener(this);
        mLlCardAboutSourceLicenses.setOnClickListener(this);
        mFabAboutShare.setOnClickListener(this);
        Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);
        mTvCardAbout21.setText(AppUtils.getVersionName(this));
        mTvCardAbout21.startAnimation(alphaAnimation);
        mTvAboutVersion.setText ( AppUtils.getVersionName ( this ) );
        mTvAboutVersion.startAnimation ( alphaAnimation );
    }
    @Override
    public void onClick( View view ) {
        Intent intent = new Intent ( );
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder ( );
        builder.setToolbarColor (getResources().getColor(R.color.colorPrimary));
        builder.setShowTitle ( true );
        CustomTabsIntent customTabsIntent = builder.build ( );
        switch( view.getId()) {
            case R.id.ll_card_about_2_email:
                intent.setAction ( Intent.ACTION_SENDTO );
                intent.setData ( Uri.parse ( Constant.EMAIL ) );
                intent.putExtra ( Intent.EXTRA_SUBJECT, "邮箱反馈" );
                try {
                    startActivity ( intent );
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.ll_card_about_source_licenses:
                AlertDialog.Builder b=new AlertDialog.Builder ( this );
                b.setTitle ( "开源许可" );
                WebView a=new WebView ( this );
                b.setView ( a );
                a.loadUrl("file:///android_asset/open_source_license.html");
                b.show();
                //customTabsIntent.launchUrl ( this, Uri.parse ( "file:///android_asset/open_source_license.html" ) );
                break;

            case R.id.ll_card_about_2_website:
                customTabsIntent.launchUrl ( this, Uri.parse ( Constant.MY_WEBSITE ) );
                break;

            case R.id.fab_about_share:
                intent.setAction ( Intent.ACTION_SEND );
                intent.putExtra ( Intent.EXTRA_TEXT, Constant.SHARE_CONTENT );
                intent.setType ( "text/plain" );
                startActivity ( Intent.createChooser ( intent, "分享" ) );
                break;
        }
    }
}