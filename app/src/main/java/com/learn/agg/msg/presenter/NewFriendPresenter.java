package com.learn.agg.msg.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.NewFriendContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.net.bean.FriendMsgBean;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.senyint.ihospital.client.HttpFactory;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewFriendPresenter extends BasePresenter<NewFriendContract.IView> implements NewFriendContract.IPresenter {

    @Override
    public void getAllFriendMsg() {
        String uid = getMvpView().getUid();
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getAllFriendMsg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<List<FriendMsgBean>>(){
                    @Override
                    protected void onNextEx(@NonNull List<FriendMsgBean> data) {
                        super.onNextEx(data);
                        getMvpView().onSuccess(data);
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError(e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onError(msg);
                    }
                });
    }

    @Override
    public void setFriend(final Context context, final String to_id, final String from_id, String pid, int friend_type, int source) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("from_id", from_id);
        map.put("to_id", to_id);
        map.put("pid", pid);
        map.put("friend_type", friend_type);
        map.put("source", source);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .setFriend(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<Object>(){
                    @Override
                    protected void onNextEx(@NonNull Object data) {
                        super.onNextEx(data);
                        getMvpView().onSuccessDisposeFriend();
                        getFriendInfo(context,from_id);
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError(e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onError(msg);
                    }
                });
    }

    @Override
    public void getFriendInfo(final Context context, String id) {
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
                        DataBaseHelp.getInstance(context).addOrUpdateUser(data.getUid(), GsonUtil.BeanToJson(data));
                    }
                });
    }


}
