package com.learn.agg.txt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;
import com.learn.agg.net.bean.BooksBean;
import com.learn.agg.txt.holder.TxtHolder;
import com.learn.agg.txt.holder.TxtHolderAdd;
import com.learn.commonalitylibrary.TxtBean;

import java.util.ArrayList;
import java.util.List;

public class TxtAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TxtBean> mList;
    private Context mContext;


    private final int type_add = 1;
    private final int type_item = 2;
    private BookListener mListener;
    private Boolean isDelete = false;

    public TxtAdapter(List<TxtBean> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case type_add:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_txt_add, parent, false);
                viewHolder = new TxtHolderAdd(view);
                break;
            case type_item:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_txt, parent, false);
                viewHolder = new TxtHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case type_item:
                TxtBean booksBean = mList.get(position);
                String cover_print = booksBean.getCover_print();
                if (cover_print.isEmpty()){
                    ((TxtHolder) holder).iv_book_bg.setVisibility(View.GONE);
                }
                ((TxtHolder) holder).tv_content_center.setText(booksBean.getTxt_name() + ".txt");
                ((TxtHolder) holder).tv_content_bottom.setText(booksBean.getTxt_name());
                ((TxtHolder) holder).tx_cbx.setVisibility(isDelete ? View.VISIBLE : View.GONE);
                ((TxtHolder) holder).tx_cbx.setChecked(booksBean.getChecked());
                if (!isDelete){
                    ((TxtHolder) holder).tx_cbx.setChecked(false);
                }
                ((TxtHolder) holder).tx_cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mList.get(position).setChecked(isChecked);
                        if (mListener != null) {
                            mListener.onCheckedChange();
                        }
                    }
                });
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getItemViewType() == type_item) {
                    if (mListener != null) {
                        if (isDelete) {
                            mList.get(position).setChecked(!mList.get(position).getChecked());
                            if (mListener != null) {
                                mListener.onCheckedChange();
                            }
                            notifyDataSetChanged();
                        } else {
                            mListener.onItemClick(mList.get(position));
                        }
                    }
                }
                if (holder.getItemViewType() == type_add) {
                    if (mListener != null) {
                        mListener.onAddBook();
                    }
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.getItemViewType() == type_item) {
                    if (mListener != null) {
                        mListener.onLongItemClick(mList.get(position));
                    }
                }
                return true;
            }
        });

    }

    public interface BookListener {
        void onItemClick(TxtBean booksBean);

        void onLongItemClick(TxtBean booksBean);

        void onAddBook();

        void onCheckedChange();
    }

    public void setOnBooksClickListener(BookListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isDelete) {
            if (position == (getItemCount() - 1)) {
                return type_add;
            } else {
                return type_item;
            }
        } else {
            return type_item;
        }
    }

    public void setIsDelete(Boolean delete) {
        this.isDelete = delete;
        notifyDataSetChanged();
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public List<TxtBean> getCheckedList(){
        List<TxtBean> list = new ArrayList<>();
        for ( int i = 0; i< mList.size();i++){
            if (mList.get(i).getChecked()){
                list.add(mList.get(i));
            }
        }
        return list;
    }

    @Override
    public int getItemCount() {
        if (!isDelete) {
            return mList.size() + 1;
        } else {
            return mList.size();
        }
    }


    public void setData(TxtBean bean) {
        mList.add(bean);
        notifyDataSetChanged();
    }

    public void setData(List<TxtBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

}
