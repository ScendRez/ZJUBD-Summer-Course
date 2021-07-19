package com.exercise.homework2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {
    private List<String> mDataset=new ArrayList<>();
    private IOnItemClickListener mItemClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDat;
        private View contentView;

        public MyViewHolder(View v)
        {
            super(v);
            contentView = v;
            tvDat=v.findViewById(R.id.tv_data);
        }
        public void onBind(String dat)
        {
            tvDat.setText(dat);
        }
        public void setOnClickListener(View.OnClickListener listener){
            if(listener!=null)
            {
                contentView.setOnClickListener(listener);
            }
        }
        public void setOnLongClickListener(View.OnLongClickListener listener){
            if(listener!=null)
            {
                contentView.setOnLongClickListener(listener);
            }
        }
    }

    @Override
    public MyAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        holder.onBind(mDataset.get(position));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position, mDataset.get(position));
                }
            }
        });
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemLongClick(position, mDataset.get(position));
                }
                return false;
            }
        });

    }
    public int getItemCount() {return mDataset.size();}
    public interface IOnItemClickListener {

        void onItemClick(int position, String data);

        void onItemLongClick(int position, String data);
    }

    public void addData(int position, String data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        if (position != mDataset.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mDataset.size() - position);
        }
    }

    public MyAdaptor(List<String> myDataset) {
        mDataset.addAll(myDataset);
    }

    public void removeData(int position) {
        if (null != mDataset && mDataset.size() > position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
            if (position != mDataset.size()) {
                //刷新改变位置item下方的所有Item的位置,避免索引错乱
                notifyItemRangeChanged(position, mDataset.size() - position);
            }
        }
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }
}
