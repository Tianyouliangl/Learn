package com.learn.agg.msg.chatHolder;

import android.view.View;
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
public class ChatTextHelloHolder extends RecyclerView.ViewHolder {
    public TextView tv_time;
    public TextView tv_content;

    public ChatTextHelloHolder(@NonNull View itemView) {
        super(itemView);
        tv_time = itemView.findViewById(R.id.tv_chat_time);
        tv_content = itemView.findViewById(R.id.tv_content);
    }
}
