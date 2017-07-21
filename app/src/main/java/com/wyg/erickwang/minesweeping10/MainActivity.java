package com.wyg.erickwang.minesweeping10;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
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
    private static final int TOTAL_TIME = 500;
    private static final String TAG = "wyg1";
    private static final int FIRST_START = 0; //第一次启动游戏
    private static final int MENU_START = 1234; //从菜单启动游戏

    private TextView tv_cntMine;
    private TextView tv_showTime;
    private TextView tv_record;
    private ImageView img_flag;

    private MineAdapter adapter;
    private List<String> dataList;
    private RecyclerView.LayoutManager layoutManager;
    private GameMap game;
    private int [][]countPosition; //用于记录每个点周围总的地雷数

    private int cntMine = 0; //雷区地雷个数
    private int row = 0;
    private int col = 0;
    private int runningTime = 0;
    private List<Integer> recordList;
    private int []flags;
    private SharedPreferences recordSharedPre;


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

        initGame(FIRST_START);

        recordSharedPre = getSharedPreferences("record_list", Context.MODE_PRIVATE);
        int totalRecordNum = recordSharedPre.getInt("totalRecordNum",0);

        if (totalRecordNum != 0){
           for (int i=0; i<totalRecordNum; i++){
               recordList.add(recordSharedPre.getInt(i+"",0));
           }
            String recordStr = "游戏排名：\n";
            for (int k=0; k<(recordList.size()>=10?10:recordList.size()); k++){
                recordStr +="第" + (k+1) + "名：" + recordList.get(k) + " s\n";
            }
            tv_record.setText(recordStr);

        }
    }

    /**
     * 从输入对话框中获取雷区的行、列以及地雷个数，对游戏初始化
     * @param flag 0：表示第一次启动游戏； 非0：表示从菜单启动游戏
     */
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
                showGame();

                img_flag.setImageResource(R.drawable.img_ingame);
                runningTime = 0;
                handler.removeCallbacks(runnable);

                tv_cntMine.setText(game.getCntMine() + "");
                tv_showTime.setText(runningTime + "");
                handler.postDelayed(runnable,1000);

                flags = new int[row * col];

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

    private void showGame() {
        //生成游戏地图等相关操作
        game = new GameMap(this,row,col,cntMine);
        game.trvalMap();
        this.countPosition = game.getCountPosition();

        //设置RecyclerView的相关操作
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_mine);
        adapter = new MineAdapter(this,game,dataList,row,col,cntMine);
        layoutManager = new GridLayoutManager(this,col);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.motifyDataset(this.countPosition); //更新适配器的数据

        //设置Adapter的监听器，执行点击操作
        adapter.setItemClickListener(new MineAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (game.isMine(position)){
                    view.setBackgroundColor(Color.RED);
                    img_flag.setImageResource(R.drawable.img_failed);

                    handler.removeCallbacks(runnable);
                    showFailedInfo();
                    Log.d(TAG, "雷:" + position);
                } else {
                    List<Integer> posList = new ArrayList();
                    posList.clear();
                    posList = game.getCircleBlank(position);



                    for (int i=0; i<posList.size(); i++) {
                        int pos = posList.get(i);
                        int num_mine = game.getNumOfMine(pos);

                        if (layoutManager.findViewByPosition(pos) != null){
                            adapter.motifyPos(num_mine, pos);
                        }
                    }
                    if(game.allClear()){
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
            public boolean onItemLongClick(View view, final int postion) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.pupo_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.insert_flag:
                                if (flags[postion] == 0) {
                                    flags[postion] = 1;
                                    adapter.setMineFlag(postion, 1);
                                    game.setFlag(postion);
                                } else {
                                    Toast.makeText(MainActivity.this,"已经标记红旗",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.cancel_flag:
                                if (flags[postion] == 1){
                                    flags[postion] = 0;
                                    adapter.setMineFlag(postion, 0);
                                    game.cancelFlag(postion);
                                }  else {
                                    Toast.makeText(MainActivity.this,"无红旗标记",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                        }

                        if (game.setFlags()){
                            showSuccessInfo();
                        }
                        return true;
                    }
                });
                popupMenu.show();

                return true;
            }
       });
    }

    private void showSuccessInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder = builder.setCancelable(false);
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
                initGame(FIRST_START);
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

    private void initGameData(int row,int col) {
        dataList = new ArrayList<>();

        for (int i=0; i<row * col; i++){
            dataList.add("");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = recordSharedPre.edit();

        for (int i=0; i<recordList.size(); i++){
            editor.putInt(i+"",recordList.get(i));
        }
        editor.putInt("totalRecordNum",recordList.size());
        editor.commit();
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
                initGame(MENU_START);
                break;
            case R.id.menu_close:
            MainActivity.this.finish();
            break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 设计实现计时
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
