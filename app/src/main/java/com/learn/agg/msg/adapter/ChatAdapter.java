package com.learn.agg.msg.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.learn.agg.msg.chatHolder.ChatEmojiReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatEmojiSendHolder;
import com.learn.agg.msg.chatHolder.ChatTextReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatTextSendHolder;
import com.learn.agg.net.bean.LoginBean;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.body.EmoticonBody;
import com.learn.commonalitylibrary.body.TextBody;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.lib.xiangxiang.im.ImSocketClient;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mList;
    private Context mContext;
    private LoginBean from_bean;
    private LoginBean to_Bean;

    // 文本
    private final int TYPE_TEXT_SEND = 1;
    private final int TYPE_TEXT_RECEIVE = 2;

    //表情
    private final int TYPE_FACE_SEND = 3;
    private final int TYPE_FACE_RECEIVE = 4;


    public ChatAdapter(Context context, List<ChatMessage> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() > 0){
            ChatMessage message = mList.get(position);
            String fromId = message.getFromId();
            String uid = from_bean.getUid();
            int bodyType = message.getBodyType();
            if (fromId.equals(uid)) {
                if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT) {
                    return TYPE_TEXT_SEND;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI) {
                    return TYPE_FACE_SEND;
                }
            } else {
                if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT) {
                    return TYPE_TEXT_RECEIVE;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI) {
                    return TYPE_FACE_RECEIVE;
                }
            }
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case TYPE_TEXT_SEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_text, parent, false);
                viewHolder = new ChatTextSendHolder(view);
                break;
            case TYPE_FACE_SEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_emoji, parent, false);
                viewHolder = new ChatEmojiSendHolder(view);
                break;
            case TYPE_TEXT_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_text, parent, false);
                viewHolder = new ChatTextReceiveHolder(view);
                break;
            case TYPE_FACE_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_emoji, parent, false);
                viewHolder = new ChatEmojiReceiveHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ChatMessage message = mList.get(position);
        showIcon(holder);
        showContent(holder, message);
        showEmoji(holder,message);
        whetherShowTime(holder, message);
        whetherShowLoading(holder, message);
    }

    private void showEmoji(RecyclerView.ViewHolder holder, ChatMessage message) {
        int bodyType = message.getBodyType();
        EmoticonBody body = GsonUtil.GsonToBean(message.getBody(), EmoticonBody.class);
        String url = body.getUrl();
        if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI){
            switch (holder.getItemViewType()){
                case TYPE_FACE_SEND:
                    Glide.with(mContext).load(url).into(((ChatEmojiSendHolder) holder).iv_emoji);
                    break;
                case TYPE_FACE_RECEIVE:
                    Glide.with(mContext).load(url).into(((ChatEmojiReceiveHolder) holder).iv_emoji);
                    break;
                default:
                    break;
            }
        }
    }

    private void showContent(RecyclerView.ViewHolder holder, ChatMessage message) {
        String body = message.getBody();
        int bodyType = message.getBodyType();
        if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT) {
            TextBody content = GsonUtil.GsonToBean(body, TextBody.class);
            switch (holder.getItemViewType()) {
                case TYPE_TEXT_SEND:
                    ((ChatTextSendHolder) holder).tv_content.setText(content.getMsg());
                    break;
                case TYPE_TEXT_RECEIVE:
                    ((ChatTextReceiveHolder) holder).tv_content.setText(content.getMsg());
                    break;
                default:
                    break;
            }
        }
    }

    private void whetherShowLoading(RecyclerView.ViewHolder holder, ChatMessage message) {
        int msgStatus = message.getMsgStatus();
        switch (holder.getItemViewType()) {
            case TYPE_TEXT_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatTextSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatTextSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_FACE_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatEmojiSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatEmojiSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void whetherShowTime(RecyclerView.ViewHolder holder, ChatMessage message) {
        String chatTime = TimeUtil.getTimeString(message.getTime());
        int displaytime = message.getDisplaytime();
        switch (holder.getItemViewType()) {
            case TYPE_TEXT_SEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatTextSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatTextSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatTextSendHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_FACE_SEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatEmojiSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatEmojiSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatEmojiSendHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_TEXT_RECEIVE:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatTextReceiveHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatTextReceiveHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatTextReceiveHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_FACE_RECEIVE:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatEmojiReceiveHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatEmojiReceiveHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatEmojiReceiveHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void showIcon(RecyclerView.ViewHolder holder) {
        String from_ImageUrl = from_bean.getImageUrl();
        String to_imageUrl = to_Bean.getImageUrl();
        switch (holder.getItemViewType()) {
            case TYPE_TEXT_SEND:
                Glide.with(mContext).load(from_ImageUrl).into(((ChatTextSendHolder) holder).iv_icon);
                break;
            case TYPE_FACE_SEND:
                Glide.with(mContext).load(from_ImageUrl).into(((ChatEmojiSendHolder) holder).iv_icon);
                break;
            case TYPE_TEXT_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatTextReceiveHolder) holder).iv_icon);
                break;
            case TYPE_FACE_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatEmojiReceiveHolder) holder).iv_icon);
                break;
        }
    }

    public void setData(List<ChatMessage> list) {
        mList.addAll(0, list);
        Log.i(ImSocketClient.TAG,"add到的数据大小----" + mList.size());
        notifyDataSetChanged();
    }

    public List<ChatMessage> getData() {
        return mList;
    }

    public void setData(ChatMessage message) {
        Boolean isAdd = true;
        String pid = message.getPid();
        for (int i = mList.size() - 1; i >= 0; i--){
            ChatMessage chatMessage = mList.get(i);
            String messagePid = chatMessage.getPid();
            if (messagePid.equals(pid)){
                isAdd = false;
                return;
            }
        }
        Log.i(ImSocketClient.TAG,"是否能添加----" + isAdd);
        if (isAdd){
            mList.add(message);
        }
    }

    public int getLastItemDisplayTime() {
        int position = 0;
        if (mList.size() > 0) {
            for (int i = mList.size() - 1; i >= 0; i--) {
                ChatMessage message = mList.get(i);
                int displayTime = message.getDisplaytime();
                if (displayTime == ChatMessage.MSG_TIME_TRUE) {
                    position = i;
                    break;
                }
            }
            ChatMessage message = mList.get(position);
            Long time = message.getTime();
            Boolean expend = TimeUtil.getTimeExpend(System.currentTimeMillis(), time);
            if (expend) {
                return ChatMessage.MSG_TIME_FALSE;
            } else {
                return ChatMessage.MSG_TIME_TRUE;
            }
        } else {
            return ChatMessage.MSG_TIME_TRUE;
        }
    }

    public void notifyChatMessage(ChatMessage message) {
        if (mList.size() > 0) {
            String pid = message.getPid();
            int status = message.getMsgStatus();
            for (int i = mList.size() - 1; i >= 0; i--) {
                String pid1 = mList.get(i).getPid();
                if (pid1.equals(pid)) {
                    mList.get(i).setMsgStatus(status);
                    notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    public void setToUserBean(LoginBean bean) {
        if (bean == null) return;
        to_Bean = bean;
        notifyDataSetChanged();
    }

    public void setFromUserBean(LoginBean bean) {
        if (bean == null) return;
        from_bean = bean;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mList != null && mList.size() > 0) {
            return mList.size();
        }
        return 0;
    }
}
