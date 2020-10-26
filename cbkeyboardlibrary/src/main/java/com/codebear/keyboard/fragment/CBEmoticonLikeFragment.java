package com.codebear.keyboard.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codebear.keyboard.R;
import com.codebear.keyboard.adapter.CBEmoticonsLikePhotoAdapter;
import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.net.BaseObserverTC;
import com.codebear.keyboard.net.BaseResponseTC;
import com.codebear.keyboard.net.IHttpProtocol;
import com.codebear.keyboard.widget.CBEmoticonsView;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.body.GifBean;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class CBEmoticonLikeFragment extends Fragment implements ICBFragment, CBEmoticonsLikePhotoAdapter.onItemClick, CBEmoticonsLikePhotoAdapter.onItemLongClick, CBEmoticonsLikePhotoAdapter.onItemTouch {


    private View mRootView;
    private boolean hadLoadData = false;
    private boolean userVisible = false;
    private boolean viewCreate = false;
    private RecyclerView rlRecyclerView;
    private CBEmoticonsView.OnEmoticonClickListener mListener;
    private LinearLayoutManager layoutManager;
    private CBEmoticonsLikePhotoAdapter adapter;
    private boolean sIsScrolling = true;
    private FrameLayout rl_loading;
    private Dialog previewDialog;
    private ImageView previewIv;

    public static ICBFragment newInstance() {
        return new CBEmoticonLikeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_like_photo, container, false);
        Log.i("aa", "onCreateView");
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rlRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rl_like_photo);
        rl_loading = mRootView.findViewById(R.id.rl_loading);
        viewCreate = true;
        initView();
        Log.i("aa", "onViewCreated");
        if (!hadLoadData && userVisible) {
            hadLoadData = true;
            initData();
        }
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            userVisible = true;
            if (!hadLoadData && viewCreate) {
                hadLoadData = true;
                initData();
            }
        }
    }

    private void initView() {
        if (!viewCreate) {
            return;
        }
        if (layoutManager == null) {
            layoutManager = new GridLayoutManager(getContext(), 4);
        }
        if (adapter == null) {
            adapter = new CBEmoticonsLikePhotoAdapter(getContext(), new ArrayList<GifBean>());
        }
        Log.i("aa", "initView");
        adapter.setOnItemClickListener(this);
        adapter.setOnItemClickLongListener(this);
        adapter.setOnItemTouchListener(this);
        rlRecyclerView.setLayoutManager(layoutManager);
        rlRecyclerView.setNestedScrollingEnabled(false);
        rlRecyclerView.setAdapter(adapter);
        rlRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    sIsScrolling = true;
                    Glide.with(getContext()).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling == true) {
                        Glide.with(getContext()).resumeRequests();
                    }
                    sIsScrolling = false;
                }
            }
        });
        isShowLoading(true);
    }

    private void initData() {
        Log.i("aa", "initData");
        getLikePhotoList();
    }

    private void getLikePhotoList() {
        String uid = EasySP.init(getContext()).getString(Constant.SPKey_UID);
        if (!uid.isEmpty()){
            HashMap<String, String> map = new HashMap<>();
            map.put("uid",uid);
            HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                    .getPhotoList(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserverTC<List<GifBean>>() {

                        @Override
                        protected void onNextEx(@NonNull List<GifBean> data) {
                            super.onNextEx(data);
                            adapter.setList(data);
                            adapter.notifyDataSetChanged();
                            Log.i("aa", "size:" + data.size());
                            isShowLoading(false);
                        }

                        @Override
                        protected void onNextSN(String msg) {
                            super.onNextSN(msg);
                            isShowLoading(false);
                        }

                        @Override
                        protected void onErrorEx(@NonNull Throwable e) {
                            super.onErrorEx(e);
                            isShowLoading(false);
                        }
                    });
        }

    }

    private void isShowLoading(Boolean b){
        rl_loading.setVisibility(b ? View.VISIBLE:View.GONE);
    }


    @Override
    public void setSeeItem(int which) {
        Log.i("aa", "setSeeItem:" + which);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void setEmoticonsBean(EmoticonsBean emoticonsBean) {

    }

    @Override
    public void setLikePhotoBean(GifBean gifBean) {
        if (viewCreate && adapter != null){
            getLikePhotoList();
        }
    }

    @Override
    public void setOnEmoticonClickListener(CBEmoticonsView.OnEmoticonClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onItemClickListener(GifBean bean) {
        if (mListener != null){
            mListener.onLikePhotoClick(bean);
        }
    }

    @Override
    public Boolean onItemLongClickListener(GifBean bean) {
        showPreview(bean);
        return false;
    }

    @Override
    public Boolean onItemTouchListener(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                hidePreview();
                break;
        }
        return false;
    }

    private void showPreview(GifBean bean) {
        if (previewDialog == null) {
            View view = View.inflate(getContext(), R.layout.view_preview_big_emoticon, null);
            previewIv = (ImageView) view.findViewById(R.id.iv_preview_big_emotion);
            previewDialog = new Dialog(getContext(), R.style.preview_dialog_style);
            previewDialog.setContentView(view);
        }
        if (bean.getType() == ChatMessage.MSG_GIF_GIF) {
            Glide.with(getContext()).asGif().load(bean.getUrl()).into(previewIv);
        } else {
            Glide.with(getContext()).asBitmap().load(bean.getUrl()).into(previewIv);
        }
        if (!previewDialog.isShowing()) {
            previewDialog.show();
        }
    }

    private void hidePreview() {
        if (previewDialog != null && previewDialog.isShowing()) {
            Glide.with(getContext()).clear(previewIv);
            previewDialog.dismiss();
        }
    }
}
