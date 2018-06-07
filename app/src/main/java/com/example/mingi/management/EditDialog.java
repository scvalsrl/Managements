package com.example.mingi.management;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class EditDialog extends Dialog implements View.OnClickListener {
    private MyDialogListener dialogListener;
    private static final int LAYOUT = R.layout.edit_text_dialog_layout;

    private Context context;
    private TextView okBtn;
    private EditText kmEt;
    private String kmPattern;


    public EditDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void setDialogListener(MyDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initView();
    }

    void initView() {
        okBtn = (TextView) findViewById(R.id.okbtn);
        kmEt = (EditText) findViewById(R.id.kmedit);

        okBtn.setOnClickListener(this);

        kmPattern = "^[0-9]*$|^[0-9]*\\.[0-9]*$";
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.okbtn:
                if(TextUtils.isEmpty(kmEt.getText())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(" 총 거리를 입력해주세요 ")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();
                }

                String km = kmEt.getText().toString();
                dialogListener.onPositiveClicked(km);
                boolean isKm = Pattern.matches(kmPattern, km);
                if(isKm == true) {
                    dismiss();
                    break;
                }
                else {
                    Toast.makeText(context, "숫자.숫자 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
