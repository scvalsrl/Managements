package com.example.mingi.management;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditDialog extends Dialog implements View.OnClickListener {
    private MyDialogListener dialogListener;
    private static final int LAYOUT = R.layout.edit_text_dialog_layout;

    private Context context;
    private TextView okBtn;
    private EditText kmEt;


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
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.okbtn:
                Toast.makeText(context, "ok 누름", Toast.LENGTH_SHORT).show();
                String km = kmEt.getText().toString();
                dialogListener.onPositiveClicked(km);
                dismiss();
                break;
        }

    }
}
