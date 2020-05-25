package com.senyint.ihospital.contract

/**
 * 公共View层契约接口
 * Created by L on 2017/7/10.
 */
interface IViewContract {

    fun showToast(msg: String)

    fun showLoadingDialog()

    fun dismissDialog()
}