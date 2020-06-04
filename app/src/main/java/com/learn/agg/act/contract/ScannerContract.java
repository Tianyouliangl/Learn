package com.learn.agg.act.contract;

import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

public interface ScannerContract {

    interface IView extends IViewContract {
        String getUid();
        String getMobile();
        void onError(String msg);
        void onSuccess(String json);
        void onSuccessNull();

    }

    interface IPresenter extends IPresenterContract {
        void getUserInfo();
    }
}
