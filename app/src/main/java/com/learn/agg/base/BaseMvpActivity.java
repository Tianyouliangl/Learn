package com.learn.agg.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import com.learn.agg.R;
import com.learn.agg.widgets.CustomDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.view.MvpActivity;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionHelper;
import com.zyq.easypermission.EasyPermissionResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.MissingResourceException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class BaseMvpActivity<P extends IPresenterContract> extends MvpActivity<P> {

    private CustomDialog mLoading;
    private Boolean mDialogIsBack = true;
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
            createLoading();
            initData();
        }
    }

    protected abstract Boolean isRequestMission();



    private void createLoading() {
        mLoading = new CustomDialog(this, mDialogIsBack);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();


    private void requestPermission() {
        if (EasyPermission.build().hasPermission(this, needPermissions)) {
            initView();
            createLoading();
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
                            createLoading();
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
                            easyPermission.openAppDetails(BaseMvpActivity.this, new String[]{"相关权限需要你去手动设置。"});
                            return true;
                        }
                    });
            easyPermission.requestPermission();
        }
    }


    @Override
    public void showLoadingDialog() {
        if (mLoading != null && !mLoading.isShowing()) {
            mLoading.show();
        }
    }

    protected void setDialogIsBack(Boolean back){
        this.mDialogIsBack = back;
    }



    @Override
    public void dismissDialog() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

    @Override
    public void showToast(@NotNull String msg) {
        super.showToast(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void setDialogBack(Boolean b){
        mDialogIsBack = b;
    }

    protected void initToolbar(Boolean l, Boolean c, Boolean r) {
        RoundedImageView back = findViewById(R.id.toolbar_back);
        TextView title = findViewById(R.id.toolbar_title);
        TextView right_content = findViewById(R.id.toolbar_action);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (back == null || title == null || right_content == null || toolbar == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        setActionBar(toolbar);
        back.setVisibility(l ? VISIBLE : GONE);
        title.setVisibility(c ? VISIBLE : GONE);
        right_content.setVisibility(r ? VISIBLE : GONE);
//        toolbar.setVisibility(l && c && r ? VISIBLE : GONE);
    }

    protected void initToolbar(String titleContent) {
        RoundedImageView back = findViewById(R.id.toolbar_back);
        TextView title = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        if (back == null || title == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(titleContent);
    }

    protected void initToolbar(String titleContent, String right) {
        RoundedImageView back = findViewById(R.id.toolbar_back);
        TextView title = findViewById(R.id.toolbar_title);
        TextView right_content = findViewById(R.id.toolbar_action);
        right_content.setText(right);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        if (back == null || title == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(titleContent);
    }

    protected void initToolbar(String titleContent, String right,View.OnClickListener onClickListener) {
        RoundedImageView back = findViewById(R.id.toolbar_back);
        TextView title = findViewById(R.id.toolbar_title);
        TextView right_content = findViewById(R.id.toolbar_action);
        right_content.setText(right);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        if (back == null || title == null || right_content == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(titleContent);
        right_content.setOnClickListener(onClickListener);
    }

    protected void goActivity(Class cls) {
        goActivity(cls, null);
    }

    protected void goActivityForResult(Class cls,int code) {
        goActivity(cls, null,code);
    }

    protected void goActivity(Class cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        this.startActivity(intent);
    }

    protected void goActivity(Class cls, Bundle bundle,int code) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        this.startActivityForResult(intent,code);
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
                createLoading();
                initData();
            } else {
                finish();
            }
        }
    }
}
