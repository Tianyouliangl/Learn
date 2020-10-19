package com.learn.agg.txt.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;

public class CatalogHolder extends RecyclerView.ViewHolder {

    public ImageView iv_read;
    public TextView tv_name;

    public CatalogHolder(@NonNull View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.tv_read_name);
        iv_read = itemView.findViewById(R.id.iv_read);
    }
}
