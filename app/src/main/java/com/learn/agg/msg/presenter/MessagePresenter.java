package com.learn.agg.msg.presenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.MessageContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.net.bean.FriendMsgCountBean;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.orhanobut.logger.Logger;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessagePresenter extends BasePresenter<MessageContract.IView> implements MessageContract.IPresenter {

    @Override
    public void getAddFriendMsg() {
        final String uid = getMvpView().getUid();
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getAllFriendMsgCount(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<List<FriendMsgCountBean>>(){
                    @Override
                    protected void onNextEx(@NonNull List<FriendMsgCountBean> data) {
                        super.onNextEx(data);
                        ArrayList<String> list = new ArrayList<>();
                        if (data.size() > 0 && data != null){
                            for (int i=0;i<data.size();i++){
                                FriendMsgCountBean dataBean = data.get(i);
                                String id = dataBean.getFrom_id();
                                String content = dataBean.getContent();
                                if (!id.equals(uid)){
                                    String replace = content.replace("我是", "");
                                    list.add(replace+"请求添加您为好友");
                                }
                            }
                        }
                        getMvpView().onSuccess(list);
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError();
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onSuccess(null);
                    }
                });
    }

    @Override
    public void getSessionList() {
        List<SessionMessage> sessionList = DataBaseHelp.getInstance(getMvpView().getContext()).getSessionList();
        for (int i=0;i<sessionList.size();i++){
            String json = GsonUtil.BeanToJson(sessionList.get(i));
            Logger.t("net").json( json);
        }
        getMvpView().onSession(sessionList);
    }
}
