package com.learn.agg.msg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionNewBean;
import com.learn.commonalitylibrary.util.NetState;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class SessionNewAdapter extends RecyclerView.Adapter<SessionNewAdapter.FriendHolder> {


    private  Context mContext;
    private  List<SessionNewBean> mList;
    private SessionNewInterface mClickListener;

    public interface SessionNewInterface{
        void onItemClick(SessionNewBean bean,int position);
    }

    public void setOnItemClickListener(SessionNewInterface clickListener){
        this.mClickListener = clickListener;
    }

    public SessionNewAdapter(Context context, List<SessionNewBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setData(List<SessionNewBean> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendHolder(LayoutInflater.from(mContext).inflate(R.layout.session_new_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendHolder holder, final int position) {
        final SessionNewBean bean = mList.get(position);
        String imageUrl = bean.getImageUrl();
        String username = bean.getUsername();
        Glide.with(mContext).load(imageUrl).into(holder.iv_iv);
        holder.tv_content.setText(username);
        holder.checkbox.setChecked(bean.getCheck());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null){
                    mList.get(position).setCheck(!bean.getCheck());
                    mClickListener.onItemClick(bean,position);
                    notifyDataSetChanged();
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

    public List<SessionNewBean> getData(){
        return mList;
    }

    public List<SessionNewBean> getCheckedSize(){
        List<SessionNewBean> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i ++){
            Boolean check = mList.get(i).getCheck();
            if (check){
                list.add(mList.get(i));
            }
        }
        return list;
    }

    public class FriendHolder extends RecyclerView.ViewHolder {

        public CheckBox checkbox;
        public RoundedImageView iv_iv;
        public TextView tv_content;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            iv_iv = itemView.findViewById(R.id.riv_iv);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
