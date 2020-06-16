package com.learn.agg.msg.presenter;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.BaseChatContract;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.ChatMessage;

import java.util.List;

public class BaseChatPresenter extends BasePresenter<BaseChatContract.IView> implements BaseChatContract.IPresenter {
    @Override
    public void getHistory(final int pageNo, int pageSize) {
        String conversation = getMvpView().getConversation();
//        HashMap<String,Object> map = new HashMap<>();
//        map.put("uid",getMvpView().getUid());
//        map.put("conversation",conversation);
//        map.put("pageNo",pageNo);
//        map.put("pageSize",pageSize);
//        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
//                .getHistory(map)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserverTC<List<ChatMessage>>() {
//                    @Override
//                    protected void onNextEx(@NonNull List<ChatMessage> data) {
//                        super.onNextEx(data);
//                        getMvpView().onSuccess(data);
//                    }
//
//                    @Override
//                    protected void onNextSN(String msg) {
//                        super.onNextSN(msg);
//                        getMvpView().onSuccess();
//                    }
//
//                    @Override
//                    protected void onErrorEx(@NonNull Throwable e) {
//                        super.onErrorEx(e);
//                        getMvpView().onError("错误.");
//                    }
//                });

        List<ChatMessage> list = DataBaseHelp.getInstance(getMvpView().getContent()).getChatMessage(conversation, pageNo, pageSize);
        getMvpView().onSuccess(list);
    }
}
