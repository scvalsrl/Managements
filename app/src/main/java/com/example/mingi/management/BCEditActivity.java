package com.example.mingi.management;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BCEditActivity extends AppCompatActivity {


    ImageView imgView;
    EditText bcname, bclevel, bccom, bcphone, bcemail;
    TextView bcadd;
    Button bccamera, bcupload;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str;
    String isGPSEnable, nowLat, nowLon, nowName;

    //for camera
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA}; // permission set

    private static final int MULTIPLE_PERMISSIONS = 101; // permission agree -> use callback func
    private String mCurrentPhotoPath;


    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;

    final String uploadFilePath = "storage/emulated/0/test/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
    String uploadFileName = ""; //전송하고자하는 파일 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcedit);
        initView();
        getDataFromDetail();
        setInitView();
        setClickBtn();
    }

    void initView() {
        bcname = (EditText) findViewById(R.id.bcname);
        bclevel = (EditText) findViewById(R.id.bclevel);
        bccom = (EditText) findViewById(R.id.bccom);
        bcphone = (EditText) findViewById(R.id.bcphone);
        bcemail = (EditText) findViewById(R.id.bcemail);
        bcadd = (TextView) findViewById(R.id.bcadd);

        bcupload = (Button) findViewById(R.id.uploadBtn);
        bccamera = (Button) findViewById(R.id.cameraBtn);

        imgView = (ImageView) findViewById(R.id.imgView);
    }

    void getDataFromDetail() {
        Intent fromDetail = getIntent();
        bcname_str = fromDetail.getStringExtra("bcname");
        bclevel_str = fromDetail.getStringExtra("bclevel");
        bccom_str = fromDetail.getStringExtra("bccom");
        bcphone_str = fromDetail.getStringExtra("bcphone");
        bcemail_str = fromDetail.getStringExtra("bcemail");
        bcadd_str = fromDetail.getStringExtra("bcadd");
    }

    void setInitView() {
        bcname.setText(bcname_str);
        bclevel.setText(bclevel_str);
        bccom.setText(bccom_str);
        bcphone.setText(bcphone_str);
        bcemail.setText(bcemail_str);
        bcadd.setText(bcadd_str);
    }

    void setClickBtn() {
        bccamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}