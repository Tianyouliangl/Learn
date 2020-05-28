package com.learn.agg.msg.act;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.contract.FriendInfoContract;
import com.learn.agg.msg.presenter.FriendInfoPresenter;
import com.learn.agg.net.bean.LoginBean;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.OfTenUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

public class FriendInfoActivity extends BaseMvpActivity<FriendInfoContract.IPresenter> implements FriendInfoContract.IView, View.OnClickListener {


    private RoundedImageView iv_icon;
    private TextView tv_name;
    private TextView tv_mobile;
    private TextView tv_sex_location;
    private TextView tv_sign;
    private TextView tv_birthday;
    private TextView tv_email;
    private Button btn_start;
    private String uid;
    private int online;
    private TextView tv_source;
    private TextView tv_remark;
    private LinearLayout ll_remark;
    private LinearLayout ll_source;

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_info;
    }


    @NotNull
    @Override
    public Class<? extends FriendInfoContract.IPresenter> registerPresenter() {
        return FriendInfoPresenter.class;
    }

    @Override
    protected void initView() {
        initToolbar("个人资料", "设置");
        ll_remark = findViewById(R.id.ll_remark);
        ll_source = findViewById(R.id.ll_source);
        iv_icon = findViewById(R.id.niv_icon);
        tv_name = findViewById(R.id.tv_name);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_sex_location = findViewById(R.id.tv_sex_location);
        tv_sign = findViewById(R.id.tv_sign);
        tv_birthday = findViewById(R.id.tv_birthday);
        tv_email = findViewById(R.id.tv_email);
        btn_start = findViewById(R.id.btn_start);
        tv_source = findViewById(R.id.tv_source);
        tv_remark = findViewById(R.id.tv_remark);
    }

    @Override
    protected void initData() {
        String local_mobile = EasySP.init(this).getString(Constant.SPKey_phone(this));
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String data = bundle.getString("data");
        boolean gson = bundle.getBoolean("isGone", false);
        LoginBean bean = GsonUtil.GsonToBean(data, LoginBean.class);
        uid = bean.getUid();
        String imageUrl = bean.getImageUrl();
        String username = bean.getUsername();
        String mobile = bean.getMobile();
        String sex = bean.getSex();
        String location = bean.getLocation();
        String sign = bean.getSign();
        String birthday = bean.getBirthday();
        String email = bean.getEmail();
        String remark = bean.getRemark();
        int source = bean.getSource();
        online = bean.getOnline();
        Boolean friend = bean.getFriend();
        Glide.with(this).load(imageUrl).into(iv_icon);
        tv_name.setText(username);
        tv_mobile.setText("账号:" + mobile);
        tv_sex_location.setText(sex + " " + location);
        tv_sign.setText(sign);
        tv_birthday.setText(birthday);
        tv_email.setText(email);
        tv_remark.setText("备注:(" + remark + ")");
        btn_start.setVisibility(gson ? View.GONE : View.VISIBLE);
        btn_start.setText(friend ? "发消息" : "加好友");
        ll_remark.setVisibility(friend ? View.VISIBLE : View.GONE);
        ll_source.setVisibility(friend ? View.VISIBLE : View.GONE);
        btn_start.setVisibility(local_mobile.equals(mobile) ? View.GONE : View.VISIBLE);
        btn_start.setOnClickListener(this);
        if (source == 1) {
            tv_source.setText("账号添加");
        }
        if (source == 0) {
            tv_source.setText("扫一扫添加");
        }
        if (friend) {
            initToolbar(true, true, true);
        } else {
            initToolbar(true, true, false);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (btn_start.getText().equals("发消息")) {

                }
                if (btn_start.getText().equals("加好友")) {
                    showLoadingDialog();
                    getPresenter().addFriendMsg();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess() {
        dismissDialog();
        showToast("发送成功");
        finish();
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
    }

    @Override
    public String getName() {
        String local_info = EasySP.init(this).getString(Constant.SPKey_info(this));
        LoginBean bean = GsonUtil.GsonToBean(local_info, LoginBean.class);
        return bean.getUsername();
    }

    @Override
    public String getFromId() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public String getToId() {
        return uid;
    }

    @Override
    public String getPid() {
        String from_id = EasySP.init(this).getString(Constant.SPKey_UID);
        return OfTenUtils.getConviction(from_id, uid);
    }
}
