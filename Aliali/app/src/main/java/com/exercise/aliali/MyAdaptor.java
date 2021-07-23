package com.exercise.aliali;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.exercise.aliali.VideoData;
import com.exercise.aliali.DataSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {
    private List<VideoData> mDataset=new ArrayList<>();
    private IOnItemClickListener mItemClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title,uploader,duration;
        private View contentView;
        private ImageView cover;

        public MyViewHolder(View v)
        {
            super(v);
            contentView = v;
            cover=v.findViewById(R.id.iv_cover);
            title=v.findViewById(R.id.tv_title);
            uploader=v.findViewById(R.id.tv_author);
            duration=v.findViewById(R.id.tv_duration);
        }
        public void onBind(VideoData data)
        {
            if(data==null) return;
            data.title=data.author+"上传的视频";
            title.setText(data.title);
            uploader.setText(data.author);
            long durationTime = (System.currentTimeMillis()-data.createTime.getTime())/1000;
            String durationText="";
            if(durationTime<60)
            {
                durationText=durationTime+"秒前";
            }
            else if(durationTime<3600)
            {
                durationText=durationTime/60+"分钟前";
            }
            else if(durationTime<3600*24)
            {
                durationText=durationTime/3600+"小时前";
            }
            else durationText=durationTime/3600/24+"天前";
            duration.setText(durationText);

            Glide.with(contentView).load(data.imageUrl)
                    .transition(withCrossFade())
                    .placeholder(R.drawable.cover_placeholder)
                    .error(R.drawable.cover_placeholder)
                    .fallback(R.drawable.cover_placeholder)
                    .into(cover);
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
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mainpage_item,parent,false));
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

        void onItemClick(int position, VideoData data);

        void onItemLongClick(int position, VideoData data);
    }

    public void addData(int position, VideoData data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        if (position != mDataset.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mDataset.size() - position);
        }
    }
    public void setData(List<VideoData> datas)
    {
        mDataset=datas;
        notifyDataSetChanged();
    }

    public MyAdaptor(List<VideoData> myDataset) {
        mDataset.addAll(myDataset);
        Log.i("REc",""+mDataset.size());
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
