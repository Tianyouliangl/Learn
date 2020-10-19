package com.learn.agg.msg.contract;

import android.content.Context;

import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.SessionNewBean;
import com.lib.xiangxiang.im.SocketManager;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface SelectNewSessionContract {
    interface IView extends IViewContract {
        String getUid();
        void onSuccess(List<SessionNewBean> list);
        void onSuccessConversation(String id,String conversation,int type);
        void onError(String msg);
        Context getContext();
        SocketManager.SendMsgCallBack callBack();
    }

    interface IPresenter extends IPresenterContract {
        void getAllFriend();
        void getConversation(String to_id,int type);
        void SocketSendJson(String json,Boolean isUpdateSession);
    }
}
