package com.learn.agg.msg.contract;

import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface MessageContract {

    interface IView extends IViewContract {
        String getUid();
        void onSuccess(List<String> list);
        void onError();
    }

    interface IPresenter extends IPresenterContract {
        void getAddFriendMsg();
    }
}
