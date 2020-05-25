package com.learn.agg.act.presenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.learn.agg.act.contract.EnrollContract;
import com.learn.agg.base.BasePresenter;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.util.PhoneUtils;
import com.senyint.ihospital.client.HttpFactory;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EnrollPresenter extends BasePresenter<EnrollContract.IView> implements EnrollContract.IPresenter {

    @Override
    public void enrollUser() {
        String mobile = getMvpView().getMobile();
        String name = getMvpView().getName();
        String email = getMvpView().getEmail();
        String location = getMvpView().getLocation();
        String password = getMvpView().getPassword();
        if (mobile.isEmpty()){
            getMvpView().onError("请输入账号");
            return;
        }
        if (!PhoneUtils.isMobile(mobile)){
            getMvpView().onError("手机号格式不正确!");
            return;
        }
        if (name.isEmpty()){
            getMvpView().onError("请输入昵称");
            return;
        }
        if (email.isEmpty()){
            getMvpView().onError("请输入邮箱");
            return;
        }
        if (location.isEmpty()){
            getMvpView().onError("请输入地址");
            return;
        }
        if (password.isEmpty()){
            getMvpView().onError("请输入密码");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("location", location);
        map.put("imageUrl", getMvpView().getImageUrl());
        map.put("sex","男");
        map.put("mobile", mobile);
        map.put("password", password);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .register(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<Object>(){

                    @Override
                    protected void onNextEx(@NonNull Object data) {
                        Log.i("net","--------" + data);
                        getMvpView().onSuccess();
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError(e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onError(msg);
                    }
                });
    }
}
