package com.learn.agg.msg.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.BaseChatContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.ChatMessage;
import com.senyint.ihospital.client.HttpFactory;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseChatPresenter extends BasePresenter<BaseChatContract.IView> implements BaseChatContract.IPresenter {
    @Override
    public void getHistory(final int pageNo, int pageSize) {
        String conversation = getMvpView().getConversation();

        List<ChatMessage> list = DataBaseHelp.getInstance(getMvpView().getContent()).getChatMessage(conversation, pageNo, pageSize);
        if (list.size() > 0){
            getMvpView().onSuccess(list);
        }else {
            HashMap<String,Object> map = new HashMap<>();
            map.put("uid",getMvpView().getUid());
            map.put("conversation",conversation);
            map.put("pageNo",pageNo);
            map.put("pageSize",pageSize);
            HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                    .getHistory(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserverTC<List<ChatMessage>>() {
                        @Override
                        protected void onNextEx(@NonNull List<ChatMessage> data) {
                            super.onNextEx(data);
                            for (int i=0;i<data.size();i++){
                                ChatMessage message = data.get(i);
                                DataBaseHelp.getInstance(getMvpView().getContent()).addChatMessage(message);
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

    @Override
    public void updateHistory(String pid) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",getMvpView().getUid());
        map.put("pid",pid);
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


}
