package com.senyint.ihospital.presenter

import com.senyint.ihospital.contract.IViewContract


/**
 * Created by L on 2017/7/10.
 */
interface IBasePresenter<out V : IViewContract> {
    fun getMvpView(): V
}




