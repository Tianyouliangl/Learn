package com.learn.agg.msg.chatHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;

/**
 * author : fengzhangwei
 * date : 2019/12/27
 */
public class ChatTextWithdrawHolder extends RecyclerView.ViewHolder {
    public TextView tv_time;
    public TextView tv_content;
    public ImageView iv_close;

    public ChatTextWithdrawHolder(@NonNull View itemView) {
        super(itemView);
        tv_time = itemView.findViewById(R.id.tv_chat_time);
        tv_content = itemView.findViewById(R.id.tv_content);
        iv_close = itemView.findViewById(R.id.iv_iv);
    }
}
