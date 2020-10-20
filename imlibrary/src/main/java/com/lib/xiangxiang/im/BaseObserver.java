package com.lib.xiangxiang.im;

import android.util.Log;

import androidx.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {


    @Override
    public final void onSubscribe(Disposable d) {

        Log.i("Net", "--onSubscribe---" + d);
        onSubscribeEx(d);
    }

    public BaseObserver() {

    }

    @Override
    public final void onNext(@NonNull BaseResponse<T> data) {
        Log.i("Net", "--onNext---");
        int code = data.getCode();
        if (code == 0) {
            onNextSN(data.getMsg());
            return;
        }
        onNextEx(data.getData());
    }

    @Override
    public final void onError(@NonNull Throwable e) {
        Log.i("Net", "--onError---" + e.toString());
        onErrorEx(e);
    }

    @Override
    public final void onComplete() {
        Log.i("Net", "--onComplete---");
        onCompleteEx();
    }

    //Java实现下划线开头的方法
    protected void onCompleteEx() {

    }

    protected void onSubscribeEx(@NonNull Disposable d) {

    }

    protected void onNextEx(@NonNull T data) {

    }

    protected void onErrorEx(@NonNull Throwable e) {

    }

    protected void onNextSN(String msg) {

    }

}
