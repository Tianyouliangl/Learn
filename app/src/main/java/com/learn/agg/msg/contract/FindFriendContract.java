package com.learn.agg.msg.contract;

import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

public interface FindFriendContract {

    interface IView extends IViewContract {
        String getMobile();
        String getUid();
        void onSuccess(String json);
        void onSuccessNull();
        void onError(String msg);
    }

    interface IPresenter extends IPresenterContract {
       void getFriend();
    }
}
