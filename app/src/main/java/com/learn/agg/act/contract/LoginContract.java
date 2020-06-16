package com.learn.agg.act.contract;

import com.learn.commonalitylibrary.LoginBean;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

public interface LoginContract {

    interface IView extends IViewContract {
        void onSuccess(LoginBean data);
        void onError(String msg);
        String getPhone();
        String getPassword();
    }

    interface IPresenter extends IPresenterContract {
        void login();
    }
}
