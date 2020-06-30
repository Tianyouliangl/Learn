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
public class ChatLocationSendHolder extends RecyclerView.ViewHolder {
    public RoundedImageView iv_icon;
    public TextView tv_time;
    public RoundedImageView iv_iv;
    public ProgressBar pb_state;
    public TextView location_title;
    public TextView location_address;

    public ChatLocationSendHolder(@NonNull View itemView) {
        super(itemView);
        iv_icon = itemView.findViewById(R.id.iv_icon);
        tv_time = itemView.findViewById(R.id.tv_chat_time);
        iv_iv = itemView.findViewById(R.id.iv_iv);
        pb_state = itemView.findViewById(R.id.pb_state);
        location_title = itemView.findViewById(R.id.tv_location_title);
        location_address = itemView.findViewById(R.id.tv_location_address);
    }
}
