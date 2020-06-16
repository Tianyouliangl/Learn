package com.learn.agg.act;

import android.view.KeyEvent;
import android.view.View;

import com.learn.agg.R;
import com.learn.agg.base.BaseAppCompatActivity;
import com.learn.commonalitylibrary.Constant;
import com.white.easysp.EasySP;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseAppCompatActivity {

    private Timer timer;
    private String mobile;
    private String password;

    @Override
    protected int getLayoutId() {
        setTheme(R.style.SplashTheme);
        final View decorView = getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if ((i & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(uiOptions);
                } else {

                }
            }
        });
        return R.layout.activity_splash;
    }

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
        mobile = EasySP.init(this).getString(Constant.SPKey_phone(this));
        password = EasySP.init(this).getString(Constant.SPKey_pwd(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mobile.isEmpty() || password.isEmpty()){
                    goActivity(LoginActivity.class);
                }else {
                    goActivity(MainActivity.class);
                }
//                goActivity(LoginActivity.class);
                finish();
            }
        }, 1500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer = null;
        }
    }
}
