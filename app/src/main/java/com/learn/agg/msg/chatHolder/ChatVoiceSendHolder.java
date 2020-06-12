package com.learn.agg.msg.chatHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * author : fengzhangwei
 * date : 2019/12/27
 */
public class ChatVoiceSendHolder extends RecyclerView.ViewHolder {
    public RoundedImageView iv_icon;
    public TextView tv_time;
    public ImageView iv_voice;
    public ProgressBar pb_state;
    public TextView tv_voice_time;
    public TextView tv_voice_content;

    public ChatVoiceSendHolder(@NonNull View itemView) {
        super(itemView);
        iv_icon = itemView.findViewById(R.id.iv_icon);
        tv_time = itemView.findViewById(R.id.tv_chat_time);
        iv_voice = itemView.findViewById(R.id.iv_voice);
        pb_state = itemView.findViewById(R.id.pb_state);
        tv_voice_time = itemView.findViewById(R.id.tv_voice_time);
        tv_voice_content = itemView.findViewById(R.id.tv_content);
    }
}
