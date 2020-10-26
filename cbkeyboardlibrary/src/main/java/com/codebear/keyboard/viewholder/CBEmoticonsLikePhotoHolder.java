package com.codebear.keyboard.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codebear.keyboard.R;
import com.codebear.keyboard.widget.PowerImageView;

public class CBEmoticonsLikePhotoHolder extends RecyclerView.ViewHolder {

    public ImageView iv_like;

    public CBEmoticonsLikePhotoHolder(@NonNull View itemView) {
        super(itemView);
        iv_like = itemView.findViewById(R.id.iv_like);
    }
}
