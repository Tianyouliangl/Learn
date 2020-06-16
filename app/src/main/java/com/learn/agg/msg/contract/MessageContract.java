package com.learn.agg.msg.contract;

import android.content.Context;

import com.learn.commonalitylibrary.Session;
import com.learn.commonalitylibrary.SessionMessage;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface MessageContract {

    interface IView extends IViewContract {
        String getUid();
        void onSuccess(List<String> list);
        void onSession(List<SessionMessage> list);
        void onError();
        Context getContext();
    }

    interface IPresenter extends IPresenterContract {
        void getAddFriendMsg();
        void getSessionList();
    }
}
