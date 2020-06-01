package com.learn.agg.msg.act;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebear.keyboard.CBEmoticonsKeyBoard;
import com.codebear.keyboard.data.AppFuncBean;
import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.widget.CBAppFuncView;
import com.codebear.keyboard.widget.CBEmoticonsView;
import com.codebear.keyboard.widget.FuncLayout;
import com.codebear.keyboard.widget.RecordIndicator;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.adapter.ChatAdapter;
import com.learn.agg.msg.contract.BaseChatContract;
import com.learn.agg.msg.presenter.BaseChatPresenter;
import com.learn.agg.net.bean.LoginBean;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.OfTenUtils;
import com.lib.xiangxiang.im.SocketManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseChatActivity extends BaseMvpActivity<BaseChatContract.IPresenter> implements BaseChatContract.IView, View.OnLayoutChangeListener, FuncLayout.OnFuncKeyBoardListener, View.OnClickListener, RecordIndicator.OnRecordListener, CBEmoticonsView.OnEmoticonClickListener, CBAppFuncView.OnAppFuncClickListener, View.OnTouchListener, OnRefreshListener, SocketManager.SendMsgCallBack {


    private RecyclerView mRecyclerView;
    private ChatAdapter chatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
    private CBEmoticonsKeyBoard mKbView;
    private LoginBean from_bean;
    private int OPTION_TYPE_CARD = 1;
    private int OPTION_TYPE_IMAGE = 2;
    private int OPTION_TYPE_LOCATION = 3;
    private int pageNo = 1;
    private int pageSize = 30;
    private LoginBean to_bean;
    private String conviction;
    private SmartRefreshLayout mSmart;
    private LinearLayoutManager layoutManager;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mKbView != null) {
            mKbView.reset();
        }
        return false;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (chatAdapter.getData().size() > 0) {
            pageNo++;
        }
        getPresenter().getHistory(pageNo, pageSize);
    }

    @Override
    public void call(String msg) {
        ChatMessage bean = GsonUtil.GsonToBean(msg, ChatMessage.class);
        chatAdapter.notifyChatMessage(bean);
    }

    public interface key {
        String KEY_FROM = "key_from_bean";
        String KEY_TO = "key_to_bean";
    }

    @Override
    protected Boolean isRequestMission() {
        return true;
    }


    @NotNull
    @Override
    public Class<? extends BaseChatContract.IPresenter> registerPresenter() {
        return BaseChatPresenter.class;
    }

    protected void initRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnLayoutChangeListener(this);
    }

    protected void initKeyBoard(CBEmoticonsKeyBoard keyBoard) {
        mKbView = keyBoard;
        mKbView.addOnFuncKeyBoardListener(this);
        mKbView.getBtnSend().setOnClickListener(this);
        RecordIndicator recordIndicator = new RecordIndicator(this);
        mKbView.setRecordIndicator(recordIndicator);
        recordIndicator.setOnRecordListener(this);
        recordIndicator.setMaxRecordTime(60);
        CBEmoticonsView cbEmoticonsView = new CBEmoticonsView(this);
        cbEmoticonsView.init(getSupportFragmentManager());
        mKbView.setEmoticonFuncView(cbEmoticonsView);
        cbEmoticonsView.addEmoticonsWithName(new String[]{"emoji"});
        cbEmoticonsView.setOnEmoticonClickListener(this);
    }

    protected void initOptions() {
        List<AppFuncBean> appFuncBeanList = new ArrayList<>();
        appFuncBeanList.add(new AppFuncBean(OPTION_TYPE_IMAGE, R.mipmap.ic_launcher_round, "图片"));
        appFuncBeanList.add(new AppFuncBean(OPTION_TYPE_LOCATION, R.mipmap.ic_launcher_round, "位置"));
        appFuncBeanList.add(new AppFuncBean(OPTION_TYPE_CARD, R.mipmap.ic_launcher_round, "名片"));
        CBAppFuncView appFuncView = new CBAppFuncView(this);
        appFuncView.setRol(3);
        appFuncView.setAppFuncBeanList(appFuncBeanList);
        mKbView.setAppFuncView(appFuncView);
        appFuncView.setOnAppFuncClickListener(this);
    }


    protected void initSmartRefresh(SmartRefreshLayout smartRefreshLayout) {
        mSmart = smartRefreshLayout;
        smartRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        from_bean = (LoginBean) intent.getSerializableExtra(key.KEY_FROM);
        to_bean = (LoginBean) intent.getSerializableExtra(key.KEY_TO);
        chatAdapter.setFromUserBean(from_bean);
        chatAdapter.setToUserBean(to_bean);
        conviction = OfTenUtils.getConviction(from_bean.getUid(), to_bean.getUid());
        getPresenter().getHistory(pageNo, pageSize);
    }

    @Override
    public String getUid() {
        return from_bean.getUid();
    }

    @Override
    public String getConversation() {
        return conviction;
    }

    @Override
    public void onSuccess(List<ChatMessage> list) {
        mSmart.finishRefresh();
        if (list.size() > 0) {
            chatAdapter.setData(list);
            layoutManager.scrollToPositionWithOffset(list.size(), 0);
        }
        if (pageNo == 1) {
            toLastItem();
        }
    }

    @Override
    public void onSuccess() {
        mSmart.finishRefresh();
        if (pageNo > 1) {
            pageNo--;
        }
    }

    @Override
    public void onError(String msg) {
        mSmart.finishRefresh();
        showToast(msg);
    }


    private void toLastItem() {
        chatAdapter.notifyItemChanged(chatAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom < oldBottom) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (chatAdapter.getItemCount() > 0) {
                        mRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }
            });
        }
    }

    @Override
    public void onFuncPop(int height) {

    }

    @Override
    public void onFuncClose() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String text = mKbView.getEtChat().getText().toString().trim();
                mKbView.getEtChat().setText("");
                sendText(text);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void EventBus(ChatMessage msg) {
        chatAdapter.setData(msg);
        toLastItem();
    }

    private void sendText(String text) {
        if (!to_bean.getUid().isEmpty() && !from_bean.getUid().isEmpty()){
            String body = ImSendMessageUtils.getTextBody(text);
            String json = ImSendMessageUtils.getChatMessageText(body, from_bean.getUid(), to_bean.getUid(), conviction, chatAdapter.getLastItemDisplayTime());
            ChatMessage message = GsonUtil.GsonToBean(json, ChatMessage.class);
            chatAdapter.setData(message);
            toLastItem();
            SocketSendJson(json);
        }
    }

    private void SocketSendJson(String json) {
        SocketManager.sendMsgSocket(this, json, this);
    }

    // 语音
    @Override
    public void recordStart() {

    }

    @Override
    public void recordFinish() {

    }

    @Override
    public void recordCancel() {

    }

    @Override
    public long getRecordTime() {
        return 0;
    }

    @Override
    public int getRecordDecibel() {
        return 0;
    }

    // 表情点击事件
    @Override
    public void onEmoticonClick(EmoticonsBean emoticon, boolean isDel) {

    }

    // 发送图片 等等
    @Override
    public void onAppFunClick(AppFuncBean emoticon) {
        int emoticonId = emoticon.getId();
        if (emoticonId == OPTION_TYPE_IMAGE) {

        }
        if (emoticonId == OPTION_TYPE_LOCATION) {

        }
        if (emoticonId == OPTION_TYPE_CARD) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
