package com.learn.agg.act;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.client.result.ParsedResultType;
import com.learn.agg.R;
import com.learn.agg.base.BaseActivity;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.white.easysp.EasySP;

public class QREncodeActivity extends BaseActivity {


    private RoundedImageView round_icon;
    private TextView tv_name;
    private TextView tv_mobile;
    private ImageView iv_qre_code;
    private String mobile;
    private boolean isFinish;

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_q_r_encode;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar(true,true,false);
        initToolbar("我的二维码");
        round_icon = findViewById(R.id.round_icon);
        tv_name = findViewById(R.id.tv_name);
        tv_mobile = findViewById(R.id.tv_mobile);
        iv_qre_code = findViewById(R.id.iv_qre_code);
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getIntent().getBundleExtra("bundle");
        isFinish = bundle.getBoolean("isFinish");
        String s = EasySP.init(this).getString(Constant.SPKey_info(this));
        LoginBean bean = GsonUtil.GsonToBean(s, LoginBean.class);
        String imageUrl = bean.getImageUrl();
        String username = bean.getUsername();
        mobile = bean.getMobile();
        Glide.with(this).load(imageUrl).into(round_icon);
        tv_name.setText(username);
        tv_mobile.setText("账号:"+ mobile);
        getQRECode();
    }

    private void getQRECode() {
        String content = Utils.getAppProcessName(this)+":"+mobile;
        Bitmap bitmap = QREncode.encodeQR(new QREncode.Builder(this)
                //二维码颜色
                .setColor(getResources().getColor(R.color.black))
                //二维码类型
                .setParsedResultType(ParsedResultType.TEXT)
                //二维码内容
                .setContents(content)
                .build());
        iv_qre_code.setImageBitmap(bitmap);
    }
}
