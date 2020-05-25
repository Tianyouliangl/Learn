package com.learn.agg.msg.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.FriendInfoContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.net.bean.LoginBean;
import com.senyint.ihospital.client.HttpFactory;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FriendInfoPresenter extends BasePresenter<FriendInfoContract.IView> implements FriendInfoContract.IPresenter {

    @Override
    public void addFriendMsg() {
        // friend_type 当前状态  0：同意 1 拒绝 2 等待
        // source 来源 扫一扫/手机号 0 1
        // content 验证信息 （我是***）
        String fromId = getMvpView().getFromId();
        String toId = getMvpView().getToId();
        String pid = getMvpView().getPid();
        if (fromId.isEmpty() || toId.isEmpty() || pid.isEmpty()) {
            getMvpView().onError("错误");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("from_id",fromId);
        map.put("to_id",toId);
        map.put("pid",pid);
        map.put("friend_type",2);
        map.put("source",1);
        map.put("content","我是"+getMvpView().getName());
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .addFriendMsg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>(){

                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        getMvpView().onSuccess();
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError("错误:"+e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onError(msg);
                    }
                });

    }
}
