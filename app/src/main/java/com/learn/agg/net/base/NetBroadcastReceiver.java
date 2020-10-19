package com.learn.agg.net.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.NetState;
import com.lib.xiangxiang.im.BuildConfig;
import com.orhanobut.logger.Logger;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class NetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.t("net").i("NetBroadcastReceiver changed");
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Boolean netWorkState = NetState.hasNetWorkConnection(context);
            Logger.t("net").i("网络是否链接:" + netWorkState);
            // 当网络发生变化，判断当前网络状态，并通过NetEvent回调当前网络状态
            final int workStatus = NetState.getNetWorkStatus(context);
            if (netWorkState){
                upUserInfo(workStatus,context);
            }
        }
    }

    private void upUserInfo(int workState,Context context) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("online",workState);
        map.put("uid", EasySP.init(context).getString(Constant.SPKey_UID));
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .upUserInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<String>(){

                    @Override
                    protected void onNextEx(@NonNull String data) {
                        Logger.t("net").i("-----" + data);

                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {
                        Logger.t("net").i("-----" + e.toString());
                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);
                        Logger.t("net").i("-----" + msg);
                    }
                });
    }

}
