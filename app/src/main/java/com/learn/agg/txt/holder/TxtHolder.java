package com.learn.agg.txt.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class TxtHolder extends RecyclerView.ViewHolder {

    public RoundedImageView iv_book_bg;
    public TextView tv_content_center;
    public TextView tv_content_bottom;
    public CheckBox tx_cbx;

    public TxtHolder(@NonNull View itemView) {
        super(itemView);
        iv_book_bg = itemView.findViewById(R.id.iv_book_bg);
        tv_content_center = itemView.findViewById(R.id.tv_content_center);
        tv_content_bottom = itemView.findViewById(R.id.tv_content_bottom);
        tx_cbx = itemView.findViewById(R.id.tx_cbx);
    }
}
