package ma.mhy.apkhook.activity;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.activity
 * 作者 mahongyin
 * 时间 2019/4/7 22:59
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import ma.mhy.apkhook.R;
import ma.mhy.apkhook.fragment.MainFragment;
import ma.mhy.apkhook.util.Tencent;

public class DexActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ColorPickerDialogListener {
    public static Context context;
    private MainFragment Main;
    private DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  context = this;
        setContentView(R.layout.activity_dex);
        initView();
        initViewPager();
    }
    private void initViewPager() {
        Main = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameview, Main).commit();
    }

    private void initView() {
        context = DexActivity.this;
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList((ColorStateList) null);

        View headerView = navigationView.getHeaderView(0);
        text = (TextView) headerView.findViewById(R.id.zz);
        text.setText("QQ:1976222027");
    }

    @Override
    public void onColorSelected(int p1, int p2) {
        switch (p1) {
            case 0:
                Main.SetColor("#" + Integer.toHexString(p2).toUpperCase());
                break;
        }
    }

    @Override
    public void onDialogDismissed(int p1) {

    }
    @Override
    public boolean onNavigationItemSelected(MenuItem p1) {
        switch (p1.getItemId()) {
            case R.id.nav_about:
                Intent intent = new Intent(DexActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_sz:
                Intent intent2 = new Intent(DexActivity.this, SettingActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_qun:
                Tencent.joinQQGroup(DexActivity.this, "iuycaEOMW-dU8VkIHRPH-VoSdRiWE-ge");
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "是否退出", Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {

                @Override
                public void onClick(View p1) {
                    Process.killProcess(Process.myPid());
                }
            }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_main_1:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
