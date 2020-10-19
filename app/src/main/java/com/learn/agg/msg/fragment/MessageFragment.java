package com.learn.agg.msg.fragment;


import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gongwen.marqueen.SimpleMF;
import com.gongwen.marqueen.SimpleMarqueeView;
import com.gongwen.marqueen.util.OnItemClickListener;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.msg.act.FriendActivity;
import com.learn.agg.msg.act.NewFriendActivity;
import com.learn.agg.msg.adapter.SessionsAdapter;
import com.learn.agg.msg.contract.MessageContract;
import com.learn.agg.msg.presenter.MessagePresenter;
import com.learn.agg.widgets.BadgeView;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.lib.xiangxiang.im.ImSocketClient;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
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
import java.util.Iterator;
import java.util.List;


public class MessageFragment extends BaseMvpFragment<MessageContract.IPresenter> implements MessageContract.IView, View.OnClickListener, OnItemClickListener, OnRefreshListener, View.OnTouchListener, SessionsAdapter.MenuClickListener {

    private List<String> sList = new ArrayList<>();
    private RecyclerView rl_message;
    private BadgeView rl_bv;
    private SimpleMarqueeView simpleMarqueeView;
    private RelativeLayout rl_find_friend;
    private SmartRefreshLayout smart_layout;
    private SessionsAdapter sessionsAdapter;
    private CardView cv;
    private RelativeLayout rl_no_msg;

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
        EventBus.getDefault().register(this);
        rl_message = view.findViewById(R.id.rl_message);
        rl_bv = view.findViewById(R.id.rl_bv);
        smart_layout = view.findViewById(R.id.msg_sl);
        simpleMarqueeView = view.findViewById(R.id.simpleMarqueeView);
        rl_find_friend = view.findViewById(R.id.rl_find_friend);
        cv = view.findViewById(R.id.cv);
        rl_no_msg = view.findViewById(R.id.rl_no_msg);
    }

    @Override
    protected void initData() {
        super.initData();
        sessionsAdapter = new SessionsAdapter(getContext(), new ArrayList<SessionMessage>());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rl_message.setLayoutManager(manager);
        rl_message.setAdapter(sessionsAdapter);
        rl_bv.showBadge(sList.size());
        simpleMarqueeView.setOnItemClickListener(this);
        rl_find_friend.setOnClickListener(this);
        smart_layout.setOnRefreshListener(this);
        rl_message.setOnTouchListener(this);
        sessionsAdapter.setOnMenuClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().getAddFriendMsg();
        getPresenter().getSessionList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopFlipping();
            Boolean isOpenMenu = sessionsAdapter.getIsOpenMenu();
            if (isOpenMenu) {
                SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                if (null != viewCache) {
                    viewCache.smoothClose();
                }
            }
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
        sList.clear();
        if (list != null && list.size() > 0) {
            cv.setVisibility(View.VISIBLE);
            rl_find_friend.setVisibility(View.VISIBLE);
            simpleMarqueeView.setVisibility(View.VISIBLE);
            stopFlipping();
            sList.addAll(list);
            startFlipping();
            rl_bv.showBadge(list.size());
        } else {
            cv.setVisibility(View.GONE);
            rl_find_friend.setVisibility(View.GONE);
            simpleMarqueeView.setVisibility(View.GONE);
            rl_bv.showBadge(0);
        }
        sendNumberCount();
    }

    @Override
    public void onSession(List<SessionMessage> list) {
        showEmpty(list.size());
        sessionsAdapter.setData(list);
        sendNumberCount();
    }

    public void sendNumberCount() {
        Log.i("chat", "发送数量");
        int n = 0;
        List<SessionMessage> list = sessionsAdapter.getData();
        for (int i = 0; i < list.size(); i++) {
            n += list.get(i).getNumber();
        }
        n += sList.size();
        HashMap<String, Object> map = new HashMap<>();
        map.put("FragmentName", MessageFragment.class.getSimpleName());
        map.put("count", n);
        EventBus.getDefault().post(map);
    }

    @Nullable
    @Override
    public Context getContext() {
        return getActivity();
    }

    //事件的 订阅 
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void MessageEvent(HashMap<String, Object> map) {
        String name = (String) map.get("notification");
        if (name != null && !name.isEmpty()) {
            if (name.equals("change")) {
                getPresenter().getAddFriendMsg();
                EventBus.getDefault().removeStickyEvent(map);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventBus(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MSG_SEND_CHAT || chatMessage.getType() == ChatMessage.MSG_SEND_SYS) {
            getPresenter().getSessionList();
        }
        if (chatMessage.getType() == ChatMessage.MSG_ADD_FRIEND) {
            getPresenter().getAddFriendMsg();
        }

    }

    @Override
    public void onError() {
        smart_layout.finishRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getPresenter().getAddFriendMsg();
        getPresenter().getSessionList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.rl_message) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                Boolean isOpenMenu = sessionsAdapter.getIsOpenMenu();
                if (isOpenMenu) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onDeleteListener(int position) {
        SessionMessage message = sessionsAdapter.getData().get(position);
        String conversation = message.getConversation();
        if (conversation != null) {
            Iterator<SessionMessage> it = sessionsAdapter.getData().iterator();
            while (it.hasNext()) {
                SessionMessage next = it.next();
                String nextConversation = next.getConversation();
                if (nextConversation != null) {
                    if (conversation.hashCode() == nextConversation.hashCode()) {
                        DataBaseHelp.getInstance(getContext()).deleteSessionConversation(next.getConversation());
                        it.remove();
                        sendNumberCount();
                    }
                }
            }
            sessionsAdapter.notifyDataSetChanged();
            showEmpty(sessionsAdapter.getData().size());
        }
    }

    @Override
    public void onRedListener() {
        getPresenter().getSessionList();
    }

    private void showEmpty(int size) {
        if (size > 0) {
            rl_no_msg.setVisibility(View.GONE);
            rl_message.setVisibility(View.VISIBLE);
        } else {
            rl_no_msg.setVisibility(View.VISIBLE);
            rl_message.setVisibility(View.GONE);
        }
    }
}
