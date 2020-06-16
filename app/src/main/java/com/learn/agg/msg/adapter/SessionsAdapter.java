package com.learn.agg.msg.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.msg.act.ChatActivity;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.widgets.BadgeView;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionsHolder>{
    private Context mContext;
    private List<SessionMessage> mList;
    private long lastClickTime = 0;

    public SessionsAdapter(Context context, List<SessionMessage> list) {
        mContext = context;
        mList = list;
    }
    public void setData(List<SessionMessage> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setData(SessionMessage message){
        mList.add(message);
        notifyDataSetChanged();
    }

    public List<SessionMessage> getData(){
        return mList;
    }

    @NonNull
    @Override
    public SessionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sessions, parent, false);
        return new SessionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SessionsHolder holder, final int position) {
        ChatMessage chatMessage = new ChatMessage();
        final SessionMessage sessionMessage = mList.get(position);
        String to_id = mList.get(position).getTo_id();
        getFriendInfo(holder,to_id,sessionMessage);
        String body = mList.get(position).getBody();
        int body_type = mList.get(position).getBody_type();
        int msg_status = mList.get(position).getMsg_status();
        int number = mList.get(position).getNumber();
        Long time = mList.get(position).getTime();
        chatMessage.setType(ChatMessage.MSG_SEND_CHAT);
        chatMessage.setBodyType(body_type);
        chatMessage.setBody(body);
        holder.tv_content_session.setText(ImSendMessageUtils.getChatBodyType(chatMessage));
        holder.tv_time_session.setText(TimeUtil.getTimeString(time));
        holder.bv_session.showBadge(number);
        if (msg_status == ChatMessage.MSG_SEND_LOADING){
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.anim);
            holder.pb_state_session.setIndeterminateDrawable(drawable);
            holder.pb_state_session.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_content_session.getLayoutParams();
            params.leftMargin = 0;
            holder.tv_content_session.setLayoutParams(params);

        }else if (msg_status == ChatMessage.MSG_SEND_SUCCESS){
            holder.pb_state_session.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.pb_state_session.getLayoutParams();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_content_session.getLayoutParams();
            params.leftMargin = layoutParams.leftMargin;
            holder.tv_content_session.setLayoutParams(params);
        }else if (msg_status == ChatMessage.MSG_SEND_ERROR){
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
            holder.pb_state_session.setIndeterminateDrawable(drawable);
            holder.pb_state_session.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_content_session.getLayoutParams();
            params.leftMargin = 0;
            holder.tv_content_session.setLayoutParams(params);
        }else {
            holder.pb_state_session.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.pb_state_session.getLayoutParams();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_content_session.getLayoutParams();
            params.leftMargin = layoutParams.leftMargin;
            holder.tv_content_session.setLayoutParams(params);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                long timeD = time - lastClickTime;
                LoginBean loginBean = sessionMessage.getInfo();
                if (timeD <= 1000){
                    lastClickTime = time;
                    return;
                }
                if ( null == loginBean){
                    Toast.makeText(mContext,"请刷新列表",Toast.LENGTH_SHORT).show();
                    return;
                }
                String from_uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
                String from_icon = EasySP.init(mContext).getString(Constant.SPKey_icon(mContext));
                LoginBean from_bean = new LoginBean();
                from_bean.setUid(from_uid);
                from_bean.setImageUrl(from_icon);
                ChatActivity.startActivity(mContext,from_bean,loginBean);
                DataBaseHelp.getInstance(mContext).setSessionNumber(sessionMessage.getConversation(),0);
                lastClickTime = time;
            }
        });

    }

    private void getFriendInfo(final SessionsHolder holder, String to_id, final SessionMessage sessionMessage) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", EasySP.init(mContext).getString(Constant.SPKey_UID));
        map.put("uid", to_id);
        HttpFactory.INSTANCE.getProtocol(IHttpProtocol.class)
                .getFriendInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverTC<LoginBean>(){
                    @Override
                    protected void onNextEx(@NonNull LoginBean data) {
                        super.onNextEx(data);
                        sessionMessage.setInfo(data);
                        Glide.with(mContext).load(data.getImageUrl()).into(holder.riv_session);
                        holder.tv_name_session.setText(data.getRemark());
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

    public class SessionsHolder extends RecyclerView.ViewHolder {

        public RoundedImageView riv_session;
        public TextView tv_name_session;
        public TextView tv_content_session;
        public TextView tv_time_session;
        public BadgeView bv_session;
        public ProgressBar pb_state_session;

        public SessionsHolder(@NonNull View itemView) {
            super(itemView);
            riv_session = itemView.findViewById(R.id.riv_session);
            tv_name_session = itemView.findViewById(R.id.tv_name_session);
            tv_content_session = itemView.findViewById(R.id.tv_content_session);
            tv_time_session = itemView.findViewById(R.id.tv_time_session);
            bv_session = itemView.findViewById(R.id.bv_session);
            pb_state_session = itemView.findViewById(R.id.pb_state_session);
        }
    }
}
