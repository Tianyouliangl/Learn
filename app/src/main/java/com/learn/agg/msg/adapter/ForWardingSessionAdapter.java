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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForWardingSessionAdapter extends RecyclerView.Adapter<ForWardingSessionAdapter.ForWardingSessionHolder> {

    private Context mContext;
    private List<SessionMessage> mList;
    private Boolean isMC = false;
    private onClickItemInterface mOnClickListener;

    public interface onClickItemInterface {
        void onClickItem(SessionMessage message, Boolean isMC,Boolean isChecked);
    }

    public ForWardingSessionAdapter(List<SessionMessage> list, Context context) {
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
        return new ForWardingSessionHolder(LayoutInflater.from(mContext).inflate(R.layout.forwarding_session_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ForWardingSessionHolder holder, int position) {
        final SessionMessage sessionMessage = mList.get(position);
        holder.checkbox.setVisibility(isMC ? View.VISIBLE : View.GONE);
        LoginBean info = sessionMessage.getInfo();
        String imageUrl = info.getImageUrl();
        String username = info.getRemark();
        holder.tv_content.setText(username);
        Glide.with(mContext).load(imageUrl).into(holder.iv_iv);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMC) {
                    holder.checkbox.setChecked(!holder.checkbox.isChecked());
                }
                if (mOnClickListener != null) {
                    mOnClickListener.onClickItem(sessionMessage, isMC,holder.checkbox.isChecked());
                }
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

    public void setMC(Boolean mc) {
        this.isMC = mc;
        notifyDataSetChanged();
    }

    public void setOnForWardingSessionItemClick(onClickItemInterface itemClick) {
        this.mOnClickListener = itemClick;
    }

    class ForWardingSessionHolder extends RecyclerView.ViewHolder {

        public CheckBox checkbox;
        public RoundedImageView iv_iv;
        public TextView tv_content;

        public ForWardingSessionHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            iv_iv = itemView.findViewById(R.id.riv_iv);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
