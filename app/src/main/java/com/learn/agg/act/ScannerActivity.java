package com.learn.agg.act;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.learn.agg.R;
import com.learn.agg.act.contract.ScannerContract;
import com.learn.agg.act.presenter.ScannerPresenter;
import com.learn.agg.base.BaseActivity;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.act.FriendInfoActivity;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.Utils;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

public class ScannerActivity extends BaseMvpActivity<ScannerContract.IPresenter> implements ScannerContract.IView, OnScannerCompletionListener, View.OnClickListener {

    private ScannerView scanner_view;
    private String mobile;
    private ImageView iv_back;
    private TextView tv_code;

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scanner;
    }

    @Override
    protected void initView() {
        setDialogBack(false);
        scanner_view = findViewById(R.id.scanner_view);
        iv_back = findViewById(R.id.iv_back);
        tv_code = findViewById(R.id.tv_code);
        scanner_view.setLaserFrameSize(250,250);
        scanner_view.setLaserFrameCornerLength(20);
        scanner_view.setLaserFrameCornerWidth(3);
        scanner_view.setDrawText("将二维码放入框内，即可扫描",14,0xFFFFFFFF,true,40);
        scanner_view.setLaserGridLineResId(R.mipmap.saoyisao_line);//网格图
        scanner_view.setLaserFrameBoundColor(0xFF26CEFF);
    }

    @Override
    protected void initData() {
        scanner_view.setOnScannerCompletionListener(this);
        iv_back.setOnClickListener(this);
        tv_code.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        scanner_view.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        scanner_view.onPause();
        super.onPause();
    }

    @Override
    public void OnScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
        if (rawResult == null){

        }else {
            showLoadingDialog();
            String pageName = Utils.getAppProcessName(this)+":";
            String text = rawResult.getText();
            if (text.startsWith(pageName)){
                String phone = text.substring(pageName.length());
                mobile = phone;
                getPresenter().getUserInfo();
            }
        }
    }

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
    }

    @Override
    public void onSuccess(String json) {
        dismissDialog();
        Bundle bundle = new Bundle();
        bundle.putString("data",json);
        goActivity(FriendInfoActivity.class,bundle);
        finish();
    }

    @Override
    public void onSuccessNull() {
        dismissDialog();
    }

    @NotNull
    @Override
    public Class<? extends ScannerContract.IPresenter> registerPresenter() {
        return ScannerPresenter.class;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_code:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFinish",true);
                goActivity(QREncodeActivity.class,bundle);
                break;
            default:
                break;
        }
    }
}
