package com.learn.agg.msg.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learn.agg.R;
import com.learn.agg.msg.act.ChatActivity;
import com.learn.agg.net.base.BaseObserverTC;
import com.learn.agg.net.base.IHttpProtocol;
import com.learn.agg.widgets.BadgeView;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.body.TextBody;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.senyint.ihospital.client.HttpFactory;
import com.white.easysp.EasySP;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionsHolder> {
    private Context mContext;
    private List<SessionMessage> mList;
    private long lastClickTime = 0;
    private Boolean isOpenMenu = false;
    private MenuClickListener mListener;

    public SessionsAdapter(Context context, List<SessionMessage> list) {
        mContext = context;
        mList = list;
    }

    public void setData(List<SessionMessage> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setData(SessionMessage message) {
        mList.add(message);
        notifyDataSetChanged();
    }

    public List<SessionMessage> getData() {
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
        final SessionMessage sessionMessage = mList.get(position);
        LoginBean userData = sessionMessage.getInfo();
        if (null != userData) {
            sessionMessage.setInfo(userData);
            RequestOptions options;
            String remark = userData.getRemark();
            holder.tv_name_session.setText(remark);
            if (userData.getSex().equals("男")) {
                options = new RequestOptions()
                        .placeholder(R.drawable.icon_t_na)//图片加载出来前，显示的图片
                        .fallback(R.drawable.icon_t_na) //url为空的时候,显示的图片
                        .error(R.drawable.icon_t_na);//图片加载失败后，显示的图片
            } else {
                options = new RequestOptions()
                        .placeholder(R.drawable.icon_t_nv)//图片加载出来前，显示的图片
                        .fallback(R.drawable.icon_t_nv) //url为空的时候,显示的图片
                        .error(R.drawable.icon_t_nv);//图片加载失败后，显示的图片
            }
            Glide.with(mContext).load(userData.getImageUrl()).apply(options).into(holder.riv_session);
        }
        String body = mList.get(position).getBody();
        int body_type = mList.get(position).getBody_type();
        int msg_status = mList.get(position).getMsg_status();
        int number = mList.get(position).getNumber();
        Long time = mList.get(position).getTime();
        holder.tv_content_session.setText(ImSendMessageUtils.getChatBodyType(mContext, sessionMessage.getTo_id(), body_type, body));
        holder.tv_time_session.setText(TimeUtil.getTimeString(time));
        holder.bv_session.showBadge(number);
        if (number > 0) {
            holder.btnUnRead.setText("标记已读");
        } else {
            holder.btnUnRead.setText("标记未读");
        }
        if (msg_status == ChatMessage.MSG_SEND_LOADING) {
            holder.pb_state_session.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.anim);
            holder.pb_state_session.setIndeterminateDrawable(drawable);
            holder.pb_state_session.setProgressDrawable(drawable);
        } else if (msg_status == ChatMessage.MSG_SEND_SUCCESS || msg_status == ChatMessage.MSG_VOICE_UNREAD) {
            holder.pb_state_session.setVisibility(View.GONE);
        } else if (msg_status == ChatMessage.MSG_SEND_ERROR) {
            holder.pb_state_session.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
            holder.pb_state_session.setIndeterminateDrawable(drawable);
            holder.pb_state_session.setProgressDrawable(drawable);
        }
//        holder.itemView.invalidate();
        holder.rl_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenMenu) {
                    final long time = System.currentTimeMillis();
                    long timeD = time - lastClickTime;
                    final LoginBean loginBean = sessionMessage.getInfo();
                    if (timeD <= 1000 || null == loginBean) {
                        lastClickTime = time;
                        Log.i("net", "userInfo is not null or click time <= 1s");
                        return;
                    }
                    ChatActivity.startActivity(mContext, loginBean.getUid(), sessionMessage.getConversation(), loginBean);
                    lastClickTime = time;
                    return;
                }
                holder.sw_menu_layout.smoothClose();
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sw_menu_layout.smoothClose();
                if (mListener != null) {
                    mListener.onDeleteListener(position);
                }
            }
        });

        holder.btnUnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sw_menu_layout.smoothClose();
                if (mListener != null) {
                    String text = holder.btnUnRead.getText().toString().trim();
                    if (text.contains("已读")) {
                        DataBaseHelp.getInstance(mContext).setSessionNumber(sessionMessage.getConversation(), 0);
                        mListener.onRedListener();
                        return;
                    }
                    if (text.contains("未读")) {
                        DataBaseHelp.getInstance(mContext).setSessionNumber(sessionMessage.getConversation(), 1);
                        mListener.onRedListener();
                        return;
                    }
                }
            }
        });

        holder.sw_menu_layout.setOnMenuOpenListener(new SwipeMenuLayout.OpenListener() {
            @Override
            public void onMenuIsOpen(Boolean isOpen) {
                isOpenMenu = isOpen;
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

    public Boolean getIsOpenMenu() {
        return isOpenMenu;
    }

    public interface MenuClickListener {
        void onDeleteListener(int position);

        void onRedListener();
    }

    public void setOnMenuClickListener(MenuClickListener listener) {
        this.mListener = listener;
    }

    public class SessionsHolder extends RecyclerView.ViewHolder {

        public RoundedImageView riv_session;
        public TextView tv_name_session;
        public TextView tv_content_session;
        public TextView tv_time_session;
        public BadgeView bv_session;
        public ProgressBar pb_state_session;
        public RelativeLayout rl_chat;
        public SwipeMenuLayout sw_menu_layout;
        public Button btnDelete;
        public Button btnUnRead;

        public SessionsHolder(@NonNull View itemView) {
            super(itemView);
            riv_session = itemView.findViewById(R.id.riv_session);
            tv_name_session = itemView.findViewById(R.id.tv_name_session);
            tv_content_session = itemView.findViewById(R.id.tv_content_session);
            tv_time_session = itemView.findViewById(R.id.tv_time_session);
            bv_session = itemView.findViewById(R.id.bv_session);
            pb_state_session = itemView.findViewById(R.id.pb_state_session);
            rl_chat = itemView.findViewById(R.id.rl_chat);
            sw_menu_layout = itemView.findViewById(R.id.sw_menu_layout);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUnRead = itemView.findViewById(R.id.btnUnRead);
        }
    }
}
