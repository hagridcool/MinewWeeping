package com.wyg.erickwang.minewweeping10;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class InputDialog extends Dialog implements View.OnClickListener{
    private Activity mContext;
    private EditText et_row;
    private EditText et_col;
    private Button btn_begin;
    private Button btn_cancel;

    private View.OnClickListener mClickListener;

    public InputDialog(){
        super(null);
    }

    public InputDialog(Activity context) {
        super(context);

        this.mContext = context;
    }

    public InputDialog(Activity context, int themeResId, View.OnClickListener mClickListener) {
        super(context, themeResId);

        this.mContext = context;
        this.mClickListener = mClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        et_row = findViewById(R.id.et_row);
        et_col = findViewById(R.id.et_col);
        btn_begin = findViewById(R.id.btn_begin);
        btn_cancel = findViewById(R.id.btn_cancel);

        Window dialogWindow = this.getWindow();
        WindowManager windowManager = mContext.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = (int)(display.getWidth()*0.8);
        dialogWindow.setAttributes(params);

        btn_cancel.setOnClickListener(this);
        btn_begin.setOnClickListener(this);
        this.setCancelable(true);

        showInputDialog();
    }

    public void showInputDialog() {
        InputDialog dialog = new InputDialog(mContext,R.style.Theme_AppCompat_Dialog,mClickListener);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_begin:
                String row = et_row.getText().toString().trim();
                String col = et_col.getText().toString().trim();
                Log.d("wyg1", "row="+row+",col="+col);
                break;
            case R.id.btn_cancel:
                this.cancel();
                break;
            default:
        }
    }
}









