package com.learn.agg.trtc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.trtc.contract.VideoCallContract;
import com.learn.agg.trtc.presenter.VideoCallPresenter;
import com.learn.agg.trtc.service.TRTCService;

import org.jetbrains.annotations.NotNull;

public class VideoCallActivity extends BaseMvpActivity<VideoCallContract.IPresenter> implements VideoCallContract.IView, View.OnClickListener {

    private RelativeLayout rl_top;
    private RelativeLayout rl_bottom;
    private Boolean animation_start = false;
    private ImageView iv_lessen;
    private Boolean isOnCreate = false;
    private TRTCService.TRTCBinder binder;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (TRTCService.TRTCBinder) service;
            binder.showWindowView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_call;
    }

    @NotNull
    @Override
    public Class<? extends VideoCallContract.IPresenter> registerPresenter() {
        return VideoCallPresenter.class;
    }

    @Override
    protected void initView() {
        rl_top = findViewById(R.id.ll_top);
        rl_bottom = findViewById(R.id.ll_bottom);
        iv_lessen = findViewById(R.id.iv_lessen);
        isOnCreate = true;
    }

    @Override
    protected void initData() {
        iv_lessen.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnCreate) {
            in_Animation();
        }
        if (binder != null) {
            binder.removeWindowView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnCreate = false;
    }

    private void in_Animation() {
//        Animation animation_top = AnimationUtils.loadAnimation(this, R.anim.in_top);
//        Animation animation_bottom = AnimationUtils.loadAnimation(this, R.anim.in_bottom);
//        rl_top.startAnimation(animation_top);
//        rl_bottom.startAnimation(animation_bottom);
    }

    private void out_Animation() {
//        Animation animation_top = AnimationUtils.loadAnimation(this, R.anim.out_top);
//        Animation animation_bottom = AnimationUtils.loadAnimation(this, R.anim.out_bottom);
//        rl_top.startAnimation(animation_top);
//        rl_bottom.startAnimation(animation_bottom);
//        animation_bottom.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                animation_start = true;
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                rl_top.setVisibility(View.GONE);
//                rl_bottom.setVisibility(View.GONE);
//                rl_top.clearAnimation();
//                rl_bottom.clearAnimation();
//                finish();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!animation_start) {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_lessen) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                bindServiceVideo();
            }
        }
    }

    private void bindServiceVideo() {
        moveTaskToBack(true);
        if (binder == null) {
            Intent intent = new Intent(this, TRTCService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        } else {
            binder.showWindowView();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                bindServiceVideo();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binder != null) {
            binder.removeWindowView();
            unbindService(serviceConnection);
        }
    }
}