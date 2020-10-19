package com.learn.agg.msg.act;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.adapter.ForWardingSessionAdapter;
import com.learn.agg.msg.adapter.ForWardingUserAdapter;
import com.learn.agg.msg.contract.ForWardingMessageContract;
import com.learn.agg.msg.presenter.ForWardingMessagePresenter;
import com.learn.agg.widgets.CustomDialog;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.body.EmoticonBody;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.TextBody;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.OfTenUtils;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.lib.xiangxiang.im.SocketManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ForWardingActivity extends BaseMvpActivity<ForWardingMessageContract.IPresenter> implements ForWardingMessageContract.IView, ForWardingSessionAdapter.onClickItemInterface, View.OnClickListener, DialogInterface.OnDismissListener {

    private RecyclerView rv_forwarding_session;
    private ForWardingSessionAdapter adapter;
    private List<SessionMessage> select_list = new ArrayList<>();
    private Boolean isMC = false;
    private CustomDialog forwarding_dialog;
    private ChatMessage chatMessage;
    private RecyclerView rl_dialog_forwarding;
    private ForWardingUserAdapter forWardingUserAdapter;
    private long send_time;
    private ChatMessage message;
    private String conviction;
    private String to_id;
    private SessionMessage sessionMessage;
    private RelativeLayout rl_new_session;
    private Map<String, String> map_conversation = new HashMap<>();

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_for_warding;
    }

    @Override
    protected void initView() {
        initToolbar(true, true, true);
        initToolbar("发送给", "多选", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMC) {
                    initToolbar("发送给", "发送");
                    isMC = true;
                    adapter.setMC(isMC);
                    return;
                }
                if (isMC) {
                    if (select_list.size() <= 0) {
                        initToolbar("发送给", "多选");
                        isMC = false;
                        adapter.setMC(isMC);
                        return;
                    } else {
                        showForwardingDialog();
                    }

                }
            }
        });
        rv_forwarding_session = findViewById(R.id.rv_forwarding_session);
        rl_new_session = findViewById(R.id.rl_new_session);
        rv_forwarding_session.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        chatMessage = (ChatMessage) bundle.getSerializable("msg");
        adapter = new ForWardingSessionAdapter(new ArrayList<SessionMessage>(), this);
        rv_forwarding_session.setAdapter(adapter);
        adapter.setOnForWardingSessionItemClick(this);
        rl_new_session.setOnClickListener(this);
        createForwardingDialog();
        getPresenter().getSessionList();
    }

    private void createForwardingDialog() {
        forWardingUserAdapter = new ForWardingUserAdapter(new ArrayList<SessionMessage>(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        forwarding_dialog = new CustomDialog(this, true, R.layout.dialog_forwarding);
        View view = forwarding_dialog.getView();
        RelativeLayout rl_content = view.findViewById(R.id.rl_content);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        TextView tv_send = view.findViewById(R.id.tv_send);
        rl_dialog_forwarding = view.findViewById(R.id.rl_dialog_forwarding);
        rl_dialog_forwarding.setLayoutManager(layoutManager);
        rl_dialog_forwarding.setAdapter(forWardingUserAdapter);
        setContent(rl_content);
        forwarding_dialog.setOnDismissListener(this);
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    private void setContent(RelativeLayout rl_content) {
        int bodyType = chatMessage.getBodyType();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT || bodyType == ChatMessage.MSG_BODY_TYPE_TEXT_HELLO) {
            String body = chatMessage.getBody();
            TextBody textBody = GsonUtil.GsonToBean(body, TextBody.class);
            TextView textView = new TextView(this);
            textView.setText(textBody.getMsg());
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            rl_content.addView(textView);
        }

        if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
            String body = chatMessage.getBody();
            ImageBody imageBody = GsonUtil.GsonToBean(body, ImageBody.class);
            RoundedImageView imageView = new RoundedImageView(this);
            imageView.setCornerRadius(8);
            params.width = 400;
            params.height = 500;
            imageView.setLayoutParams(params);
            Glide.with(this).load(imageBody.getImage()).into(imageView);
            rl_content.addView(imageView);
        }

        if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI) {
            String body = chatMessage.getBody();
            EmoticonBody emoticonBody = GsonUtil.GsonToBean(body, EmoticonBody.class);
            ImageView imageView = new ImageView(this);
            params.width = 100;
            params.height = 100;
            imageView.setLayoutParams(params);
            Glide.with(this).load(emoticonBody.getUrl()).into(imageView);
            rl_content.addView(imageView);
        }
    }

    private void showForwardingDialog() {
        if (forwarding_dialog != null && !forwarding_dialog.isShowing()) {
            forWardingUserAdapter.setData(select_list);
            forwarding_dialog.show();
        }
    }

    private void dismissForwardingDialog() {
        if (forwarding_dialog != null && forwarding_dialog.isShowing()) {
            forwarding_dialog.dismiss();
        }
    }

    @NotNull
    @Override
    public Class<? extends ForWardingMessageContract.IPresenter> registerPresenter() {
        return ForWardingMessagePresenter.class;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSuccess(List<SessionMessage> list) {
        adapter.setData(list);
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public void onSuccessConversation(String con, String id) {
        map_conversation.put(id, con);
    }

    @Override
    public void onClickItem(SessionMessage message, Boolean isMC, Boolean isChecked) {
        if (isMC) {
            if (isChecked) {
                select_list.add(message);
                getPresenter().getConversation(message.getTo_id());
            } else {
                Iterator<SessionMessage> iterator = select_list.iterator();
                while (iterator.hasNext()) {
                    SessionMessage next = iterator.next();
                    if (next.getConversation().hashCode() == message.getConversation().hashCode()) {
                        iterator.remove();
                        map_conversation.remove(message.getTo_id());
                    }
                }
            }
            initToolbar("发送给", select_list.size() > 0 ? "发送(" + select_list.size() + ")" : "发送");
        } else {
            getPresenter().getConversation(message.getTo_id());
            select_list.add(message);
            showForwardingDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismissForwardingDialog();
                break;
            case R.id.tv_send:
                dismissForwardingDialog();
                String from_id = getUid();
                if (select_list.size() != map_conversation.size()) return;
                for (int i = 0; i < select_list.size(); i++) {
                    sessionMessage = select_list.get(i);
                    send_time = System.currentTimeMillis();
                    message = new ChatMessage();
                    to_id = sessionMessage.getTo_id();
                    conviction = map_conversation.get(to_id);
                    if (conviction == null) {
                        dismissForwardingDialog();
                        return;
                    }
                    List<ChatMessage> list = DataBaseHelp.getInstance(this).getChatMessage(conviction, 1, 30);
                    if (list.size() > 0) {
                        Long time = list.get(list.size() - 1).getTime();
                        Boolean expend = TimeUtil.getTimeExpend(send_time, time);
                        if (expend) {
                            message.setDisplaytime(ChatMessage.MSG_TIME_FALSE);
                        } else {
                            message.setDisplaytime(ChatMessage.MSG_TIME_TRUE);
                        }
                    } else {
                        message.setDisplaytime(ChatMessage.MSG_TIME_TRUE);
                    }
                    message.setBodyType(this.chatMessage.getBodyType());
                    message.setBody(this.chatMessage.getBody());
                    message.setMsgStatus(ChatMessage.MSG_SEND_LOADING);
                    message.setType(this.chatMessage.getType());
                    message.setTime(send_time);
                    message.setPid(ImSendMessageUtils.getPid());
                    message.setToId(to_id);
                    message.setFromId(from_id);
                    message.setConversation(conviction);
                    DataBaseHelp.getInstance(this).addChatMessage(message);
                    DataBaseHelp.getInstance(this).addOrUpdateSession(sessionMessage);
                    SocketManager.sendMsgSocket(this, GsonUtil.BeanToJson(message), new SocketManager.SendMsgCallBack() {
                        @Override
                        public void call(String msg) {

                        }
                    });
                }
                finish();
                break;
            case R.id.rl_new_session:
                Bundle bundle = new Bundle();
                bundle.putSerializable("msg", chatMessage);
                goActivity(SelectNewSessionActivity.class, bundle);
                break;
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Iterator<SessionMessage> iterator = select_list.iterator();
        while (iterator.hasNext()) {
            SessionMessage next = iterator.next();
            if (next != null) {
                iterator.remove();
                map_conversation.remove(next.getTo_id());
            }
        }
    }
}
