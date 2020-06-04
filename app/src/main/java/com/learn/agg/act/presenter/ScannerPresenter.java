package com.learn.agg.act.presenter;

import androidx.annotation.NonNull;

import com.learn.agg.act.contract.ScannerContract;
import com.learn.agg.base.BasePresenter;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.net.bean.LoginBean;
import com.learn.agg.util.PhoneUtils;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.senyint.ihospital.client.HttpFactory;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScannerPresenter extends BasePresenter<ScannerContract.IView> implements ScannerContract.IPresenter {

    @Override
    public void getUserInfo() {
        String phone = getMvpView().getMobile();
        if (!PhoneUtils.isMobile(phone)){
            getMvpView().onError("手机号格式不正确!");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("uid", getMvpView().getUid());
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .findFriend(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>(){

                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        getMvpView().onSuccess(GsonUtil.BeanToJson(data));
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        getMvpView().onError("错误:"+e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        getMvpView().onSuccessNull();
                    }
                });
    }
}
