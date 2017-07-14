package com.wyg.erickwang.minesweeping10;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Erick on 2017/7/14 0014.
 * Email: jsp_103@163.com
 * version: V1.0
 *  用于定义游戏所用的地图，以及与地图相关的操作
 */

public class GameMap {
    private Context mContext;
    private int row; //雷区的行
    private int col; //雷区的列
    private int [][]map;
    private int [][]countPosition; //用于记录每个点周围总的地雷数
    static int [][]visited;
    private int countMine = 0; //地雷总数目

    static int dir[][]=new int[][]{{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
    public static final int MAP_MINE = 0; //表示有地雷
    public static final int MAP_BLANK = 1; //表示没有地雷，只是空白
    public static final int MAP_FLAG = 3; //表示插上小红旗

    public GameMap(){}

    //只生成空白地图不设置地雷
    public GameMap(Context context,int row,int col){
        this.row = row;
        this.col = col;
        mContext = context;

        createBlankMap();
    }

    /**
     * 设置带有地雷的地图
     * @param row 行数
     * @param col 列数
     * @param count <=0：表示在全局范围内随机设置地雷；
     *              >0:表示设置的地雷数等于count;
     */
    public GameMap(Context context,int row,int col,int count){
        this.row = row;
        this.col = col;
        mContext = context;

        if (count <= 0) {
            settingMine();
        } else {
            createBlankMap(); //创建空白地图，随机生成行和列，按照指定数量设置地雷
            setttingMine(count);
        }

    }

    private void createBlankMap(){
        map = new int[row][];
        for (int i=0; i<row; i++){
            map[i] = new int[col];
            for (int j=0; j<col; j++){
                map[i][j] = MAP_BLANK;
            }
        }
    }

    //对整个地图范围内的点布雷
    public void settingMine(){
        countMine = 0;

        for (int i=0; i<row; i++){
            for (int j=0; j<col; j++){
                Random random = new Random();
                int tmp = random.nextInt(2);

                if (tmp == 0) countMine ++; //地雷数增加

                map[i][j] = tmp;
            }
        }
    }

    //对指定位置布雷
    private void settingMine(int row,int col){
        if (row >= this.row || row < 0 || col >= this.col || col < 0) {
            Toast.makeText(mContext,"位置输入错误",Toast.LENGTH_SHORT).show();
            return;
        }
        map[row][col] = 0;
    }

    //根据指定数量设置地雷
    private void setttingMine(int count){
        if (count <= 0){
            Toast.makeText(mContext,"数量输入错误",Toast.LENGTH_SHORT).show();
            return;
        } else {
            Random random = new Random();
            while(count > 0) {
                int r = random.nextInt(row);
                int c = random.nextInt(col);

                if (map[r][c] != MAP_MINE){
                    map[r][c] = MAP_MINE;
                    count --;
                }
            }
        }
    }

    /**
     * 执行扫雷操作，根据输入的方块序号确定雷块
     * @param index
     */
    public boolean sweeping(int index){
        //将序号转为行和列
        int r = index / col;
        int c = index % col;

        trvalMap();
        if (map[r][c] == MAP_MINE){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据输入的坐标进行扫雷操作
     * @param row
     * @param col
     * @return
     */
    public boolean sweeping(int row,int col){
        trvalMap();
        if (map[row][col] == MAP_MINE){
            return true;
        } else {
            return false;
        }
    }


    private void trvalMap(){
        this.countPosition = new int[row][];
        this.visited = new int[row][];

        for (int i=0; i<row; i++){
            countPosition[i] = new int[col];
            visited[i] = new int[col];
        }
        dfs(0,0); //遍历整个雷区，得出每个点所对应的地雷个数

    }

    public void dfs(int r,int c){
        if(r<0 || c<0 || r>=row || c>=col){
            return;
        }

        if (visited[r][c] == 1) {
            return;
        }

        if (map[r][c] == MAP_MINE) {
            countPosition[r][c] ++;
        }

        for (int i = 0; i < dir.length; i++) {
            int newRow = r + dir[i][0];
            int newCol = c + dir[i][1];

            if (newRow>=0 && newRow<row && newCol>=0
                    && newCol<col && map[newRow][newCol] == MAP_MINE) {
                countPosition[r][c] ++;
            }
        }

        visited[r][c] = 1;

        for (int i = 0; i < dir.length; i++) {
            int newRow = r + dir[i][0];
            int newCol = c + dir[i][1];

            dfs(newRow, newCol);
        }

        return;
    }

    public int[][] getMap() {
        return map;
    }

    public int[][] getCountPosition() {
        return countPosition;
    }
}
