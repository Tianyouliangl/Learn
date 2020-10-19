package com.learn.agg.txt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bifan.txtreaderlib.interfaces.IChapter;
import com.learn.agg.R;
import com.learn.agg.txt.holder.CatalogHolder;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogHolder> {

    private List<IChapter> list;
    private Context context;
    private int path = -1;
    private int index = -1;
    private onClickInterface mOnClickItem;

    public CatalogAdapter(Context c, List<IChapter> l) {
        this.context = c;
        this.list = l;
    }

    public interface onClickInterface{
        void onClickItem(IChapter iChapter);
    }

    public void setOnClickListener(onClickInterface clickInterface){
        this.mOnClickItem = clickInterface;
    }

    @NonNull
    @Override
    public CatalogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CatalogHolder(LayoutInflater.from(context).inflate(R.layout.item_read_txt, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogHolder holder, int position) {
        final IChapter chapter = list.get(position);
        holder.tv_name.setText(chapter.getTitle());
        if (path != -1) {
            if (path == chapter.getIndex()){
                index = position;
                holder.iv_read.setVisibility(View.VISIBLE);
            }else {
                holder.iv_read.setVisibility(View.GONE);
            }

        } else {
            holder.iv_read.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickItem != null){
                    mOnClickItem.onClickItem(chapter);
                }
            }
        });

    }

    public void setList(List<IChapter> l) {
        list.addAll(l);
        notifyDataSetChanged();
    }

    public void setPath(int  p) {
        path = p;
        notifyDataSetChanged();
    }

    public int getIndex(){
        return index;
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }
}
