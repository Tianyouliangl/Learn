package com.learn.agg.msg.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.learn.agg.R;
import com.learn.agg.msg.chatHolder.ChatEmojiReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatEmojiSendHolder;
import com.learn.agg.msg.chatHolder.ChatGifReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatGifSendHolder;
import com.learn.agg.msg.chatHolder.ChatImageReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatImageSendHolder;
import com.learn.agg.msg.chatHolder.ChatLocationReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatLocationSendHolder;
import com.learn.agg.msg.chatHolder.ChatTextHelloHolder;
import com.learn.agg.msg.chatHolder.ChatTextReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatTextSendHolder;
import com.learn.agg.msg.chatHolder.ChatTextWithdrawHolder;
import com.learn.agg.msg.chatHolder.ChatVoiceReceiveHolder;
import com.learn.agg.msg.chatHolder.ChatVoiceSendHolder;
import com.learn.commonalitylibrary.body.GifBean;
import com.learn.commonalitylibrary.util.FileUpLoadManager;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.body.EmoticonBody;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.TextBody;
import com.learn.commonalitylibrary.body.VoiceBody;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.MediaManager;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.learn.commonalitylibrary.body.LocationBody;

import java.io.File;
import java.util.Iterator;
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
    private itemClickListener mClickListener;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (viewType == TYPE_VOICE_SEND) {
                ((ChatVoiceSendHolder) viewHolder).pb_state.setVisibility(View.GONE);
            }
            if (viewType == TYPE_VOICE_RECEIVE) {
                ((ChatVoiceReceiveHolder) viewHolder).pb_state.setVisibility(View.GONE);
            }
            if (what == 1) {
                play(viewType, voice_url, viewHolder, mChatMessage);
                handler.removeMessages(1);
            }
            if (what == 0) {
                handler.removeMessages(0);
            }
        }
    };

    // 文本
    private final int TYPE_TEXT_SEND = 1;
    private final int TYPE_TEXT_RECEIVE = 2;
    private final int TYPE_TEXT_HELLO = 9;

    //表情
    private final int TYPE_FACE_SEND = 3;
    private final int TYPE_FACE_RECEIVE = 4;

    // 图片
    private final int TYPE_IMAGE_SHEND = 5;
    private final int TYPE_IAMGE_RECEIVE = 6;

    // 语音
    private final int TYPE_VOICE_SEND = 7;
    private final int TYPE_VOICE_RECEIVE = 8;

    //位置
    private final int TYPE_LOCATION_SEND = 10;
    private final int TYPE_LOCATION_RECEIVE = 11;

    //撤回
    private final int TYPE_TEXT_WITHDRAW = 12;

    // 斗图
    private final int TYPE_GIF_SEND = 13;
    private final int TYPE_GIF_RECEIVE = 14;


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
            if (bodyType == ChatMessage.MSG_BODY_TYPE_CANCEL) {
                return TYPE_TEXT_WITHDRAW;
            } else if (fromId.equals(uid)) {
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
                if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT_HELLO) {
                    return TYPE_TEXT_HELLO;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_LOCATION) {
                    return TYPE_LOCATION_SEND;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_GIF) {
                    return TYPE_GIF_SEND;
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
                if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT_HELLO) {
                    return TYPE_TEXT_HELLO;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_LOCATION) {
                    return TYPE_LOCATION_RECEIVE;
                }
                if (bodyType == ChatMessage.MSG_BODY_TYPE_GIF) {
                    return TYPE_GIF_RECEIVE;
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
            case TYPE_TEXT_WITHDRAW:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_withdraw, parent, false);
                viewHolder = new ChatTextWithdrawHolder(view);
                break;
            case TYPE_TEXT_SEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_text, parent, false);
                viewHolder = new ChatTextSendHolder(view);
                break;
            case TYPE_TEXT_HELLO:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_hello_text, parent, false);
                viewHolder = new ChatTextHelloHolder(view);
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
            case TYPE_LOCATION_SEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_location, parent, false);
                viewHolder = new ChatLocationSendHolder(view);
                break;
            case TYPE_GIF_SEND:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_send_gif, parent, false);
                viewHolder = new ChatGifSendHolder(view);
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
            case TYPE_LOCATION_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_location, parent, false);
                viewHolder = new ChatLocationReceiveHolder(view);
                break;
            case TYPE_GIF_RECEIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_receive_gif, parent, false);
                viewHolder = new ChatGifReceiveHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

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
        shoeImageLocation(holder, message);
        onItemClick(holder, message);
        //send error
        onPbClick(holder, message);
    }

    private void onItemClick(final RecyclerView.ViewHolder holder, final ChatMessage message) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bodyType = message.getBodyType();
                int msgStatus = message.getMsgStatus();
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
                    if (msgStatus == ChatMessage.MSG_VOICE_UNREAD) {
                        if (mClickListener != null) {
                            mClickListener.onVoiceRead(message);
                        }
                        message.setMsgStatus(2);
                        switch (viewType) {
                            case TYPE_VOICE_SEND:
                                ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.GONE);
                                break;
                            case TYPE_VOICE_RECEIVE:
                                ((ChatVoiceReceiveHolder) holder).pb_state.setVisibility(View.GONE);
                                break;
                            default:
                                break;
                        }
                        DataBaseHelp.getInstance(mContext).addChatMessage(message);
                    }
                    if (exists) {
                        play(viewType, path, holder, message);
                    } else {
                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.anim);
                        switch (viewType) {
                            case TYPE_VOICE_SEND:
                                ((ChatVoiceSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                                ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                                Log.i("mmm", "不存在，，，开始下载。。。");
                                downLoadFile(url, fileName);
                                break;
                            case TYPE_VOICE_RECEIVE:
                                ((ChatVoiceReceiveHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                                ((ChatVoiceReceiveHolder) holder).pb_state.setVisibility(View.VISIBLE);
                                Log.i("mmm", "不存在，，，开始下载。。。");
                                downLoadFile(url, fileName);
                                break;
                            default:
                                break;
                        }
                    }

                } else if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                    if (mClickListener != null) {
                        String body = message.getBody();
                        ImageBody imageBody = GsonUtil.GsonToBean(body, ImageBody.class);
                        mClickListener.onClickItemImage(message.getPid());
                    }
                } else if (bodyType == ChatMessage.MSG_BODY_TYPE_LOCATION) {
                    if (mClickListener != null) {
                        String body = message.getBody();
                        LocationBody locationBody = GsonUtil.GsonToBean(body, LocationBody.class);
                        mClickListener.onClickLocation(locationBody);
                    }
                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onLongClick(message);
                }
                return true;
            }
        });
    }

    private void onPbClick(RecyclerView.ViewHolder holder, final ChatMessage message) {
        int msgStatus = message.getMsgStatus();
        if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
            switch (holder.getItemViewType()) {
                case TYPE_TEXT_SEND:
                    ((ChatTextSendHolder) holder).pb_state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListener != null) {
                                mClickListener.onClickPb(message);
                            }
                        }
                    });
                    break;
                case TYPE_FACE_SEND:
                    ((ChatEmojiSendHolder) holder).pb_state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListener != null) {
                                mClickListener.onClickPb(message);
                            }
                        }
                    });
                    break;
                case TYPE_IMAGE_SHEND:
                    ((ChatImageSendHolder) holder).pb_state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListener != null) {
                                mClickListener.onClickPb(message);
                            }
                        }
                    });
                    break;
                case TYPE_VOICE_SEND:
                    ((ChatVoiceSendHolder) holder).pb_state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListener != null) {
                                mClickListener.onClickPb(message);
                            }
                        }
                    });
                    break;
                case TYPE_LOCATION_SEND:
                    ((ChatLocationSendHolder) holder).pb_state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListener != null) {
                                mClickListener.onClickPb(message);
                            }
                        }
                    });
                    break;
                case TYPE_GIF_SEND:
                    ((ChatGifSendHolder) holder).pb_state.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickListener != null) {
                                mClickListener.onClickPb(message);
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void downLoadFile(String url, String fileName) {
        new FileUpLoadManager().downloadFile(url, fileName, new FileUpLoadManager.FileDownloadCallBack() {
            @Override
            public void onError(Throwable e) {
                Log.i("mmm", "下载失败---" + e.toString());
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onSuccess(String url) {
                Log.i("mmm", "下载成功---" + url);
                voice_url = url;
                handler.sendEmptyMessage(1);
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

                    if (state > 0 && time > 0) {
                        ((ChatVoiceSendHolder) holder).tv_voice_content.setVisibility(View.VISIBLE);
                        ((ChatVoiceSendHolder) holder).tv_voice_content.setText(voiceContent);
                    } else {
                        ((ChatVoiceSendHolder) holder).tv_voice_content.setVisibility(View.GONE);
                    }
                    ((ChatVoiceSendHolder) holder).tv_voice_time.setText((time / 1000) + "'");
                    break;
                case TYPE_VOICE_RECEIVE:
                    if (state > 0 && time > 0) {
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

    private void showImage(final RecyclerView.ViewHolder holder, ChatMessage message) {
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
        } else if (bodyType == ChatMessage.MSG_BODY_TYPE_GIF) {
            GifBean bean = GsonUtil.GsonToBean(body, GifBean.class);
            String url = bean.getUrl();
            int type = bean.getType();
            switch (holder.getItemViewType()) {
                case TYPE_GIF_SEND:
                    switch (type) {
                        case 5:
                            Glide.with(mContext).asGif().apply(options).load(url).into(((ChatGifSendHolder) holder).iv_iv);
                            break;
                        case 6:
                            Glide.with(mContext).asBitmap().apply(options).load(url).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    resource = eraseColor(resource, -1);
                                    resource = eraseColor(resource, -16777216);
                                    ((ChatGifSendHolder) holder).iv_iv.setImageBitmap(resource);
                                }
                            });
                            break;
                    }
                    break;
                case TYPE_GIF_RECEIVE:
                    switch (type) {
                        case 5:
                            Glide.with(mContext).asGif().load(url).apply(options).into(((ChatGifReceiveHolder) holder).iv_iv);
                            break;
                        case 6:
                            Glide.with(mContext).asBitmap().apply(options).load(url).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    resource = eraseColor(resource, -1);
                                    resource = eraseColor(resource, -16777216);
                                    ((ChatGifReceiveHolder) holder).iv_iv.setImageBitmap(resource);
                                }
                            });
                            break;
                    }
                    break;
            }
        }
    }

    private void shoeImageLocation(RecyclerView.ViewHolder holder, ChatMessage message) {
        int bodyType = message.getBodyType();
        String body = message.getBody();
        if (bodyType == ChatMessage.MSG_BODY_TYPE_LOCATION) {
            LocationBody locationBody = GsonUtil.GsonToBean(body, LocationBody.class);
            String location_url = locationBody.getLocation_url();
            Log.i("ssss", "----location_url----" + location_url);
            String url = locationBody.getUrl();
            switch (holder.getItemViewType()) {

                case TYPE_LOCATION_SEND:
                    File file_send = new File(location_url);
                    if (file_send.exists()) {
                        Glide.with(mContext).load(location_url).into(((ChatLocationSendHolder) holder).iv_iv);
                    } else {
                        Glide.with(mContext).load(url).into(((ChatLocationSendHolder) holder).iv_iv);
                    }
                    break;
                case TYPE_LOCATION_RECEIVE:
                    File file_receive = new File(location_url);
                    if (file_receive.exists()) {
                        Glide.with(mContext).load(location_url).into(((ChatLocationReceiveHolder) holder).iv_iv);
                    } else {
                        Glide.with(mContext).load(url).into(((ChatLocationReceiveHolder) holder).iv_iv);
                    }
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
        if (bodyType == ChatMessage.MSG_BODY_TYPE_CANCEL) {
            String fromId = message.getFromId();
            String uid = from_bean.getUid();
            if (fromId.equals(uid)) {
                ((ChatTextWithdrawHolder) holder).tv_content.setText("你撤回一条消息");
                ((ChatTextWithdrawHolder) holder).iv_close.setVisibility(View.VISIBLE);
            } else {
                ((ChatTextWithdrawHolder) holder).tv_content.setText("对方撤回一条消息");
                ((ChatTextWithdrawHolder) holder).iv_close.setVisibility(View.GONE);
            }
        } else if (bodyType == ChatMessage.MSG_BODY_TYPE_TEXT || bodyType == ChatMessage.MSG_BODY_TYPE_TEXT_HELLO) {
            switch (holder.getItemViewType()) {
                case TYPE_TEXT_SEND:
                    TextBody content_text_send = GsonUtil.GsonToBean(body, TextBody.class);
                    ((ChatTextSendHolder) holder).tv_content.setText(content_text_send.getMsg());
                    break;
                case TYPE_TEXT_HELLO:
                    TextBody content_text_hello = GsonUtil.GsonToBean(body, TextBody.class);
                    ((ChatTextHelloHolder) holder).tv_content.setText(content_text_hello.getMsg());
                    break;
                case TYPE_TEXT_RECEIVE:
                    TextBody content_text_receive = GsonUtil.GsonToBean(body, TextBody.class);
                    ((ChatTextReceiveHolder) holder).tv_content.setText(content_text_receive.getMsg());
                    break;
                default:
                    break;
            }
        } else if (bodyType == ChatMessage.MSG_BODY_TYPE_LOCATION) {
            switch (holder.getItemViewType()) {
                case TYPE_LOCATION_SEND:
                    LocationBody locationBody_send = GsonUtil.GsonToBean(body, LocationBody.class);
                    ((ChatLocationSendHolder) holder).location_address.setText(locationBody_send.getTitle());
                    break;
                case TYPE_LOCATION_RECEIVE:
                    LocationBody locationBody_receive = GsonUtil.GsonToBean(body, LocationBody.class);
                    ((ChatLocationReceiveHolder) holder).location_address.setText(locationBody_receive.getTitle());
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
                } else if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
                    ((ChatTextSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                    ((ChatTextSendHolder) holder).pb_state.setProgressDrawable(drawable);
                    ((ChatTextSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_FACE_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatEmojiSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatEmojiSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                } else if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
                    ((ChatEmojiSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                    ((ChatEmojiSendHolder) holder).pb_state.setProgressDrawable(drawable);
                    ((ChatEmojiSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_IMAGE_SHEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatImageSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatImageSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                } else if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
                    ((ChatImageSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                    ((ChatImageSendHolder) holder).pb_state.setProgressDrawable(drawable);
                    ((ChatImageSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_VOICE_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                } else if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
                    ((ChatVoiceSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                    ((ChatVoiceSendHolder) holder).pb_state.setProgressDrawable(drawable);
                    ((ChatVoiceSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_LOCATION_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatLocationSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatLocationSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                } else if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
                    ((ChatLocationSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                    ((ChatLocationSendHolder) holder).pb_state.setProgressDrawable(drawable);
                    ((ChatLocationSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_GIF_SEND:
                if (msgStatus == ChatMessage.MSG_SEND_SUCCESS) {
                    ((ChatGifSendHolder) holder).pb_state.setVisibility(View.GONE);
                } else if (msgStatus == ChatMessage.MSG_SEND_LOADING) {
                    ((ChatGifSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                } else if (msgStatus == ChatMessage.MSG_SEND_ERROR) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_send_error);
                    ((ChatGifSendHolder) holder).pb_state.setIndeterminateDrawable(drawable);
                    ((ChatGifSendHolder) holder).pb_state.setProgressDrawable(drawable);
                    ((ChatGifSendHolder) holder).pb_state.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_VOICE_RECEIVE:
                if (msgStatus == ChatMessage.MSG_VOICE_UNREAD) {
                    ((ChatVoiceReceiveHolder) holder).pb_state.setVisibility(View.VISIBLE);
                } else {
                    ((ChatVoiceReceiveHolder) holder).pb_state.setVisibility(View.GONE);
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
            case TYPE_TEXT_WITHDRAW:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatTextWithdrawHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatTextWithdrawHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatTextWithdrawHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_TEXT_SEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatTextSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatTextSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatTextSendHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_TEXT_HELLO:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatTextHelloHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatTextHelloHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatTextHelloHolder) holder).tv_time.setVisibility(View.GONE);
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
            case TYPE_LOCATION_SEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatLocationSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatLocationSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatLocationSendHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_GIF_SEND:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatGifSendHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatGifSendHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatGifSendHolder) holder).tv_time.setVisibility(View.GONE);
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
            case TYPE_LOCATION_RECEIVE:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatLocationReceiveHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatLocationReceiveHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatLocationReceiveHolder) holder).tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_GIF_RECEIVE:
                if (displaytime == ChatMessage.MSG_TIME_TRUE) {
                    ((ChatGifReceiveHolder) holder).tv_time.setVisibility(View.VISIBLE);
                    ((ChatGifReceiveHolder) holder).tv_time.setText(chatTime);
                } else {
                    ((ChatGifReceiveHolder) holder).tv_time.setVisibility(View.GONE);
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
            case TYPE_LOCATION_SEND:
                Glide.with(mContext).load(from_ImageUrl).into(((ChatLocationSendHolder) holder).iv_icon);
                break;
            case TYPE_GIF_SEND:
                Glide.with(mContext).load(from_ImageUrl).into(((ChatGifSendHolder) holder).iv_icon);
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
            case TYPE_LOCATION_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatLocationReceiveHolder) holder).iv_icon);
                break;
            case TYPE_GIF_RECEIVE:
                Glide.with(mContext).load(to_imageUrl).into(((ChatGifReceiveHolder) holder).iv_icon);
                break;
            default:
                break;
        }
    }

    public void setData(List<ChatMessage> list) {
        mList.addAll(0, list);
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
                break;
            }
        }
        if (isAdd) {
            mList.add(message);
        } else {
            notifyChatMessage(message);
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
                if (pid1.equals(pid) && status != msgStatus) {
                    mList.get(i).setMsgStatus(status);
                    mList.get(i).setBody(body);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void removeItem(String pid) {
        if (pid.isEmpty()) return;
        Iterator<ChatMessage> iterator = mList.iterator();
        while (iterator.hasNext()) {
            ChatMessage next = iterator.next();
            if (next.getPid().equals(pid)) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
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

    private void play(int viewType, String path, RecyclerView.ViewHolder holder, final ChatMessage message) {
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
                        if (playPid.equals(message.getPid())) {
                            animation.start();
                            MediaManager.resume();
                            break;
                        } else {
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
                        if (playPid.equals(message.getPid())) {
                            animation.start();
                            MediaManager.resume();
                            break;
                        } else {
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


    //BitmapUtil中擦除Bitmap像素的方法
    private Bitmap eraseColor(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }
        b.setPixels(pixels, 0, width, 0, 0, width, height);
        return b;
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
                switch (viewType) {
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

    public interface itemClickListener {
        void onClickItemImage(String image_url);

        void onVoiceRead(ChatMessage message);

        void onClickLocation(LocationBody body);

        void onLongClick(ChatMessage message);

        void onClickPb(ChatMessage message);
    }

    public void setOnItemClickListener(itemClickListener listener) {
        this.mClickListener = listener;
    }


}
