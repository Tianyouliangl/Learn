package com.learn.agg.msg.contract;

import android.content.Context;

import com.learn.commonalitylibrary.ChatMessage;
import com.lib.xiangxiang.im.SocketManager;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface BaseChatContract {
    interface IView extends IViewContract {

        String getUid();

        String getTo_Id();

        String getConversation();

        Context getContent();

        SocketManager.SendMsgCallBack callBack();

        void onSuccess(List<ChatMessage> list);

        void onSuccess();

        void onSuccessAddLike(ChatMessage message);

        void onSuccessConversation(String conversation);

        void onError(String msg);
    }

    interface IPresenter extends IPresenterContract {

        void getHistory(final int pageNo, int pageSize, String toid);

        void updateHistory(String pid);

        void getUserInfo(String id);

        void getConversation();

        void SocketSendJson(String json,Boolean isUpdateSession);

        void addLikePhoto(ChatMessage msg);
    }
}
