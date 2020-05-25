package com.learn.agg.msg.contract;

import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

public interface FriendInfoContract {

    interface IView extends IViewContract {
        void onSuccess();
        void onError(String msg);
        String getName();
        String getFromId();
        String getToId();
        String getPid();
    }

    interface IPresenter extends IPresenterContract {
        void addFriendMsg();
    }
}
