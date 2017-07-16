package com.wyg.erickwang.minesweeping10;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private TextView tv_cntMine;
    private TextView tv_showTime;
    private TextView tv_record;
    private ImageView img_flag;

    private MineAdapter adapter;
    private List<String> dataList;
    private int [][]map;
    private int [][]countPosition; //用于记录每个点周围总的地雷数
    private GameMap game;
    private RecyclerView.LayoutManager layoutManager;
    private int cntMine = 0; //雷区地雷个数
    private int row = 0;
    private int col = 0;
    private int runningTime = 0;
    private List<Integer> recordList;

    private static final int TOTAL_TIME = 100;
    private static final String TAG = "wyg1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_cntMine = (TextView) findViewById(R.id.tv_cntMine);
        tv_showTime = (TextView) findViewById(R.id.tv_showTime);
        tv_record = (TextView) findViewById(R.id.tv_record);
        img_flag = (ImageView) findViewById(R.id.img_flag);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toobar);
        setSupportActionBar(toolbar);

        recordList = new ArrayList<Integer>();

        initGame(0);

    }

    private void showGame(int col) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_mine);
        adapter = new MineAdapter(dataList);
        layoutManager = new GridLayoutManager(this,col);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new MineAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(MainActivity.this,dataList.get(position),Toast.LENGTH_SHORT).show();
                if (game.isMine(position)){
                    view.setBackgroundColor(Color.RED);
                    img_flag.setImageResource(R.drawable.img_failed);

                    handler.removeCallbacks(runnable);
                    showFailedInfo();
                    Log.d(TAG, "雷:" + position);
                } else {
                    view.setVisibility(View.INVISIBLE);

                    List<Integer> posList;
                    posList = game.getCircleBlank(position);

                    for (int i=0; i<posList.size(); i++){
                        View v = layoutManager.getChildAt(posList.get(i));
                        //v.setVisibility(View.INVISIBLE);
                        int num_mine = game.getNumOfMine(posList.get(i));
                        adapter.motifyPos(posList.get(i),num_mine);
                        //v.setVisibility(View.INVISIBLE);
                    }
                    posList.clear();

                    if (game.allClear()) {
                        recordList.add(runningTime);
                        handler.removeCallbacks(runnable);

                        showSuccessInfo();
                    }

                    Log.d(TAG, "无雷" + position);
                }
            }
        });

        adapter.setItemLongClickListener(new MineAdapter.ItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int postion) {
                Snackbar.make(view,"设置地雷标记",Snackbar.LENGTH_LONG)
                        .setAction("插旗", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adapter.setMineFlag(postion,"◀▶");
                            }
                        }).show();
            }
        });


    }

    private void sweeping() {
        game = new GameMap(this,row,col,cntMine);
        map = game.getMap();
        game.trvalMap();
        countPosition = game.getCountPosition();

        if (countPosition == null){
            Log.d(TAG, "countPosition == null");
        } else {
            adapter.motifyDataset(countPosition);
        }
    }

    private void showSuccessInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder = builder.setCancelable(true);
        builder = builder.setTitle("提示");
        builder = builder.setMessage("恭喜你找到了所有雷，游戏胜利");
        builder = builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handler.removeCallbacks(runnable);

                Collections.sort(recordList, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer a, Integer b) {
                        int i = a.intValue();
                        int j = b.intValue();

                        if (i >= j) return 1;
                        else return -1;
                    }
                });

                String recordStr = "游戏排名：\n";
                for (int k=0; k<(recordList.size()>=10?10:recordList.size()); k++){
                    recordStr +="第" + (k+1) + "名：" + recordList.get(k) + " s\n";
                }
                tv_record.setText(recordStr);
            }
        });

        adapter.setItemClickListener(null);
        adapter.setItemLongClickListener(null);
        builder.create().show();
    }

    private void showFailedInfo(){
        handler.removeCallbacks(runnable);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder = builder.setCancelable(true);
        builder = builder.setTitle("提示");
        builder = builder.setMessage("游戏失败");
        builder = builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initGame(0);
            }
        });

        builder = builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        adapter.setItemClickListener(null);
        adapter.setItemLongClickListener(null);
        builder.create().show();
    }

    private void initGame(final int flag){
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final EditText et_row = dialogView.findViewById(R.id.et_row);
        final EditText et_col = dialogView.findViewById(R.id.et_col);
        final EditText et_cnt = dialogView.findViewById(R.id.et_cnt);
        Button btn_begin = dialogView.findViewById(R.id.btn_begin);
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

        builder = builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row = Integer.parseInt(et_row.getText().toString().trim());
                col = Integer.parseInt(et_col.getText().toString().trim());
                cntMine = Integer.parseInt(et_cnt.getText().toString().trim());

                if (row <= 0 || col <= 0){
                    Toast.makeText(MainActivity.this,"请输入大于0的整数",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cntMine > row * col){
                    Toast.makeText(MainActivity.this,"请输入地雷个数",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("wyg1",et_row.getText().toString().trim() + " x " + et_col.getText().toString().trim());

                dialog.cancel();

                initGameData(row,col);
                showGame(col);
                sweeping();

                img_flag.setImageResource(R.drawable.img_ingame);


                runningTime = 0;
                handler.removeCallbacks(runnable);

                tv_cntMine.setText(game.getCntMine() + "");
                tv_showTime.setText(runningTime + "");
                handler.postDelayed(runnable,1000);


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                if (flag == 0) //第一次打开游戏
                    MainActivity.this.finish();
            }
        });


    }

    private void initGameData(int row,int col) {
        dataList = new ArrayList<>();

        for (int i=0; i<row * col; i++){
            dataList.add("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_setting:
                initGame(2);
                break;
            case R.id.menu_close:
                MainActivity.this.finish();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 设计实现倒计时
     */
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            runningTime ++;
            tv_showTime.setText(runningTime + "");
            handler.postDelayed(this,1000);

            if (runningTime == TOTAL_TIME){
                showFailedInfo();
            }
        }
    };
}
