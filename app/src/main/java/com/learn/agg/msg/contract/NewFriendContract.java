package com.learn.agg.msg.contract;

import com.learn.agg.net.bean.FriendMsgBean;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface NewFriendContract {

    interface IView extends IViewContract {
        String getUid();
        void onSuccess(List<FriendMsgBean> list);
        void onSuccessDisposeFriend();
        void onError(String msg);
    }

    interface IPresenter extends IPresenterContract {
        void getAllFriendMsg();
        void setFriend(String to_id,String from_id,String pid,int friend_type,int source);
    }
}
