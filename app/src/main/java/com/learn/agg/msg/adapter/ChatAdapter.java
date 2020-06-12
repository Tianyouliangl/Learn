package com.learn.agg.msg.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learn.agg.R;
import com.learn.agg.msg.chatHolder.ChatEmojiReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatEmojiSendHolder;
import com.learn.agg.msg.chatHolder.ChatImageReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatImageSendHolder;
import com.learn.agg.msg.chatHolder.ChatTextReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatTextSendHolder;
import com.learn.agg.msg.chatHolder.ChatVoiceReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatVoiceSendHolder;
import com.learn.agg.net.bean.LoginBean;
import com.learn.agg.widgets.FileUpLoadManager;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.body.EmoticonBody;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.TextBody;
import com.learn.commonalitylibrary.body.VoiceBody;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.MediaManager;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.lib.xiangxiang.im.ImSocketClient;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mList;
    private Context mContext;
    private LoginBean from_bean;
    private LoginBean to_Bean;
    private Boolean isPlay = false;
    private Boolean isPause = false;
    private String playPid;
    private AnimationDrawable animation;

    private int viewType;
    private RecyclerView.ViewHolder viewHolder;
    private String voice_url;
    private ChatMessage mChatMessage;
    private Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what =  msg.what;
            if (viewType == TYPE_VOICE_SEND) {
                ((ChatVoiceSendHolder) viewHolder).pb_state.setVisibility(View.GONE);
            }
            if (viewType == TYPE_VOICE_RECEIVE){
                ((ChatVoiceReceiveHolder) viewHolder).pb_state.setVisibility(View.GONE);
            }
            if (what == 1){
                play(viewType,voice_url,viewHolder,mChatMessage);
                handler.removeMessages(1);
            }
            if (what == 0){
                handler.removeMessages(0);
            }
        }
    };

    // 文本
    private final int TYPE_TEXT_SEND = 1;
    private final int TYPE_TEXT_RECEIVE = 2;

    //表情
    private final int TYPE_FACE_SEND = 3;
    private final int TYPE_FACE_RECEIVE = 4;

    // 图片
    private final int TYPE_IMAGE_SHEND = 5;
    private final int TYPE_IAMGE_RECEIVE = 6;

    // 语音
    private final int TYPE_VOICE_SEND = 7;
    private final int TYPE_VOICE_RECEIVE = 8;


    public ChatAdapter(Context context, List<ChatMessage> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() > 0) {
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
                if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                    return TYPE_IMAGE_SHEND;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_VOICE) {
                    return TYPE_VOICE_SEND;
                }
            } else {
                if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT) {
                    return TYPE_TEXT_RECEIVE;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI) {
                    return TYPE_FACE_RECEIVE;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                    return TYPE_IAMGE_RECEIVE;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_VOICE) {
                    return TYPE_VOICE_RECEIVE;
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
            case TYPE_IMAGE_SHEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_image, parent, false);
                viewHolder = new ChatImageSendHolder(view);
                break;
            case TYPE_VOICE_SEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_voice, parent, false);
                viewHolder = new ChatVoiceSendHolder(view);
                break;
            case TYPE_TEXT_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_text, parent, false);
                viewHolder = new ChatTextReceiveHolder(view);
                break;
            case TYPE_FACE_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_emoji, parent, false);
                viewHolder = new ChatEmojiReceiveHolder(view);
                break;
            case TYPE_IAMGE_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_image, parent, false);
                viewHolder = new ChatImageReceiveHolder(view);
                break;
            case TYPE_VOICE_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_voice, parent, false);
                viewHolder = new ChatVoiceReceiveHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ChatMessage message = mList.get(position);

        // 公共的
        showIcon(holder);
        whetherShowTime(holder, message);
        whetherShowPb(holder, message);
        // 多条目
        showContent(holder, message);
        showEmoji(holder, message);
        showImage(holder, message);
        showVoice(holder, message);

        onItemClick(holder, message);
    }

    private void onItemClick(final RecyclerView.ViewHolder holder, final ChatMessage message) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bodyType = message.getBodyType();
                mChatMessage = message;
                if (bodyType == ChatMessage.MSG_BODY_TYPE_VOICE) {
                    viewType = holder.getItemViewType();
                    viewHolder = holder;
                    String body = message.getBody();
                    VoiceBody bean = GsonUtil.GsonToBean(body, VoiceBody.class);
                    final String path = bean.getFileAbsPath();
                    String fileName = bean.getFileName();
                    String url = bean.getUrl();
                    Boolean exists = FileUpLoadManager.whereExists(path);
                    if (exists) {
                       play(viewType,path,holder,message);
                    } else {
                        switch (viewType) {
                            case TYPE_VOICE_SEND:
                                ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                                Log.i("mmm","不存在，，，开始下载。。。");
                                new FileUpLoadManager().downloadFile(url, fileName, new FileUpLoadManager.FileDownloadCallBack() {
                                    @Override
                                    public void onError(Throwable e) {
//                                        Toast.makeText(mContext, "下载失败!", Toast.LENGTH_SHORT).show();
                                        Log.i("mmm","下载失败---" + e.toString());
                                        handler.sendEmptyMessage(0);
                                    }

                                    @Override
                                    public void onSuccess(String url) {
                                        Log.i("mmm","下载成功---" + url);
                                        voice_url = url;
                                        handler.sendEmptyMessage(1);
                                    }
                                });
                                break;
                            case TYPE_VOICE_RECEIVE:
                                ((ChatVoiceReceiveHolder) holder).pb_state.setVisibility(View.VISIBLE);
                                Log.i("mmm","不存在，，，开始下载。。。");
                                new FileUpLoadManager().downloadFile(url, fileName, new FileUpLoadManager.FileDownloadCallBack() {
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i("mmm","下载失败---" + e.toString());
                                        handler.sendEmptyMessage(0);
                                    }

                                    @Override
                                    public void onSuccess(String url) {
                                        Log.i("mmm","下载成功---" + url);
                                        voice_url = url;
                                        handler.sendEmptyMessage(1);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }

                }

            }
        });
    }


    private void showVoice(RecyclerView.ViewHolder holder, ChatMessage message) {
        int bodyType = message.getBodyType();
        String body = message.getBody();
        if (bodyType == ChatMessage.MSG_BODY_TYPE_VOICE) {
            VoiceBody voiceBody = GsonUtil.GsonToBean(body, VoiceBody.class);
            long time = voiceBody.getTime();
            int state = voiceBody.getState();
            String voiceContent = voiceBody.getVoice_content();
            switch (holder.getItemViewType()) {
                case TYPE_VOICE_SEND:

                    if (state > 0) {
                        ((ChatVoiceSendHolder) holder).tv_voice_content.setVisibility(View.VISIBLE);
                        ((ChatVoiceSendHolder) holder).tv_voice_content.setText(voiceContent);
                    } else {
                        ((ChatVoiceSendHolder) holder).tv_voice_content.setVisibility(View.GONE);
                    }
                    ((ChatVoiceSendHolder) holder).tv_voice_time.setText((time / 1000) + "'");
                    break;
                case TYPE_VOICE_RECEIVE:
                    if (state > 0) {
                        ((ChatVoiceReceiveHolder) holder).tv_voice_content.setVisibility(View.VISIBLE);
                        ((ChatVoiceReceiveHolder) holder).tv_voice_content.setText(voiceContent);
                    } else {
                        ((ChatVoiceReceiveHolder) holder).tv_voice_content.setVisibility(View.GONE);
                    }
                    ((ChatVoiceReceiveHolder) holder).tv_voice_time.setText((time / 1000) + "'");
                    break;
            }
        }
    }

    private void showImage(RecyclerView.ViewHolder holder, ChatMessage message) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.icon_iv_loading)//图片加载出来前，显示的图片
                .fallback(R.drawable.icon_iv_error) //url为空的时候,显示的图片
                .error(R.drawable.icon_iv_error);//图片加载失败后，显示的图片
        int bodyType = message.getBodyType();
        String body = message.getBody();
        if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
            ImageBody bean = GsonUtil.GsonToBean(body, ImageBody.class);
            String image_url = bean.getImage();
            switch (holder.getItemViewType()) {
                case TYPE_IMAGE_SHEND:
                    Glide.with(mContext).load(image_url).apply(options).into(((ChatImageSendHolder) holder).iv_iv);
                    break;
                case TYPE_IAMGE_RECEIVE:
                    Glide.with(mContext).load(image_url).apply(options).into(((ChatImageReceiveHolder) holder).iv_iv);
                    break;
                default:
                    break;
            }
        }
    }

    private void showEmoji(RecyclerView.ViewHolder holder, ChatMessage message) {
        int bodyType = message.getBodyType();
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.icon_bq_loading)//图片加载出来前，显示的图片
                .fallback(R.drawable.icon_bq_error) //url为空的时候,显示的图片
                .error(R.drawable.icon_bq_error);//图片加载失败后，显示的图片
        EmoticonBody body = GsonUtil.GsonToBean(message.getBody(), EmoticonBody.class);
        String url = body.getUrl();
        if (bodyType == ChatMessage.MSG_BODY_TYPE_EMOJI) {
            switch (holder.getItemViewType()) {
                case TYPE_FACE_SEND:
                    Glide.with(mContext).load(url).apply(options).into(((ChatEmojiSendHolder) holder).iv_emoji);
                    break;
                case TYPE_FACE_RECEIVE:
                    Glide.with(mContext).load(url).apply(options).into(((ChatEmojiReceiveHolder) holder).iv_emoji);
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

    private void whetherShowPb(RecyclerView.ViewHolder holder, ChatMessage message) {
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
            case TYPE_IMAGE_SHEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatImageSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatImageSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_VOICE_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
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
            case TYPE_IMAGE_SHEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatImageSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatImageSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatImageSendHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_VOICE_SEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatVoiceSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatVoiceSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatVoiceSendHolder) holder).tv_time.setVisibility(View.GONE);
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
            case TYPE_IAMGE_RECEIVE:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatImageReceiveHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatImageReceiveHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatImageReceiveHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_VOICE_RECEIVE:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatVoiceReceiveHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatVoiceReceiveHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatVoiceReceiveHolder) holder).tv_time.setVisibility(View.GONE);
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
            case TYPE_IMAGE_SHEND:
                Glide.with(mContext).load(from_ImageUrl).into(((ChatImageSendHolder) holder).iv_icon);
                break;
            case TYPE_VOICE_SEND:
                Glide.with(mContext).load(from_ImageUrl).into(((ChatVoiceSendHolder) holder).iv_icon);
                break;
            case TYPE_TEXT_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatTextReceiveHolder) holder).iv_icon);
                break;
            case TYPE_FACE_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatEmojiReceiveHolder) holder).iv_icon);
                break;
            case TYPE_IAMGE_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatImageReceiveHolder) holder).iv_icon);
                break;
            case TYPE_VOICE_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatVoiceReceiveHolder) holder).iv_icon);
                break;
            default:
                break;
        }
    }

    public void setData(List<ChatMessage> list) {
        mList.addAll(0, list);
        Log.i(ImSocketClient.TAG, "add到的数据大小----" + mList.size());
        notifyDataSetChanged();
    }

    public List<ChatMessage> getData() {
        return mList;
    }

    public void setData(ChatMessage message) {
        Boolean isAdd = true;
        String pid = message.getPid();
        for (int i = mList.size() - 1; i >= 0; i--) {
            ChatMessage chatMessage = mList.get(i);
            String messagePid = chatMessage.getPid();
            if (messagePid.equals(pid)) {
                isAdd = false;
                return;
            }
        }
        Log.i(ImSocketClient.TAG, "是否能添加----" + isAdd);
        if (isAdd) {
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
            String body = message.getBody();
            for (int i = mList.size() - 1; i >= 0; i--) {
                String pid1 = mList.get(i).getPid();
                int msgStatus = mList.get(i).getMsgStatus();
                String body1 = mList.get(i).getBody();
                if (pid1.equals(pid) && status != msgStatus) {
                    mList.get(i).setMsgStatus(status);
                    if (!body.equals(body1)) {
                        mList.get(i).setBody(body);
                    }
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

    public void pauseMedia() {
        MediaManager.pause();
        MediaManager.release();
    }

    private void play(int viewType,String path,RecyclerView.ViewHolder holder, final ChatMessage message) {
        switch (viewType) {
            case TYPE_VOICE_SEND:
                if (path == null) {
                    break;
                }
                ((ChatVoiceSendHolder) holder).iv_voice.setImageResource(R.drawable.voice_right_animations);
                animation = (AnimationDrawable) ((ChatVoiceSendHolder) holder).iv_voice.getDrawable();
                if (!isPlay) {
                    if (!isPause) {
                        animation.start();
                        playMedia(holder, animation, path, message);
                        break;
                    } else {
                        if (playPid.equals(message.getPid())){
                            animation.start();
                            MediaManager.resume();
                            break;
                        }else {
                            MediaManager.pause();
                            MediaManager.release();
                            animation.start();
                            playMedia(holder, animation, path, message);
                            break;
                        }

                    }
                } else {
                    if (playPid.equals(message.getPid())) {
                        MediaManager.pause();
                        break;
                    } else {
                        MediaManager.pause();
                        MediaManager.release();
                        animation.start();
                        playMedia(holder, animation, path, message);
                        break;
                    }
                }
            case TYPE_VOICE_RECEIVE:
                ((ChatVoiceReceiveHolder) holder).iv_voice.setImageResource(R.drawable.voice_left_animations);
                animation = (AnimationDrawable) ((ChatVoiceReceiveHolder) holder).iv_voice.getDrawable();
                if (!isPlay) {
                    if (!isPause) {
                        animation.start();
                        playMedia(holder, animation, path, message);
                        break;
                    } else {
                        if (playPid.equals(message.getPid())){
                            animation.start();
                            MediaManager.resume();
                            break;
                        }else {
                            MediaManager.pause();
                            MediaManager.release();
                            animation.start();
                            playMedia(holder, animation, path, message);
                            break;
                        }
                    }
                } else {
                    if (playPid.equals(message.getPid())) {
                        MediaManager.pause();
                        break;
                    } else {
                        MediaManager.pause();
                        MediaManager.release();
                        animation.start();
                        playMedia(holder, animation, path, message);
                        break;
                    }
                }

        }
    }

    private void playMedia(final RecyclerView.ViewHolder holder, final AnimationDrawable animation, final String playPath, final ChatMessage message) {
        MediaManager.playSound(playPath, new MediaManager.onPlayListener() {
            @Override
            public void onStart() {
                isPlay = true;
                isPause = false;
                playPid = message.getPid();
                Log.i("mmm", "onStart");
            }

            @Override
            public void onPause() {
                animation.stop();
                isPlay = false;
                isPause = true;
                Log.i("mmm", "onPause");
            }

            @Override
            public void OnCompletion(MediaPlayer mp) {
                MediaManager.release();
            }

            @Override
            public void onRelease() {
                animation.stop();
                playPid = null;
                isPlay = false;
                isPause = false;
                int viewType = holder.getItemViewType();
                switch (viewType){
                    case TYPE_VOICE_SEND:
                        ((ChatVoiceSendHolder) holder).iv_voice.setImageResource(R.drawable.icon_right_voice_three);
                        break;
                    case TYPE_VOICE_RECEIVE:
                        ((ChatVoiceReceiveHolder) holder).iv_voice.setImageResource(R.drawable.icon_left_voice_three);
                        break;

                }

                Log.i("mmm", "onRelease");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null && mList.size() > 0) {
            return mList.size();
        }
        return 0;
    }
}
