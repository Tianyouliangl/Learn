package com.senyint.ihospital.view

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.senyint.ihospital.contract.IPresenterContract
import com.senyint.ihospital.contract.IViewContract

/**
 * Created by L on 2017/7/10.
 */
abstract class MvpFragment<out P : IPresenterContract> : Fragment(), IBaseView<P>, IViewContract {


    private var mPresenter: P? = null

    fun getPresenter() = mPresenter!!

    private fun initPresenter() {
        val clazz = registerPresenter()
        val constructor = clazz.getConstructor()
        mPresenter = constructor.newInstance()
        mPresenter!!.registerMvpView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        mPresenter?.onCreate()
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mPresenter?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
        mPresenter = null
    }

    override fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
