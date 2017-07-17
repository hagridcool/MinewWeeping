package com.wyg.erickwang.minesweeping10;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erick on 2017/7/14 0014.
 * Email: jsp_103@163.com
 * version: V1.0
 */

public class MineAdapter extends RecyclerView.Adapter<MineAdapter.MineViewHolder> {
    private List<String> dataList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;
    private MineAdapter.MineViewHolder holder;
    private List<MineViewHolder> holderList;

    private String TAG = "wyg1";

    public void motifyPos(int position, int num_mine) {
         holderList.get(position).tv_mine.setText(num_mine + "");
         holderList.get(position).tv_mine.setBackgroundColor(Color.WHITE);

        Log.d(TAG, "motifyPos: " + position);
    }

    /**
     * 将雷区数据添加到RecyclerView中
     * @param newDataset
     */
    public void motifyDataset(int [][] newDataset) {
        dataList.clear();

        for (int i=0; i<newDataset.length; i++){
            for (int j=0; j<newDataset[i].length; j++){
                dataList.add(newDataset[i][j] + "");
            }
        }

       notifyDataSetChanged();
    }

    public MineAdapter(List<String> dataList){
        this.dataList = dataList;

        holderList = new ArrayList<>();
    }

    public void setMineFlag(int position, String s) {
        holderList.get(position).tv_mine.setText(s);
        holderList.get(position).tv_mine.setTextColor(Color.RED);
    }


    public interface ItemClickListener{
        void onItemClick(View view,int position);
    }

    public interface ItemLongClickListener{
        void onItemLongClick(View view,int postion);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    @Override
    public MineAdapter.MineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        holder = new MineViewHolder(view);


        holderList.add(holder);

        //Log.d("wyg1", "onCreateViewHolder: holderList.size==" + holderList.size());

        return holder;
    }

    @Override
    public void onBindViewHolder(MineAdapter.MineViewHolder holder, final int position) {
//        if (dataList.get(position).equals("-1")){
//            holder.tv_mine.setText("");
//        } else {
//            holder.tv_mine.setText(dataList.get(position));
//        }

        holder.tv_mine.setText("");
        //设置控件背景颜色
        holder.tv_mine.setBackgroundColor(Color.BLUE);
        //holder.tv_mine.setBackgroundColor(Color.BLACK);
        holder.tv_mine.setTag(position);
        holder.tv_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将按钮点击事件传递到外部，供调用者使用
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(view,(int)view.getTag());
            }
        });

        holder.tv_mine.setLongClickable(true);
        holder.tv_mine.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemLongClickListener != null)
                    mItemLongClickListener.onItemLongClick(view,(int)view.getTag());

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MineViewHolder extends RecyclerView.ViewHolder{
        TextView tv_mine;

        public MineViewHolder(View itemView) {
            super(itemView);

            tv_mine = itemView.findViewById(R.id.tv_mine);
        }
    }
}
