package com.learn.agg.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.learn.agg.R;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionHelper;
import com.zyq.easypermission.EasyPermissionResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

public abstract class BaseAppCompatActivity extends AppCompatActivity {

    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private EasyPermission easyPermission;
    private int RC_CODE_CALLPHONE = 12907;
    private Boolean isRequestMission = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(getLayoutId());
        isRequestMission = isRequestMission();
        if (isRequestMission){
            requestPermission();
        }else {
            initView();
            initData();
        }
    }

    protected abstract int getLayoutId();

    protected void initView() {

    }

    protected abstract Boolean isRequestMission();

    protected void initData() {

    }

    private void requestPermission() {
        if (EasyPermission.build().hasPermission(this, needPermissions)) {
            initView();
            initData();
        } else {
            easyPermission = EasyPermission.build()
                    .mRequestCode(RC_CODE_CALLPHONE)
                    .mContext(this)
                    .mPerms(needPermissions)
                    .mResult(new EasyPermissionResult() {
                        @Override
                        public void onPermissionsAccess(int requestCode) {
                            super.onPermissionsAccess(requestCode);
                            //做你想做的
                            initView();
                            initData();
                        }

                        @Override
                        public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                            super.onPermissionsDismiss(requestCode, permissions);
                            //你的权限被用户拒绝了你怎么办
                            finish();
                        }

                        @Override
                        public boolean onDismissAsk(int requestCode, @NonNull List<String> permissions) {
                            //你的权限被用户禁止了并且不能请求了你怎么办
                            easyPermission.openAppDetails(BaseAppCompatActivity.this, new String[]{"相关权限需要你去手动设置。"});
                            return true;
                        }
                    });
            easyPermission.requestPermission();
        }
    }


    protected void goActivity(Class cls){
        goActivity(cls,null);
    }

    protected void goActivity(Class cls,Bundle bundle){
        Intent intent = new Intent(this, cls);
        if (bundle != null){
            intent.putExtra("bundle",bundle);
        }
        this.startActivity(intent);
    }

    protected void setPermissionList(String[] p){
            if (p.length <= 0){
            return;
        }
        needPermissions = p;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //使用EasyPermissionHelper注入回调
        EasyPermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (EasyPermission.APP_SETTINGS_RC == requestCode) {
            //设置界面返回
            //Result from system setting
            if (easyPermission.hasPermission(this)) {
                //做你想做的
                initView();
                initData();
            } else {
                finish();
            }
        }
    }
}
