package com.learn.agg.msg.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.msg.act.FriendInfoActivity;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.net.bean.FriendMsgBean;
import com.learn.agg.net.bean.LoginBean;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.senyint.ihospital.client.HttpFactory;
import com.shehuan.niv.NiceImageView;
import com.white.easysp.EasySP;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FriendMsgAdapter extends RecyclerView.Adapter<FriendMsgAdapter.FriendMsgHolder> {


    private List<FriendMsgBean> mList;
    private Context mContext;
    private String local_uid;
    private FriendMsgInterface mLoadingChangListener;

    public FriendMsgAdapter(Context context, List<FriendMsgBean> list) {
        local_uid = EasySP.init(context).getString(Constant.SPKey_UID);
        this.mContext = context;
        this.mList = list;
    }

    public interface FriendMsgInterface {
        void showLoading();
        void dismissLoading();
        void onClickReject(FriendMsgBean bean);
        void onClickAgree(FriendMsgBean bean);
    }

    public void setOnLoadingChangListener(FriendMsgInterface friendMsgInterface) {
        this.mLoadingChangListener = friendMsgInterface;
    }

    @NonNull
    @Override
    public FriendMsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_msg, parent, false);
        return new FriendMsgHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendMsgHolder holder, final int position) {
        holder.fl_loading.setVisibility(View.VISIBLE);
        holder.rl_data.setVisibility(View.GONE);
        final FriendMsgBean bean = mList.get(position);
        final String id = bean.getFrom_id();
        final int friend_type = bean.getFriend_type();
        if (local_uid.equals(id)) {
            if (friend_type == 0) {
                holder.tv_wait.setVisibility(View.VISIBLE);
                holder.tv_reject.setVisibility(View.GONE);
                holder.tv_agree.setVisibility(View.GONE);
                holder.tv_wait.setText("已添加");
            }

            if (friend_type == 1) {
                holder.tv_wait.setVisibility(View.VISIBLE);
                holder.tv_reject.setVisibility(View.GONE);
                holder.tv_agree.setVisibility(View.GONE);
                holder.tv_wait.setText("对方拒绝");
            }

            if (friend_type == 2) {
                holder.tv_wait.setVisibility(View.VISIBLE);
                holder.tv_reject.setVisibility(View.GONE);
                holder.tv_agree.setVisibility(View.GONE);
            }
            searUserData(holder, bean.getTo_id());
            holder.tv_content.setText("已发送验证信息");
        } else {
            if (friend_type == 0) {
                holder.tv_wait.setVisibility(View.VISIBLE);
                holder.tv_reject.setVisibility(View.GONE);
                holder.tv_agree.setVisibility(View.GONE);
                holder.tv_wait.setText("已添加");
            }

            if (friend_type == 1) {
                holder.tv_wait.setVisibility(View.VISIBLE);
                holder.tv_reject.setVisibility(View.GONE);
                holder.tv_agree.setVisibility(View.GONE);
                holder.tv_wait.setText("已拒绝");
            }

            if (friend_type == 2) {
                holder.tv_wait.setVisibility(View.GONE);
                holder.tv_reject.setVisibility(View.VISIBLE);
                holder.tv_agree.setVisibility(View.VISIBLE);
            }
            holder.tv_content.setText(bean.getContent());
            searUserData(holder, bean.getFrom_id());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingChangListener != null) {
                    mLoadingChangListener.showLoading();
                }
                if (local_uid.equals(id)) {
                    getInfo(bean.getTo_id(),false, friend_type);
                } else {
                    getInfo(bean.getFrom_id(), true,friend_type);
                }
            }
        });

        holder.tv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingChangListener != null) {
                    mLoadingChangListener.showLoading();
                    mLoadingChangListener.onClickReject(mList.get(position));
                }
            }
        });

        holder.tv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingChangListener != null) {
                    mLoadingChangListener.showLoading();
                    mLoadingChangListener.onClickAgree(mList.get(position));
                }
            }
        });
    }

    private void getInfo(final String id, final boolean isGone , final int friend_type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", id);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getUserInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>() {

                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        String mobile = data.getMobile();
                        HashMap<String, String> map = new HashMap<>();
                        map.put("phone", mobile);
                        map.put("uid", local_uid);
                        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                                .findFriend(map)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseObserverTC<LoginBean>() {

                                    @Override
                                    protected void onNextEx(@NonNull LoginBean data) {
                                        if (mLoadingChangListener != null) {
                                            mLoadingChangListener.dismissLoading();
                                        }
                                        String json = GsonUtil.BeanToJson(data);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("data", json);

                                        if (friend_type == 2) {
                                            bundle.putBoolean("isGone", isGone);
                                        } else {
                                            bundle.putBoolean("isGone", false);
                                        }
                                        Intent intent = new Intent(mContext, FriendInfoActivity.class);
                                        intent.putExtra("bundle", bundle);
                                        mContext.startActivity(intent);
                                    }

                                    @Override
                                    protected void onErrorEx(@NonNull Throwable e) {

                                    }

                                    @Override
                                    protected void onNextSN(String msg) {
                                        super.onNextSN(msg);
                                    }
                                });
                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {

                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);

                    }
                });
    }

    public void setData(List<FriendMsgBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    private void searUserData(final FriendMsgHolder holder, final String uid) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getUserInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>() {

                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        holder.fl_loading.setVisibility(View.GONE);
                        holder.rl_data.setVisibility(View.VISIBLE);
                        String imageUrl = data.getImageUrl();
                        String username = data.getUsername();
                        Glide.with(mContext).load(imageUrl).into(holder.iv_icon);
                        holder.tv_name.setText(username);

                    }

                    @Override
                    protected void onErrorEx(@NonNull Throwable e) {

                    }

                    @Override
                    protected void onNextSN(String msg) {
                        super.onNextSN(msg);

                    }
                });
    }


    @Override
    public int getItemCount() {
        if (mList == null || mList.size() <= 0) {
            return 0;
        }
        return mList.size();
    }

    public class FriendMsgHolder extends RecyclerView.ViewHolder {

        public NiceImageView iv_icon;
        public TextView tv_name;
        public TextView tv_content;
        public TextView tv_reject;
        public TextView tv_agree;
        public TextView tv_wait;
        public FrameLayout fl_loading;
        public RelativeLayout rl_data;

        public FriendMsgHolder(@NonNull View itemView) {
            super(itemView);
            fl_loading = itemView.findViewById(R.id.fl_loading);
            rl_data = itemView.findViewById(R.id.rl_data);
            iv_icon = itemView.findViewById(R.id.niv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_reject = itemView.findViewById(R.id.tv_reject);
            tv_agree = itemView.findViewById(R.id.tv_agree);
            tv_wait = itemView.findViewById(R.id.tv_wait);

        }
    }
}
