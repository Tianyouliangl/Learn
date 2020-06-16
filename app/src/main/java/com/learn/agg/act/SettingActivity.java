package com.learn.agg.act;

import android.view.View;
import android.widget.Button;

import com.learn.agg.R;
import com.learn.agg.base.BaseActivity;
import com.learn.agg.util.ActivityUtil;
import com.learn.commonalitylibrary.Constant;
import com.lib.xiangxiang.im.SocketManager;
import com.white.easysp.EasySP;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_exit;

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar(true,true,false);
        initToolbar("设置");
        btn_exit = findViewById(R.id.btn_exit);
    }


    @Override
    protected void initData() {
        super.initData();
        btn_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_exit:
                exitApp();
                break;
            default:
                break;
        }
    }

    public void exitApp(){
        SocketManager.logOutSocket(this);
        EasySP.init(this).put(Constant.SPKey_info(this),"");
        EasySP.init(this).put(Constant.SPKey_phone(this),"");
        EasySP.init(this).put(Constant.SPKey_pwd(this),"");
        goActivity(LoginActivity.class);
        ActivityUtil.getInstance().finishAllActivityExcept(LoginActivity.class);
    }
}
