package com.learn.agg.msg.contract;

import android.content.Context;

import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface ForWardingMessageContract {

    interface IView extends IViewContract {
        Context getContext();
        void onSuccess(List<SessionMessage> list);
        void onError(String msg);
        String getUid();
        void onSuccessConversation(String con,String id);
    }

    interface IPresenter extends IPresenterContract {
        void getSessionList();
        void getConversation(String to_id);
    }
}
