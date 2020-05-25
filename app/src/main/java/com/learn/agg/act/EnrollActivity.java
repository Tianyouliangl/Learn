package com.learn.agg.act;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.act.contract.EnrollContract;
import com.learn.agg.act.presenter.EnrollPresenter;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.commonalitylibrary.Constant;
import com.shehuan.niv.NiceImageView;

import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Result;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;

public class EnrollActivity extends BaseMvpActivity<EnrollContract.IPresenter> implements EnrollContract.IView, View.OnClickListener {

    private EditText ed_mobile;
    private EditText ed_name;
    private EditText ed_email;
    private EditText ed_location;
    private EditText ed_password;
    private Button btn_enroll;
    private String image_url;
    private NiceImageView iv_nice_icon;

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_enroll;
    }

    @Override
    protected void initView() {
        initToolbar(true,true,false);
        initToolbar("注册");
        iv_nice_icon = findViewById(R.id.iv_nice_icon);
        ed_mobile = findViewById(R.id.ed_phone);
        ed_name = findViewById(R.id.ed_name);
        ed_email = findViewById(R.id.ed_email);
        ed_location = findViewById(R.id.ed_location);
        ed_password = findViewById(R.id.ed_password);
        btn_enroll = findViewById(R.id.btn_enroll);
    }

    @Override
    protected void initData() {
        image_url = Constant.getImageUrl();
        Glide.with(this).load(image_url).into(iv_nice_icon);
        btn_enroll.setOnClickListener(this);
    }

    @NotNull
    @Override
    public Class<? extends EnrollContract.IPresenter> registerPresenter() {
        return EnrollPresenter.class;
    }

    @Override
    public String getImageUrl() {
        return image_url;
    }

    @Override
    public String getMobile() {
        return ed_mobile.getText().toString().trim();
    }

    @Override
    public String getName() {
        return ed_name.getText().toString().trim();
    }

    @Override
    public String getEmail() {
        return ed_email.getText().toString().trim();
    }

    @Override
    public String getLocation() {
        return ed_location.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return ed_password.getText().toString().trim();
    }

    @Override
    public void onSuccess() {
        dismissDialog();
        Intent intent = getIntent();
        intent.putExtra("imageUrl",image_url);
        intent.putExtra("mobile",ed_mobile.getText().toString().trim());
        intent.putExtra("password",ed_password.getText().toString().trim());
        setResult(RESULT_OK,intent);
        showToast("注册成功");
        finish();
    }

    @Override
    public void onError(String msg) {
        showToast(msg);
        dismissDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_enroll:
                showLoadingDialog();
                getPresenter().enrollUser();
                break;
            default:
                break;
        }
    }
}
