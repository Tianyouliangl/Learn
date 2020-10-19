package com.learn.agg.msg.presenter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learn.agg.R;
import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.BaseChatContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.lib.xiangxiang.im.SocketManager;
import com.orhanobut.logger.Logger;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseChatPresenter extends BasePresenter<BaseChatContract.IView> implements BaseChatContract.IPresenter {
    @Override
    public void getHistory(final int pageNo, int pageSize, String toid) {
        String conversation = getMvpView().getConversation();
        if (conversation.isEmpty()) {
            getMvpView().onError("参数错误:Conversation is not null");
            return;
        }
        List<ChatMessage> list = DataBaseHelp.getInstance(getMvpView().getContent()).getChatMessage(conversation, pageNo, pageSize);
        Log.i("net", "---history---size---" + list.size());
        if (list.size() > 0) {
            getMvpView().onSuccess(list);
        } else {
            if (conversation != null && !conversation.isEmpty()) {
                HashMap<String, Object> map = new HashMap<>();
                final Context content = getMvpView().getContent();
                map.put("uid", getMvpView().getTo_Id());
                map.put("conversation", conversation);
                map.put("pageNo", pageNo);
                map.put("pageSize", pageSize);
                HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                        .getHistory(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserverTC<List<ChatMessage>>() {
                            @Override
                            protected void onNextEx(@NonNull List<ChatMessage> data) {
                                super.onNextEx(data);
                                Iterator<ChatMessage> iterator = data.iterator();
                                while (iterator.hasNext()) {
                                    ChatMessage next = iterator.next();
                                    if (next.getType() == ChatMessage.MSG_SEND_CHAT) {
                                        DataBaseHelp.getInstance(content).addChatMessage(next);
                                    }
                                }
                                getMvpView().onSuccess(data);
                            }

                            @Override
                            protected void onNextSN(String msg) {
                                super.onNextSN(msg);
                                getMvpView().onSuccess();
                            }

                            @Override
                            protected void onErrorEx(@NonNull Throwable e) {
                                super.onErrorEx(e);
                                getMvpView().onError("错误.");
                            }
                        });
            }

        }

    }

    @Override
    public void updateHistory(String pid) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", getMvpView().getUid());
        map.put("pid", pid);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .updateHistory(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<ChatMessage>() {
                    @Override
                    protected void onNextEx(@NonNull ChatMessage data) {
                        super.onNextEx(data);

                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);

                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        super.onErrorEx(e);
                        getMvpView().onError("错误.");
                    }
                });
    }

    @Override
    public void getUserInfo(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", getMvpView().getUid());
        map.put("uid", id);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getFriendInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>() {
                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        super.onNextEx(data);
                        DataBaseHelp.getInstance(getMvpView().getContent()).addOrUpdateUser(data.getUid(), GsonUtil.BeanToJson(data));
                    }
                });
    }

    @Override
    public void getConversation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("fromId", getMvpView().getUid());
        map.put("toId", getMvpView().getTo_Id());
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getConversation(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<String>() {
                    @Override
                    protected void onNextEx(@NonNull String data) {
                        super.onNextEx(data);
                        Logger.t("net").i("conversation:" + data);
                        List<SessionMessage> list = DataBaseHelp.getInstance(getMvpView().getContent()).getSessionList();
                        String conversation = null;
                        for (int i=0;i<list.size();i++){
                            SessionMessage sessionMessage = list.get(i);
                            String from_id = sessionMessage.getFrom_id();
                            String to_id = sessionMessage.getTo_id();
                            if (getMvpView().getUid().equals(from_id) && getMvpView().getTo_Id().equals(to_id)){
                                 conversation = sessionMessage.getConversation();
                                 break;
                            }
                        }
                        if (conversation == null){
                            if (!data.isEmpty()){
                                getMvpView().onSuccessConversation(data);
                                return;
                            }
                        }
                        getMvpView().onSuccessConversation(conversation);

                    }
                });
    }

    @Override
    public void SocketSendJson(String json,Boolean isUpdateSession) {
        ChatMessage chatMessage = GsonUtil.GsonToBean(json, ChatMessage.class);
        addOrChatMessage(chatMessage);
        if (isUpdateSession){
            addOrUpdateSession(chatMessage);
        }
        SocketManager.sendMsgSocket(getMvpView().getContent(), json, getMvpView().callBack());
    }

    private final void addOrChatMessage(ChatMessage chatMessage) {
        if (chatMessage == null) return;
        DataBaseHelp.getInstance(getMvpView().getContent()).addChatMessage(chatMessage);
    }

    private final void addOrUpdateSession(ChatMessage chatMessage) {
        SessionMessage sessionMessage = new SessionMessage();
        sessionMessage.setConversation(chatMessage.getConversation());
        sessionMessage.setTo_id(chatMessage.getToId());
        sessionMessage.setFrom_id(chatMessage.getFromId());
        sessionMessage.setBody(chatMessage.getBody());
        sessionMessage.setMsg_status(chatMessage.getMsgStatus());
        sessionMessage.setTime(chatMessage.getTime());
        sessionMessage.setBody_type(chatMessage.getBodyType());
        sessionMessage.setNumber(0);
        DataBaseHelp.getInstance(getMvpView().getContent()).addOrUpdateSession(sessionMessage);
        EventBus.getDefault().post(chatMessage);
    }


}
