package com.wyg.erickwang.minesweeping10;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * Created by Erick on 2017/7/14 0014.
 * Email: jsp_103@163.com
 * version: V1.0
 */

public class MineAdapter extends RecyclerView.Adapter<MineAdapter.MineViewHolder> {
    private List<String> dataList;
    private ItemClickListener mItemClickListener;

    public interface ItemClickListener{
        void onItemClick(View view,int position);
    }

    public MineAdapter(List<String> dataList){
        this.dataList = dataList;
    }

    public void setItemClickListener(ItemClickListener listener){
        if (listener != null){
            this.mItemClickListener = listener;
        }
    }

    @Override
    public MineAdapter.MineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        MineAdapter.MineViewHolder holder = new MineViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MineAdapter.MineViewHolder holder, int position) {
        holder.btn_mine.setText(dataList.get(position));
        holder.btn_mine.setTag(position);
        holder.btn_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将按钮点击事件传递到外部，供调用者使用
                mItemClickListener.onItemClick(view,(int)view.getTag());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MineViewHolder extends RecyclerView.ViewHolder{
        Button btn_mine;

        public MineViewHolder(View itemView) {
            super(itemView);

            btn_mine = itemView.findViewById(R.id.btn_mine);
        }
    }
}
