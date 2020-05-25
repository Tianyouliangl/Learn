package com.learn.agg.msg.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gongwen.marqueen.SimpleMF;
import com.gongwen.marqueen.SimpleMarqueeView;
import com.gongwen.marqueen.util.OnItemClickListener;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.msg.act.FriendActivity;
import com.learn.agg.msg.act.NewFriendActivity;
import com.learn.agg.msg.contract.MessageContract;
import com.learn.agg.msg.presenter.MessagePresenter;
import com.learn.agg.widgets.BadgeView;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.lib.xiangxiang.im.ImService;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MessageFragment extends BaseMvpFragment<MessageContract.IPresenter> implements MessageContract.IView, View.OnClickListener, OnItemClickListener, OnRefreshListener {

    private List<String> sList = new ArrayList<>();
    private RecyclerView rl_message;
    private BadgeView rl_bv;
    private SimpleMarqueeView simpleMarqueeView;
    private RelativeLayout rl_find_friend;
    private SmartRefreshLayout smart_layout;
    private Bitmap cirleBitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @NotNull
    @Override
    public Class<? extends MessageContract.IPresenter> registerPresenter() {
        return MessagePresenter.class;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar(true, true, true);
        setToolBarIcon();
        rl_message = view.findViewById(R.id.rl_message);
        rl_bv = view.findViewById(R.id.rl_bv);
        smart_layout = view.findViewById(R.id.msg_sl);
        simpleMarqueeView = view.findViewById(R.id.simpleMarqueeView);
        rl_find_friend = view.findViewById(R.id.rl_find_friend);
    }

    private void setToolBarIcon() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String image_url = EasySP.init(getContext()).getString(Constant.SPKey_icon(getContext()));
                try {
                    URL url = new URL(image_url);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                    cirleBitmap = NotificationUtils.getCirleBitmap(bitmap);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initToolbar(cirleBitmap,"消息", "联系人", MessageFragment.this);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void initData() {
        super.initData();
        rl_message.setLayoutManager(new LinearLayoutManager(getContext()));
        rl_bv.showBadge(sList.size());
        simpleMarqueeView.setOnItemClickListener(this);
        rl_find_friend.setOnClickListener(this);
        smart_layout.setOnRefreshListener(this);
//        smart_layout.autoRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().getAddFriendMsg();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopFlipping();
        } else {
            getPresenter().getAddFriendMsg();
            startFlipping();
        }
    }

    public void startFlipping() {
        if (simpleMarqueeView != null) {
            SimpleMF<String> marqueeFactory = new SimpleMF(getContext());
            marqueeFactory.setData(sList);
            simpleMarqueeView.setMarqueeFactory(marqueeFactory);
            simpleMarqueeView.startFlipping();
        }
    }

    public void stopFlipping() {
        simpleMarqueeView.stopFlipping();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                showToast("二维码");

                break;
            case R.id.toolbar_action:
                goActivity(FriendActivity.class);
                break;
            case R.id.rl_find_friend:
                goActivity(NewFriendActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClickListener(View mView, Object mData, int mPosition) {
        goActivity(NewFriendActivity.class);
    }

    @Override
    public String getUid() {
        return EasySP.init(getActivity()).getString(Constant.SPKey_UID);
    }

    @Override
    public void onSuccess(List<String> list) {
        smart_layout.finishRefresh();
        HashMap<String, Object> map = new HashMap<>();
        map.put("FragmentName",MessageFragment.class.getSimpleName());
        if (list != null && list.size() > 0) {
            simpleMarqueeView.setVisibility(View.VISIBLE);
            stopFlipping();
            sList.clear();
            sList.addAll(list);
            startFlipping();
            rl_bv.showBadge(list.size());
            map.put("count",list.size());
        } else {
            simpleMarqueeView.setVisibility(View.GONE);
            rl_bv.showBadge(0);
            map.put("count",0);
        }
        EventBus.getDefault().postSticky(map);
    }

    @Override
    public void onError() {
        smart_layout.finishRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getPresenter().getAddFriendMsg();
    }
}
