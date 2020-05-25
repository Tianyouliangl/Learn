package com.senyint.ihospital.presenter

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

fun IBasePresenter<*>.getContextEx(): Context = when {
    getMvpView() is Activity -> getMvpView() as Activity
    getMvpView() is Fragment -> (getMvpView() as Fragment).activity!!
    else -> throw IllegalStateException("the presenter not found context")
}
