package com.learn.agg.msg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learn.agg.R;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForWardingUserAdapter extends RecyclerView.Adapter<ForWardingUserAdapter.ForWardingSessionHolder> {

    private Context mContext;
    private List<SessionMessage> mList;

    public ForWardingUserAdapter(List<SessionMessage> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<SessionMessage> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForWardingSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForWardingSessionHolder(LayoutInflater.from(mContext).inflate(R.layout.dialog_forwarding_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ForWardingSessionHolder holder, int position) {
        SessionMessage message = mList.get(position);
        LoginBean info = message.getInfo();
        if (null != info){
            String imageUrl = info.getImageUrl();
            Glide.with(mContext).load(imageUrl).into(holder.iv_iv);
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null || mList.size() <= 0) {
            return 0;
        }
        return mList.size();
    }


    class ForWardingSessionHolder extends RecyclerView.ViewHolder {

        public RoundedImageView iv_iv;

        public ForWardingSessionHolder(@NonNull View itemView) {
            super(itemView);
            iv_iv = itemView.findViewById(R.id.riv_iv);
        }
    }
}
