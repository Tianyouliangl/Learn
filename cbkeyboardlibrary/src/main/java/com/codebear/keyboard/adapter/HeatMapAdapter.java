package com.codebear.keyboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codebear.keyboard.R;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.body.GifBean;

import java.util.List;

public class HeatMapAdapter extends RecyclerView.Adapter<HeatMapAdapter.HeatMapHolder> {


    private Context mContext;
    private List<GifBean> list;
    private HearMapClick mOnClick;

    public interface HearMapClick{
        void onHeatMapItemClick(GifBean gifBean);
    }

    public void setOnHeatMapClickListener(HearMapClick click){
        this.mOnClick = click;
    }

    public HeatMapAdapter(Context mContext, List<GifBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setData(List<GifBean> gifBeanList) {
        this.list = gifBeanList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HeatMapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_emoticon_like_photo, parent, false);
        return new HeatMapHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeatMapHolder holder, int position) {
        final GifBean gifBean = list.get(position);
        int type = gifBean.getType();
        String url = gifBean.getUrl();
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.icon_bq_loading)//图片加载出来前，显示的图片
                .fallback(R.drawable.icon_bq_error) //url为空的时候,显示的图片
                .error(R.drawable.icon_bq_error);//图片加载失败后，显示的图片
        if (type == ChatMessage.MSG_GIF_GIF) {
            Glide.with(mContext).asGif().load(url).apply(options).into(holder.iv_like);
        }else {
            Glide.with(mContext).asBitmap().load(url).apply(options).into(holder.iv_like);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClick){
                    mOnClick.onHeatMapItemClick(gifBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HeatMapHolder extends RecyclerView.ViewHolder {

        public ImageView iv_like;

        public HeatMapHolder(@NonNull View itemView) {
            super(itemView);
            iv_like = itemView.findViewById(R.id.iv_like);
        }
    }
}
