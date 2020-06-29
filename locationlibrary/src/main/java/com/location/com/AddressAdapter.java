package com.location.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>{

    List<PoiltemBean> mList = new ArrayList<>();
    Context mContext;
    private locationInterface mOnItemClick;

    public interface locationInterface{
        void  onItemClick(PoiltemBean bean,int position);
    }

    public AddressAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<PoiltemBean> list){
        this.mList = list;
        notifyDataSetChanged();
    }

    public void checked(int position){
        for (int i=0;i<mList.size();i++){
            mList.get(i).setChecked(false);
        }
        mList.get(position).setChecked(true);
        notifyDataSetChanged();
    }

    public PoiltemBean getChecked(){
        for (int i=0;i<mList.size();i++){
            PoiltemBean bean = mList.get(i);
            if (bean.getChecked()){
                return bean;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, final int position) {
        holder.tv_title.setText(mList.get(position).getTitle());
        holder.tv_address.setText(mList.get(position).getDistance() + "米 | " + mList.get(position).getAddress());
        holder.iv_checked.setVisibility(mList.get(position).getChecked() ? View.VISIBLE:View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null){
                    PoiltemBean bean = mList.get(position);
                    mOnItemClick.onItemClick(bean,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_title;
        public TextView tv_address;
        public ImageView iv_checked;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_address = itemView.findViewById(R.id.tv_address);
            iv_checked = itemView.findViewById(R.id.iv_checked);
        }
    }

    public void setOnClickItem(locationInterface locationInterface){
        this.mOnItemClick = locationInterface;
    }
}
