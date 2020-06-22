package com.learn.agg.msg.act;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.adapter.FriendAdapter;
import com.learn.agg.msg.contract.FriendContract;
import com.learn.agg.msg.presenter.FriendPresenter;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendActivity extends BaseMvpActivity<FriendContract.IPresenter> implements FriendContract.IView, OnRefreshListener, FriendAdapter.friendInterface {


    private RecyclerView rl_friend;
    private FriendAdapter adapter;
    private SmartRefreshLayout smart_layout;

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend;
    }

    @NotNull
    @Override
    public Class<? extends FriendContract.IPresenter> registerPresenter() {
        return FriendPresenter.class;
    }

    @Override
    protected void initView() {
        initToolbar(true,true,true);
        initToolbar("联系人", "添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(NewFriendActivity.class);
            }
        });
        EventBus.getDefault().register(this);
        rl_friend = findViewById(R.id.rl_friend);
        smart_layout = findViewById(R.id.start_layout);
        smart_layout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        rl_friend.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendAdapter(this, new ArrayList<LoginBean>());
        adapter.setOnClickItemListener(this);
        rl_friend.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().getAllFriend();
    }

    //黏性事件的 订阅   

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void MessageEvent(HashMap<String, Object> map) {
        String name = (String) map.get("notification");
        if (name != null && !name.isEmpty()){
            if (name.equals("change")){
                getPresenter().getAllFriend();
            }
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getPresenter().getAllFriend();
    }

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public void onSuccess(List<LoginBean> list) {
        smart_layout.finishRefresh();
        adapter.setData(list);
    }

    @Override
    public void onError(String msg) {
        smart_layout.finishRefresh();
    }

    @Override
    public void onItemClick(LoginBean bean) {
        String json = GsonUtil.BeanToJson(bean);
        Bundle bundle = new Bundle();
        bundle.putString("data", json);
        bundle.putBoolean("isGone", false);
        Intent intent = new Intent(this, FriendInfoActivity.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
