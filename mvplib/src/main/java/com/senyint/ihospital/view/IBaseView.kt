package com.senyint.ihospital.view

import com.senyint.ihospital.contract.IPresenterContract


interface IBaseView<out P : IPresenterContract> {
    fun registerPresenter(): Class<out P>
}
