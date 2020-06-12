package com.learn.agg.video;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.video.contract.ReadContract;
import com.learn.agg.video.presenter.ReadPresenter;

import org.jetbrains.annotations.NotNull;

public class ReadFragment extends BaseMvpFragment<ReadContract.IPresenter> implements ReadContract.IView {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @NotNull
    @Override
    public Class<? extends ReadContract.IPresenter> registerPresenter() {
        return ReadPresenter.class;
    }
}
