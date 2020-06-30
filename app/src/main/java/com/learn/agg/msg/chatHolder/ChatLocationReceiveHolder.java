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
public class ChatLocationReceiveHolder extends RecyclerView.ViewHolder {
    public RoundedImageView iv_icon;
    public TextView tv_time;
    public RoundedImageView iv_iv;
    public TextView location_title;
    public TextView location_address;

    public ChatLocationReceiveHolder(@NonNull View itemView) {
        super(itemView);
        iv_icon = itemView.findViewById(R.id.iv_icon);
        tv_time = itemView.findViewById(R.id.tv_chat_time);
        iv_iv = itemView.findViewById(R.id.iv_iv);
        location_title = itemView.findViewById(R.id.tv_location_title);
        location_address = itemView.findViewById(R.id.tv_location_address);
    }
}
