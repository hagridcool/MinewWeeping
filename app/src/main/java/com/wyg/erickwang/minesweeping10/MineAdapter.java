package com.wyg.erickwang.minesweeping10;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Erick on 2017/7/14 0014.
 * Email: jsp_103@163.com
 * version: V1.0
 */

public class MineAdapter extends RecyclerView.Adapter<MineAdapter.MineViewHolder> {
    private Context mContext;
    private List<String> dataList;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;
    private MineAdapter.MineViewHolder holder;
    private List<Integer> changedList;
    private GameMap game;
    private int flagState;
    private int insertFlag = 0;
    static List<MineViewHolder> holderList;

    private static final int FIRST_LOAD = 0;
    private static final int CLICK_SWEEPING = 1;
    private static final int CLICK_LONG = 2;



    private String TAG = "wyg1";


    public MineAdapter(){}

    public MineAdapter(Context mContext,GameMap game,List<String> dataList, int row, int col, int cntMine){
        this.mContext = mContext;
        this.game = game;
        this.dataList = dataList;

        flagState = FIRST_LOAD;
        insertFlag = 0;
    }

    public void motifyPos(int cntMine,int postion) {
        flagState = CLICK_SWEEPING;

        notifyItemChanged(postion);
    }

    public void setMineFlag(int postion, int insertFlag) {
        this.insertFlag = insertFlag;

        this.flagState = CLICK_LONG;

        notifyItemChanged(postion);
    }

    public interface ItemClickListener{
        void onItemClick(View view,int position);
    }

    public interface ItemLongClickListener{
        boolean onItemLongClick(View view,int postion);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    /**
     * 将雷区数据添加到RecyclerView中
     */
    public void motifyDataset(int [][] countPosition) {
        dataList.clear();

        for (int i=0; i<countPosition.length; i++){
            for (int j=0; j<countPosition[i].length; j++){
                dataList.add(countPosition[i][j] + "");
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public MineAdapter.MineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        holder = new MineViewHolder(view);

        return holder;
    }

    /**
     * 执行扫雷操作
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MineAdapter.MineViewHolder holder, final int position) {
        if (flagState == FIRST_LOAD) {
            if (dataList.get(position).equals("-1")) {
                holder.tv_mine.setText("");
            } else {
                holder.tv_mine.setText(dataList.get(position));
            }
            holder.tv_mine.setText("");
            holder.tv_mine.setBackgroundColor(Color.GRAY);
            //holder.tv_mine.setBackgroundColor(Color.BLACK);
            holder.tv_mine.setTag(position);
        } else if (flagState == CLICK_SWEEPING){
            if (!game.isMine(position)) {
                holder.tv_mine.setText(dataList.get(position) + "");
                holder.tv_mine.setBackgroundColor(Color.WHITE);
                holder.tv_mine.setEnabled(false);
            }
        } else if (flagState == CLICK_LONG){
            if (insertFlag == 1 && !holder.tv_mine.getText().toString().equals("◀▶")){
                holder.tv_mine.setText("◀▶");
                holder.tv_mine.setTextColor(Color.RED);
                holder.tv_mine.setBackgroundColor(Color.GRAY);
            } else if (insertFlag == 0 && holder.tv_mine.getText().toString().equals("◀▶") ){
                if (dataList.get(position).equals("-1")) {
                    holder.tv_mine.setText("");
                } else {
                    holder.tv_mine.setText(dataList.get(position));
                    holder.tv_mine.setTextColor(Color.BLACK);
                }
            }
        }

        holder.tv_mine.setTag(position);
        holder.tv_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将按钮点击事件传递到外部，供调用者使用
                if (mItemClickListener != null) {
                    flagState = CLICK_SWEEPING; //标记点击事件
                    mItemClickListener.onItemClick(view, (int) view.getTag());
                }
            }
        });

        //处理长按操作
        holder.tv_mine.setLongClickable(true);
        holder.tv_mine.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemLongClickListener != null) {
                    flagState = CLICK_LONG;
                    mItemLongClickListener.onItemLongClick(view, (int) view.getTag());
                }
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
        private int tag;


        public MineViewHolder(View itemView) {
            super(itemView);
            tv_mine = itemView.findViewById(R.id.tv_mine);
        }
    }
}
