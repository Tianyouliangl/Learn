package com.learn.agg.msg.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.base.BasePresenter;
import com.learn.agg.msg.contract.MessageContract;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.net.bean.FriendMsgBean;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kotlin.text.Regex;

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
                .subscribe(new BaseObserverTC<List<FriendMsgBean>>(){
                    @Override
                    protected void onNextEx(@NonNull List<FriendMsgBean> data) {
                        super.onNextEx(data);
                        ArrayList<String> list = new ArrayList<>();
                        if (data.size() > 0 && data != null){
                            for (int i=0;i<data.size();i++){
                                FriendMsgBean bean = data.get(i);
                                String id = bean.getFrom_id();
                                String content = bean.getContent();
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

                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onSuccess(null);
                    }
                });
    }
}
