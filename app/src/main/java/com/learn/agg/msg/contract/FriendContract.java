package com.learn.agg.msg.contract;

import com.learn.agg.net.bean.LoginBean;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface FriendContract {

    interface IView extends IViewContract {
        String getUid();
        void onSuccess(List<LoginBean> list);
        void onError(String msg);
    }

    interface IPresenter extends IPresenterContract {
        void getAllFriend();
    }
}
