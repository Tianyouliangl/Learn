package com.learn.agg.act;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.learn.agg.R;
import com.learn.agg.act.contract.LoginContract;
import com.learn.agg.act.presenter.LoginPresenter;
import com.learn.agg.base.BaseAppCompatActivity;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.net.bean.LoginBean;
import com.learn.agg.widgets.CustomDialog;
import com.learn.commonalitylibrary.Constant;
import com.lib.xiangxiang.im.SocketManager;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;

public class GlobalDialogActivity extends BaseMvpActivity<LoginContract.IPresenter> implements LoginContract.IView, View.OnClickListener {


    private TextView tv_content;
    private TextView tv_exit;
    private TextView tv_again_login;

    @Override
    protected int getLayoutId() {
        setTheme(R.style.MyDialogStyle);
        return R.layout.dialog_offline;
    }

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected void initView() {
        tv_content = findViewById(R.id.tv_content);
        tv_exit = findViewById(R.id.tv_exit);
        tv_again_login = findViewById(R.id.tv_again_login);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String body = bundle.getString("body");
        tv_content.setText(body);
        tv_exit.setOnClickListener(this);
        tv_again_login.setOnClickListener(this);
    }

    @Override
    public void onSuccess(LoginBean data) {
        dismissDialog();
        EasySP.init(this).put(Constant.SPKey_UID, data.getUid())
                .put(Constant.SPKey_phone(this), data.getMobile())
                .put(Constant.SPKey_pwd(this), getPassword())
                .put(Constant.SPKey_userName(this),data.getUsername())
                .put(Constant.SPKey_icon(this),data.getImageUrl())
                .put(Constant.SPKey_info(this), new Gson().toJson(data))
                .put(Constant.SPKey_token(this),data.getToken());
        finish();
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
        Intent intent = getIntent();
        intent.putExtra("isFinish", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getPhone() {
        return EasySP.init(this).getString(Constant.SPKey_phone(this));
    }

    @Override
    public String getPassword() {
        return EasySP.init(this).getString(Constant.SPKey_pwd(this));
    }

    @NotNull
    @Override
    public Class<? extends LoginContract.IPresenter> registerPresenter() {
        return LoginPresenter.class;
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        switch (v.getId()) {
            case R.id.tv_exit:
                intent.putExtra("isFinish", true);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_again_login:
                showLoadingDialog();
                getPresenter().login();
                intent.putExtra("isFinish", false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
