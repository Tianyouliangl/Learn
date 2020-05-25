package com.learn.agg.base;

import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;
import com.senyint.ihospital.presenter.IBasePresenter;


public abstract class BasePresenter<V extends IViewContract> implements IBasePresenter<V>, IPresenterContract {
    private V mMvpView = null;


    @Override
    public void registerMvpView(IViewContract mvpView) {
            mMvpView = (V)mvpView;
        }

    @Override
    public V getMvpView() {
        return mMvpView;
    }

    @Override
    public void onCreate() {

    }


    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }


    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
