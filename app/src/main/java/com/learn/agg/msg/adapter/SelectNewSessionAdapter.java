package com.learn.agg.msg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.SessionNewBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SelectNewSessionAdapter extends RecyclerView.Adapter<SelectNewSessionAdapter.ForWardingSessionHolder> {

    private Context mContext;
    private List<SessionNewBean> mList;

    public SelectNewSessionAdapter(List<SessionNewBean> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<SessionNewBean> list) {
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
        SessionNewBean message = mList.get(position);
        Glide.with(mContext).load(message.getImageUrl()).into(holder.iv_iv);
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
