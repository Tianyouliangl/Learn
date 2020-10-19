package com.learn.agg.msg.act;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;
import com.learn.agg.act.ScannerActivity;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.adapter.FriendMsgAdapter;
import com.learn.agg.msg.contract.NewFriendContract;
import com.learn.agg.msg.presenter.NewFriendPresenter;
import com.learn.agg.net.bean.FriendMsgBean;
import com.learn.commonalitylibrary.Constant;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NewFriendActivity extends BaseMvpActivity<NewFriendContract.IPresenter> implements NewFriendContract.IView, View.OnClickListener, OnRefreshListener, FriendMsgAdapter.FriendMsgInterface {

    private SmartRefreshLayout smart_layout;
    private RecyclerView rv_new_friend;
    private FriendMsgAdapter adapter;
    private RelativeLayout rl_zh;
    private RelativeLayout rl_sao;

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_friend;
    }

    @Override
    protected void initView() {
        initToolbar(true, true, false);
        initToolbar("新朋友");
        smart_layout = findViewById(R.id.smart_layout);
        rv_new_friend = findViewById(R.id.rv_new_friend);
        rl_zh = findViewById(R.id.rl_add_zh);
        rl_sao = findViewById(R.id.rl_add_scanner);
    }

    @Override
    protected void initData() {
        rv_new_friend.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendMsgAdapter(this, new ArrayList<FriendMsgBean>());
        adapter.setOnLoadingChangListener(this);
        smart_layout.setOnRefreshListener(this);
        rv_new_friend.setAdapter(adapter);
        rl_zh.setOnClickListener(this);
        rl_sao.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().getAllFriendMsg();
    }

    @NotNull
    @Override
    public Class<? extends NewFriendContract.IPresenter> registerPresenter() {
        return NewFriendPresenter.class;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_zh:
                goActivity(FindFriendActivity.class);
                break;
            case R.id.rl_add_scanner:
                goActivity(ScannerActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getPresenter().getAllFriendMsg();
    }

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public void onSuccess(List<FriendMsgBean> list) {
        dismissDialog();
        smart_layout.finishRefresh();
        adapter.setData(list);
    }

    @Override
    public void onSuccessDisposeFriend() {
        dismissDialog();
        getPresenter().getAllFriendMsg();
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
        smart_layout.finishRefresh();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void dismissLoading() {
        dismissDialog();
    }

    @Override
    public void onClickReject(FriendMsgBean bean) {
        getPresenter().setFriend(this,bean.getTo_id(), bean.getFrom_id(), bean.getPid(), 1, 1);
    }

    @Override
    public void onClickAgree(FriendMsgBean bean) {
        getPresenter().setFriend(this,bean.getTo_id(), bean.getFrom_id(), bean.getPid(), 0, 1);
    }
}
