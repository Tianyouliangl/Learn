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
import com.learn.agg.net.bean.FriendMsgBean;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.white.easysp.EasySP;

import java.util.List;

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
            String toUserInfo = bean.getToUserInfo();
            UserInfo userInfo = GsonUtil.GsonToBean(toUserInfo, UserInfo.class);
            String imageUrl = userInfo.getData().getImageUrl();
            String username = userInfo.getData().getUsername();
            holder.fl_loading.setVisibility(View.GONE);
            holder.rl_data.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imageUrl).into(holder.iv_icon);
            holder.tv_name.setText(username);
            holder.tv_wait.setVisibility(View.VISIBLE);
            holder.tv_reject.setVisibility(View.GONE);
            holder.tv_agree.setVisibility(View.GONE);
            if (friend_type == 0) {
                holder.tv_wait.setText("已添加");
                holder.tv_content.setText("对方已同意");
            }

            if (friend_type == 1) {
                holder.tv_wait.setText("对方拒绝");
                holder.tv_content.setText("对方拒绝");
            }

            if (friend_type == 2) {
                holder.tv_wait.setText("等待验证");
                holder.tv_content.setText("已发送验证信息");
            }

        } else {
            String userInfo = bean.getFromUserInfo();
            UserInfo info = GsonUtil.GsonToBean(userInfo, FriendMsgAdapter.UserInfo.class);
            String imageUrl = info.getData().getImageUrl();
            String username = info.getData().getUsername();
            holder.fl_loading.setVisibility(View.GONE);
            holder.rl_data.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imageUrl).into(holder.iv_icon);
            holder.tv_name.setText(username);
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
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (local_uid.equals(id)) {
                    String toUserInfo = bean.getToUserInfo();
                    String json = GsonUtil.BeanToJson(GsonUtil.GsonToBean(toUserInfo, UserInfo.class).getData());
                    LoginBean loginBean = GsonUtil.GsonToBean(json, LoginBean.class);
                    if(friend_type == 0){
                        loginBean.setFriend(true);
                    }else {
                        loginBean.setFriend(false);
                    }
                    bundle.putString("data", GsonUtil.BeanToJson(loginBean));
                    bundle.putBoolean("isGone", true);
                } else {
                    String userInfo = bean.getFromUserInfo();
                    String json = GsonUtil.BeanToJson(GsonUtil.GsonToBean(userInfo, UserInfo.class).getData());
                    LoginBean loginBean = GsonUtil.GsonToBean(json, LoginBean.class);
                    if(friend_type == 0){
                        loginBean.setFriend(true);
                    }else {
                        loginBean.setFriend(false);
                    }
                    bundle.putString("data", GsonUtil.BeanToJson(loginBean));
                    bundle.putBoolean("isGone", false);
                }
                Intent intent = new Intent(mContext, FriendInfoActivity.class);
                intent.putExtra("bundle", bundle);
                mContext.startActivity(intent);
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

    public void setData(List<FriendMsgBean> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (mList == null || mList.size() <= 0) {
            return 0;
        }
        return mList.size();
    }

    public class FriendMsgHolder extends RecyclerView.ViewHolder {

        public RoundedImageView iv_icon;
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

    public static class UserInfo {

        /**
         * msg : 成功
         * code : 1
         * data : {"birthday":"2000-01-01","uid":"e02ff4f4-b87d-40ed-afb5-80363500c84f","sex":"男","imageUrl":"https://b-ssl.duitang.com/uploads/item/201804/29/20180429111927_4i2Ks.thumb.700_0.jpeg","mobile":"17600463506","sign":"退一步海阔天空.","online":0,"location":"北京市丰台区公益西桥","age":20,"email":"pipi@qq.com","username":"皮皮"}
         */

        private String msg;
        private int code;
        private DataBean data;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * birthday : 2000-01-01
             * uid : e02ff4f4-b87d-40ed-afb5-80363500c84f
             * sex : 男
             * imageUrl : https://b-ssl.duitang.com/uploads/item/201804/29/20180429111927_4i2Ks.thumb.700_0.jpeg
             * mobile : 17600463506
             * sign : 退一步海阔天空.
             * online : 0
             * location : 北京市丰台区公益西桥
             * age : 20
             * email : pipi@qq.com
             * username : 皮皮
             */

            private String birthday;
            private String uid;
            private String sex;
            private String imageUrl;
            private String mobile;
            private String sign;
            private int online;
            private String location;
            private int age;
            private String email;
            private String username;

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public int getOnline() {
                return online;
            }

            public void setOnline(int online) {
                this.online = online;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }
    }
}
