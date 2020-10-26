package com.codebear.keyboard.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codebear.keyboard.R;
import com.codebear.keyboard.viewholder.CBEmoticonsLikePhotoHolder;
import com.learn.commonalitylibrary.body.GifBean;

import java.util.List;

public class CBEmoticonsLikePhotoAdapter extends RecyclerView.Adapter<CBEmoticonsLikePhotoHolder> {

    private List<GifBean> mList;
    private final Context mContext;
    private onItemClick mItemClick;
    private onItemLongClick mItemLongClick;
    private onItemTouch mItemTouck;

    public interface onItemClick {
        void onItemClickListener(GifBean bean);
    }

    public interface onItemLongClick{
        Boolean onItemLongClickListener(GifBean bean);
    }

    public interface onItemTouch{
        Boolean onItemTouchListener(MotionEvent motionEvent);
    }

    public void setOnItemClickListener(onItemClick click){
        this.mItemClick = click;
    }

    public void setOnItemClickLongListener(onItemLongClick click){
        this.mItemLongClick = click;
    }

    public void setOnItemTouchListener(onItemTouch touch){
        this.mItemTouck = touch;
    }

    public CBEmoticonsLikePhotoAdapter(Context mContext, List<GifBean> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    @NonNull
    @Override
    public CBEmoticonsLikePhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_emoticon_like_photo,
                parent, false);
        return new CBEmoticonsLikePhotoHolder(view);
    }


    public void setList(List<GifBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void setItem(GifBean bean){
        if (bean == null)return;
        this.mList.add(0,bean);
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final CBEmoticonsLikePhotoHolder holder, final int position) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.icon_bq_loading)//图片加载出来前，显示的图片
                .fallback(R.drawable.icon_bq_error) //url为空的时候,显示的图片
                .error(R.drawable.icon_bq_error);//图片加载失败后，显示的图片
        Glide.with(mContext).asBitmap().apply(options).load(mList.get(position).getUrl()).into
                (new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        resource = eraseColor(resource, -1);
                        resource = eraseColor(resource, -16777216);
                        holder.iv_like.setImageBitmap(resource);
                    }
                });
//        Glide.with(mContext).asGif().apply(options).load(mList.get(position).getLikePhotoUrl()).into(holder.iv_like);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mItemClick){
                    mItemClick.onItemClickListener(mList.get(position));
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mItemLongClick){
                    mItemLongClick.onItemLongClickListener(mList.get(position));
                }
                return false;
            }
        });
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != mItemTouck){
                    return mItemTouck.onItemTouchListener(event);
                }
                return false;
            }
        });

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
}
