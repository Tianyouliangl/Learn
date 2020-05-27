package com.learn.agg.fragment;

import androidx.appcompat.view.menu.MenuPresenter;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.fragment.contract.MenuLeftContract;
import com.learn.agg.fragment.presenter.MenuLeftPresenter;

import org.jetbrains.annotations.NotNull;

public class MenuLeftFragment extends BaseMvpFragment<MenuLeftContract.IPresenter> implements MenuLeftContract.IView {
    @Override
    protected int getLayoutId() {
        return R.layout.layout_menu_left;
    }

    @NotNull
    @Override
    public Class<? extends MenuLeftContract.IPresenter> registerPresenter() {
        return MenuLeftPresenter.class;
    }
}
