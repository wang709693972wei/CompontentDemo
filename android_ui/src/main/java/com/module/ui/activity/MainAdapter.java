package com.module.ui.activity;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.module.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 2018/9/27.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private Activity mContext;
    private final LayoutInflater mLayoutInflater;

    public ArrayList<String> mDatas = new ArrayList<>();

    public void addItem(String item) {
        mDatas.add(item);
    }

    public void addAll(List<String> data) {
        mDatas.addAll(data);
    }


    public interface OnItemClickLitener {
        void onItemClick(int index);
    }

    private MainAdapter.OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(MainAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public MainAdapter(Activity context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

    }


    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View inflate = mLayoutInflater.inflate(R.layout.main_item, parent, false);
        MainAdapter.MyViewHolder masterViewHolder = new MainAdapter.MyViewHolder(inflate);
        return masterViewHolder;
    }

    @Override
    public void onBindViewHolder(final MainAdapter.MyViewHolder holder, final int position) {
        final String oj = mDatas.get(position);
        upDateHolderView(holder, oj, position);
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(position);
                }
            });
        }
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w("TAG", "---" + event.getAction());
                return false;
            }
        });
    }

    private void upDateHolderView(MainAdapter.MyViewHolder holder, String oj, int position) {
        holder.item_tv.setText(oj);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        public TextView item_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            item_tv = itemView.findViewById(R.id.item_tv);
        }

    }

}



