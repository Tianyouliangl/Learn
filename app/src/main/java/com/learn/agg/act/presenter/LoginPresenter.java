package com.learn.agg.act.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.act.contract.LoginContract;
import com.learn.agg.base.BasePresenter;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.util.PhoneUtils;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.senyint.ihospital.client.HttpFactory;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter<LoginContract.IView> implements LoginContract.IPresenter {

    @Override
    public void login() {
        String phone = getMvpView().getPhone();
        final String password = getMvpView().getPassword();
        if (!PhoneUtils.isMobile(phone)){
            getMvpView().onError("手机号格式不正确!");
            return;
        }
        if (password.isEmpty()){
            getMvpView().onError("请输入密码!");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("password", password);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .login(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>(){

                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
//                        NotificationUtils.getUserThreadCirBitmap(data.getImageUrl());
                        getMvpView().onSuccess(data);
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError("错误:"+e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onError(msg);
                    }
                });
    }
}
