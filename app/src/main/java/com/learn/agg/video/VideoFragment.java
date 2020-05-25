package com.learn.agg.video;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.video.contract.VideoContract;
import com.learn.agg.video.presenter.VideoPresenter;

import org.jetbrains.annotations.NotNull;

public class VideoFragment extends BaseMvpFragment<VideoContract.IPresenter> implements VideoContract.IView {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @NotNull
    @Override
    public Class<? extends VideoContract.IPresenter> registerPresenter() {
        return VideoPresenter.class;
    }
}
