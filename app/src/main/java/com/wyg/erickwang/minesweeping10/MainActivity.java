package com.wyg.erickwang.minesweeping10;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_open;
    private MineAdapter adapter;
    private List<String> dataList;


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
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this,col));
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new MineAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this,dataList.get(position),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initGame(final int flag){
        final int[] row = new int[]{0};
        final int[] col = new int[]{0};

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final EditText et_row = dialogView.findViewById(R.id.et_row);
        final EditText et_col = dialogView.findViewById(R.id.et_col);
        Button btn_begin = dialogView.findViewById(R.id.btn_begin);
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

        builder = builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row[0] = Integer.parseInt(et_row.getText().toString().trim());
                col[0] = Integer.parseInt(et_col.getText().toString().trim());

                if (row[0] <= 0 || col[0] <= 0){
                    Toast.makeText(MainActivity.this,"请输入大于0的整数",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("wyg1",et_row.getText().toString().trim() + "X" + et_col.getText().toString().trim());

                dialog.cancel();

                initGameData(row[0],col[0]);
                showGame(col[0]);

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

        for (int i=1; i<=row * col; i++){
            dataList.add(i + "");
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
