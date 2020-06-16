package com.learn.agg.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.act.QREncodeActivity;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.base.IconOnClickListener;
import com.learn.agg.fragment.contract.MenuLeftContract;
import com.learn.agg.fragment.presenter.MenuLeftPresenter;
import com.learn.agg.msg.act.FriendInfoActivity;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class MenuLeftFragment extends BaseMvpFragment<MenuLeftContract.IPresenter> implements MenuLeftContract.IView, View.OnClickListener {

    private RoundedImageView iv_round;
    private TextView tv_name;
    private TextView tv_sign;
    private ImageView iv_close;
    private TextView tv_money;
    private RelativeLayout rl_info;
    private String json;
    private TextView tv_code;
    private ImageView iv_code;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_menu_left;
    }

    @NotNull
    @Override
    public Class<? extends MenuLeftContract.IPresenter> registerPresenter() {
        return MenuLeftPresenter.class;
    }

    @Override
    protected void initView() {
        super.initView();
        rl_info = view.findViewById(R.id.rl_info);
        iv_round = view.findViewById(R.id.iv_round);
        tv_name = view.findViewById(R.id.tv_name);
        tv_sign = view.findViewById(R.id.tv_sign);
        iv_close = view.findViewById(R.id.iv_close);
        tv_money = view.findViewById(R.id.tv_money);
        tv_code = view.findViewById(R.id.tv_code);
        iv_code = view.findViewById(R.id.iv_code);
        iv_close.setOnClickListener(this);
        rl_info.setOnClickListener(this);
        iv_code.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        json = EasySP.init(getContext()).getString(Constant.SPKey_info(getContext()));
        LoginBean bean = GsonUtil.GsonToBean(json, LoginBean.class);
        String imageUrl = bean.getImageUrl();
        String username = bean.getUsername();
        BigDecimal money = bean.getMoney();
        String sign = bean.getSign();
        Glide.with(getContext()).load(imageUrl).into(iv_round);
        tv_name.setText(username);
        tv_sign.setText(sign);
        tv_money.setText("余额:¥"+money.toString());
        tv_code.setText("版本号:"+getVersionName(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (getActivity() instanceof IconOnClickListener) {
                    ((IconOnClickListener) getActivity()).closeMenu();
                }
                break;
            case R.id.rl_info:
                Bundle bundle = new Bundle();
                bundle.putString("data", json);
                bundle.putBoolean("isGone", true);
                Intent intent = new Intent(getActivity(), FriendInfoActivity.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
            case R.id.iv_code:
                Bundle bundleCode = new Bundle();
                bundleCode.putBoolean("isFinish",false);
                goActivity(QREncodeActivity.class,bundleCode);
                break;
            default:
                break;
        }
    }
}
