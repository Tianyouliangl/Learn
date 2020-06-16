package com.learn.agg.msg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.NetState;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {


    private  Context mContext;
    private  List<LoginBean> mList;
    private friendInterface mOnClickItem;

    public interface friendInterface{
        void onItemClick(LoginBean bean);
    }

    public void setOnClickItemListener(friendInterface friendInterface){
        this.mOnClickItem = friendInterface;
    }

    public FriendAdapter(Context context, List<LoginBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<LoginBean> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendHolder(LayoutInflater.from(mContext).inflate(R.layout.item_friend,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, final int position) {
        LoginBean bean = mList.get(position);
        String imageUrl = bean.getImageUrl();
        String sign = bean.getSign();
        int online = bean.getOnline();
        String username = bean.getUsername();
        Glide.with(mContext).load(imageUrl).into(holder.iv_icon);
        holder.tv_name.setText(username);
        holder.tv_content.setText(NetState.getNetState(online)+sign);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickItem != null){
                    mOnClickItem.onItemClick(mList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null || mList.size() <= 0){
            return 0;
        }
        return mList.size();
    }

    public class FriendHolder extends RecyclerView.ViewHolder {

        public RoundedImageView iv_icon;
        public TextView tv_name;
        public TextView tv_content;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.niv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
