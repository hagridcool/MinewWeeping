package com.wyg.erickwang.minesweeping10;

import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_open;
    private MineAdapter adapter;
    private List<String> dataList;
    private int [][]map;
    private int [][]countPosition; //用于记录每个点周围总的地雷数
    private GameMap game;
    private RecyclerView.LayoutManager layoutManager;
    private int cntMine = 0; //雷区地雷个数
    private int row = 0;
    private int col = 0;

    private static final String TAG = "wyg1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_open = (Button) findViewById(R.id.btn_open_dialog);
        btn_open.setOnClickListener(this);

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
                if (game.sweeping(position)){
                    view.setBackgroundColor(Color.RED);

                    Log.d(TAG, "雷:" + position);
                } else {
                    //view.setVisibility(View.INVISIBLE);

                    List<Integer> posList;
                    posList = game.getCircleBlank(position);

                    for (int i=0; i<posList.size(); i++){
                        View v = layoutManager.getChildAt(posList.get(i));
                        v.setVisibility(View.INVISIBLE);
                        int num_mine = game.getNumOfMine(position);
                        adapter.motifyPos(position,num_mine);
                        v.setBackgroundColor(Color.WHITE);
                    }
                    posList.clear();

                    Log.d(TAG, "无雷" + position);
                }
            }
        });


    }

    private void sweeping() {
        game = new GameMap(this,row,col,cntMine);
        map = game.getMap();
        countPosition = game.getCountPosition();

        //adapter.motifyDataset(countPosition);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_open_dialog:
                initGame(2);
                break;
            default:
                Log.d("wyg1", "onClick: 13245645546");
        }
    }
}
