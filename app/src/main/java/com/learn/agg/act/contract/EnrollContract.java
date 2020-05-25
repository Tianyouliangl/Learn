package com.learn.agg.act.contract;

import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

public interface EnrollContract {

    interface IView extends IViewContract {
        String getImageUrl();
        String getMobile();
        String getName();
        String getEmail();
        String getLocation();
        String getPassword();
        void onSuccess();
        void onError(String msg);
    }

    interface IPresenter extends IPresenterContract {
        void enrollUser();
    }
}
