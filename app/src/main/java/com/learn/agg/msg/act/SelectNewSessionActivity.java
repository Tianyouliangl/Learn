package com.learn.agg.msg.act;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.adapter.ForWardingUserAdapter;
import com.learn.agg.msg.adapter.SelectNewSessionAdapter;
import com.learn.agg.msg.adapter.SessionNewAdapter;
import com.learn.agg.msg.contract.SelectNewSessionContract;
import com.learn.agg.msg.presenter.SelectNewSessionPresenter;
import com.learn.agg.util.ActivityUtil;
import com.learn.agg.widgets.CustomDialog;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.SessionNewBean;
import com.learn.commonalitylibrary.body.EmoticonBody;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.TextBody;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.OfTenUtils;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.lib.xiangxiang.im.ImSocketClient;
import com.lib.xiangxiang.im.SocketManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class SelectNewSessionActivity extends BaseMvpActivity<SelectNewSessionContract.IPresenter> implements SelectNewSessionContract.IView, SessionNewAdapter.SessionNewInterface, View.OnClickListener,SocketManager.SendMsgCallBack {

    private SessionNewAdapter adapter;
    private RecyclerView rl_session_new;
    private Button btn_send;
    private ChatMessage chatMessage;
    private SelectNewSessionAdapter forWardingUserAdapter;
    private CustomDialog forwarding_dialog;
    private RecyclerView rl_dialog_forwarding;
    private RoundedImageView riv_to;
    private int send_count = 0;
    private TextView tv_progress;
    private Map<String,String> map_conversation = new HashMap<>();
    private RoundedImageView riv_from;
    private View view_forwarding;
    private RelativeLayout rl_content;

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_new_session;
    }

    @Override
    protected void initView() {
        initToolbar(true,true,false);
        initToolbar("选择联系人");
        rl_session_new = findViewById(R.id.rl_session_new);
        btn_send = findViewById(R.id.btn_send);
        rl_session_new.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        showLoadingDialog();
        createForwardingDialog();
        Bundle bundle = getIntent().getBundleExtra("bundle");
        chatMessage = (ChatMessage) bundle.getSerializable("msg");
        adapter = new SessionNewAdapter(this, new ArrayList<SessionNewBean>());
        rl_session_new.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        btn_send.setOnClickListener(this);
        getPresenter().getAllFriend();
    }

    @NotNull
    @Override
    public Class<? extends SelectNewSessionContract.IPresenter> registerPresenter() {
        return SelectNewSessionPresenter.class;
    }

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public void onSuccess(List<SessionNewBean> list) {
        dismissDialog();
        adapter.setData(list);
    }

    @Override
    public void onSuccessConversation(String id, String conversation,int type) {
        Iterator<Map.Entry<String, String>> iterator = map_conversation.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            if (next.getKey().equals(id)){
                iterator.remove();
            }
        }
        map_conversation.put(id,conversation);
        if (type == 1){
            if (map_conversation.size() == adapter.getCheckedSize().size()){
                socketForwardingMsg(adapter.getCheckedSize());
            }
        }
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public SocketManager.SendMsgCallBack callBack() {
        return this;
    }

    @Override
    public void onItemClick(SessionNewBean bean, int position) {
        Boolean check = bean.getCheck();
        if (check){
            getPresenter().getConversation(bean.getUid(),0);
        }else {
            map_conversation.remove(bean.getUid());
        }
    }

    private void createForwardingDialog() {
        forWardingUserAdapter = new SelectNewSessionAdapter(new ArrayList<SessionNewBean>(), this);
        forwarding_dialog = new CustomDialog(this, true,R.layout.dialog_forwarding);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        view_forwarding = forwarding_dialog.getView();
        forwarding_dialog.setCancelable(false);
        forwarding_dialog.setCanceledOnTouchOutside(true);
        TextView tv_cancel = view_forwarding.findViewById(R.id.tv_cancel);
        TextView tv_send = view_forwarding.findViewById(R.id.tv_send);
        rl_dialog_forwarding = view_forwarding.findViewById(R.id.rl_dialog_forwarding);
        rl_content = view_forwarding.findViewById(R.id.rl_content);
        rl_dialog_forwarding.setLayoutManager(layoutManager);
        rl_dialog_forwarding.setAdapter(forWardingUserAdapter);
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    private void setContent(RelativeLayout rl_content) {
        int bodyType = chatMessage.getBodyType();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT || bodyType == ChatMessage.MSG_BODY_TYPE_TEXT_HELLO){
            String body = chatMessage.getBody();
            TextBody textBody = GsonUtil.GsonToBean(body, TextBody.class);
            TextView textView = new TextView(this);
            textView.setText(textBody.getMsg());
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            rl_content.addView(textView);
        }

        if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE){
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

        if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI){
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

    private void showForwardingDialog(List<SessionNewBean> select_list) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                dismissForwardingDialog();
                break;
            case R.id.tv_send:
                final List<SessionNewBean> newBeans = adapter.getCheckedSize();
                Logger.t(ImSocketClient.TAG).i("conversationSize:" + map_conversation.size() + " =====  checkSize:" + newBeans.size());
                if (newBeans.size() <= 0)return;
                if (map_conversation.size() != newBeans.size()){
                    for (int i=0;i<newBeans.size();i++){
                        getPresenter().getConversation(newBeans.get(i).getUid(),1);
                    }
                    return;
                }
                View view_tv_send = LayoutInflater.from(this).inflate(R.layout.dialog_send_msg, null, false);
                forwarding_dialog.setContentView(view_tv_send);
                riv_from = view_tv_send.findViewById(R.id.riv_from);
                riv_to = view_tv_send.findViewById(R.id.riv_to);
                tv_progress = view_tv_send.findViewById(R.id.tv_progress);
                Glide.with(SelectNewSessionActivity.this).load(adapter.getCheckedSize().get(send_count).getImageUrl()).into(riv_to);
                tv_progress.setText(0+"/"+newBeans.size());
                String url = EasySP.init(this).getString(Constant.SPKey_icon(this));
                Glide.with(this).load(url).into(riv_from);
                socketForwardingMsg(newBeans);
                break;
            case R.id.btn_send:
                List<SessionNewBean> checkedSize = adapter.getCheckedSize();
                if (checkedSize.size() <= 0){
                    dismissForwardingDialog();
                    return;
                }
                setContent(rl_content);
                showForwardingDialog(checkedSize);
                break;
            default:
                break;
        }
    }

    private void socketForwardingMsg(List<SessionNewBean> newBeans) {
        for (int i = 0; i < newBeans.size();i++){
            ChatMessage  message = new ChatMessage();
            SessionNewBean newBean = newBeans.get(i);
            String conviction = map_conversation.get(newBean.getUid());
            if (conviction.isEmpty() || conviction == null)return;
            List<ChatMessage> list = DataBaseHelp.getInstance(this).getChatMessage(conviction, 1, 30);
            if (list.size() > 0){
                Long time = list.get(list.size() - 1).getTime();
                Boolean expend = TimeUtil.getTimeExpend(System.currentTimeMillis(), time);
                if (expend){
                    message.setDisplaytime(ChatMessage.MSG_TIME_FALSE);
                }else {
                    message.setDisplaytime(ChatMessage.MSG_TIME_TRUE);
                }
            }else {
                message.setDisplaytime(ChatMessage.MSG_TIME_TRUE);
            }
            message.setBodyType(this.chatMessage.getBodyType());
            message.setBody(this.chatMessage.getBody());
            message.setMsgStatus(ChatMessage.MSG_SEND_LOADING);
            message.setType(this.chatMessage.getType());
            message.setTime(System.currentTimeMillis());
            message.setPid(ImSendMessageUtils.getPid());
            message.setToId(newBean.getUid());
            message.setFromId(getUid());
            message.setConversation(conviction);
            Logger.t(ImSocketClient.TAG).i("转发消息对象:" + newBean.getUsername() +" === conversation:"+ conviction);
            getPresenter().SocketSendJson(GsonUtil.BeanToJson(message),true);
        }
    }

    @Override
    public void call(String msg) {
        final List<SessionNewBean> newBeans = adapter.getCheckedSize();
        int size = newBeans.size();
        send_count +=1;
        if (send_count >= size){
            dismissForwardingDialog();
            showToast("发送成功");
            ActivityUtil.getInstance().finishActivity(ForWardingActivity.class);
            finish();
            return;
        }

        Log.i("socket","---call---" + send_count);
        if (send_count < newBeans.size()){
            Glide.with(SelectNewSessionActivity.this).load(adapter.getCheckedSize().get(send_count).getImageUrl()).into(riv_to);
            tv_progress.setText(send_count+"/"+newBeans.size());
        }
    }
}
