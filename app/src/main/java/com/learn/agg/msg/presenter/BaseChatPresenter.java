package com.learn.agg.msg.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.BaseChatContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.commonalitylibrary.ChatMessage;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseChatPresenter extends BasePresenter<BaseChatContract.IView> implements BaseChatContract.IPresenter {
    @Override
    public void getHistory(final int pageNo, int pageSize) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",getMvpView().getUid());
        map.put("conversation",getMvpView().getConversation());
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
