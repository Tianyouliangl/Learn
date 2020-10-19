package com.learn.agg.msg.presenter;


import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.act.ForWardingActivity;
import com.learn.agg.msg.contract.ForWardingMessageContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.orhanobut.logger.Logger;
import com.senyint.ihospital.client.HttpFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForWardingMessagePresenter extends BasePresenter<ForWardingMessageContract.IView> implements ForWardingMessageContract.IPresenter {

    @Override
    public void getSessionList() {
        List<SessionMessage> list = DataBaseHelp.getInstance(getMvpView().getContext()).getSessionList();
        if (list.size() > 0){
            getMvpView().onSuccess(list);
        }else {
            getMvpView().onError("null");
        }
    }

    @Override
    public void getConversation(final String to_id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("fromId", getMvpView().getUid());
        map.put("toId",to_id);
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
                            if (getMvpView().getUid().equals(from_id) && to_id.equals(to_id)){
                                conversation = sessionMessage.getConversation();
                                break;
                            }
                        }
                        if (conversation == null){
                            if (!data.isEmpty()){
                                getMvpView().onSuccessConversation(data,to_id);
                            }
                            return;
                        }
                        getMvpView().onSuccessConversation(conversation,to_id);

                    }
                });
    }
}
