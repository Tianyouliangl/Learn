package com.learn.agg.msg.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.SelectNewSessionContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.SessionNewBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.lib.xiangxiang.im.SocketManager;
import com.orhanobut.logger.Logger;
import com.senyint.ihospital.client.HttpFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SelectNewSessionPresenter extends BasePresenter<SelectNewSessionContract.IView> implements SelectNewSessionContract.IPresenter {

    @Override
    public void getAllFriend() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid",getMvpView().getUid());
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getFriend(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<List<SessionNewBean>>(){
                    @Override
                    protected void onNextEx(@NonNull List<SessionNewBean> data) {
                        super.onNextEx(data);
                        getMvpView().onSuccess(data);
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        super.onErrorEx(e);
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onSuccess(new ArrayList<SessionNewBean>());
                    }
                });
    }


    @Override
    public void getConversation(final String to_uid, final int type) {
        final String from_uid = getMvpView().getUid();
        HashMap<String, String> map = new HashMap<>();
        map.put("fromId",from_uid);
        map.put("toId",to_uid);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getConversation(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<String>() {
                    @Override
                    protected void onNextEx(@NonNull String data) {
                        super.onNextEx(data);
                        Logger.t("net").i("conversation:" + data);
                        List<SessionMessage> list = DataBaseHelp.getInstance(getMvpView().getContext()).getSessionList();
                        String conversation = null;
                        for (int i=0;i<list.size();i++){
                            SessionMessage sessionMessage = list.get(i);
                            String from_id = sessionMessage.getFrom_id();
                            String to_id = sessionMessage.getTo_id();
                            if ( to_uid.equals(to_id) && from_uid.equals(from_id)){
                                conversation = sessionMessage.getConversation();
                                break;
                            }
                        }
                        if (conversation == null){
                            if (!data.isEmpty()){
                                getMvpView().onSuccessConversation(to_uid,data,type);
                            }
                            return;
                        }
                        getMvpView().onSuccessConversation(to_uid,conversation,type);

                    }
                });
    }

    @Override
    public void SocketSendJson(String json, Boolean isUpdateSession) {
        ChatMessage chatMessage = GsonUtil.GsonToBean(json, ChatMessage.class);
        addOrChatMessage(chatMessage);
        if (isUpdateSession){
            addOrUpdateSession(chatMessage);
        }
        SocketManager.sendMsgSocket(getMvpView().getContext(), json, getMvpView().callBack());
    }

    private final void addOrChatMessage(ChatMessage chatMessage) {
        if (chatMessage == null) return;
        DataBaseHelp.getInstance(getMvpView().getContext()).addChatMessage(chatMessage);
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
        DataBaseHelp.getInstance(getMvpView().getContext()).addOrUpdateSession(sessionMessage);
        EventBus.getDefault().post(chatMessage);
    }
}
