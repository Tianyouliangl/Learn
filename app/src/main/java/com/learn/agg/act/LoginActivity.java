package com.learn.agg.act;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.learn.agg.R;
import com.learn.agg.act.contract.LoginContract;
import com.learn.agg.act.presenter.LoginPresenter;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.Constant;
import com.makeramen.roundedimageview.RoundedImageView;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;


public class LoginActivity extends BaseMvpActivity<LoginContract.IPresenter> implements LoginContract.IView, View.OnClickListener {

    private RoundedImageView iv_nice_icon;
    private EditText ed_phone;
    private EditText ed_pwd;
    private Button btn_login;
    private TextView tv_new_user;

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @NotNull
    @Override
    public Class<? extends LoginContract.IPresenter> registerPresenter() {
        return LoginPresenter.class;
    }

    @Override
    protected void initView() {
        iv_nice_icon = findViewById(R.id.iv_nice_icon);
        ed_phone = findViewById(R.id.ed_phone);
        ed_pwd = findViewById(R.id.ed_pwd);
        btn_login = findViewById(R.id.btn_login);
        tv_new_user = findViewById(R.id.tv_new_user);
    }

    @Override
    protected void initData() {
        btn_login.setOnClickListener(this);
        tv_new_user.setOnClickListener(this);
        setUserInfo();
    }

    private void setUserInfo() {
        String info = EasySP.init(this).getString(Constant.SPKey_info(this));
        if (info != null) {
            LoginBean json = new Gson().fromJson(info, LoginBean.class);
            String password = EasySP.init(this).getString(Constant.SPKey_pwd(this));
            if (json != null) {
                getPresenter().getAllFriend(this,json.getUid());
                String mobile = json.getMobile();
                if (ed_phone != null) {
                    ed_phone.setText(mobile);
                }
                if (ed_pwd != null) {
                    ed_pwd.setText(password);
                }
                if (json.getImageUrl() != null && !json.getImageUrl().isEmpty()) {
                    if (iv_nice_icon != null) {
                        Glide.with(this).load(json.getImageUrl()).into(iv_nice_icon);
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                showLoadingDialog();
                getPresenter().login();
                break;
            case R.id.tv_new_user:
                goActivityForResult(EnrollActivity.class, 10023);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(LoginBean data) {
        dismissDialog();
        getPresenter().getAllFriend(this,data.getUid());
        EasySP.init(this).put(Constant.SPKey_UID, data.getUid())
                .put(Constant.SPKey_phone(this), data.getMobile())
                .put(Constant.SPKey_pwd(this), getPassword())
                .put(Constant.SPKey_userName(this),data.getUsername())
                .put(Constant.SPKey_icon(this),data.getImageUrl())
                .put(Constant.SPKey_info(this), new Gson().toJson(data))
                .put(Constant.SPKey_token(this),data.getToken());
        DataBaseHelp.getInstance(this).createSessions();
        DataBaseHelp.getInstance(this).createUserTable();
        DataBaseHelp.getInstance(this).createTxtTable();
        goActivity(MainActivity.class);
        finish();
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
        showToast(msg);
    }

    @Override
    public String getPhone() {
        return ed_phone.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return ed_pwd.getText().toString().trim();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String mobile = data.getStringExtra("mobile");
            String password = data.getStringExtra("password");
            String imageUrl = data.getStringExtra("imageUrl");
            switch (requestCode) {
                case 10023:
                    if (!mobile.isEmpty()) {
                        ed_phone.setText(mobile);
                        ed_phone.setSelection(mobile.length());
                    }
                    if (!password.isEmpty()) {
                        ed_pwd.setText(password);
                        ed_pwd.setSelection(password.length());
                    }
                    if (!imageUrl.isEmpty()) {
                        Glide.with(this).load(imageUrl).into(iv_nice_icon);
                    }
                    break;
            }
        }
    }
}
