package com.example.mingi.management;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BCDetailActivity extends AppCompatActivity {
    ImageView imgView, emailBtn, callBtn, msgBtn;
    TextView bcname, bclevel, bccom, bcphone, bcemail, bcadd;
    Button bcedit, bcdelete;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str, no;

    private String permissions[] = {"Manifest.permission.CALL_PHONE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail);
        initView();
        btnClick();

        setTestString();
        setTextView();
    }

    void initView() {
        bcname = (TextView) findViewById(R.id.bcname);
        bclevel = (TextView) findViewById(R.id.bclevel);
        bccom = (TextView) findViewById(R.id.bccom);
        bcphone = (TextView) findViewById(R.id.bcphone);
        bcemail = (TextView) findViewById(R.id.bcemail);
        bcadd = (TextView) findViewById(R.id.bcadd);

        bcedit = (Button) findViewById(R.id.bcedit);
        bcdelete = (Button) findViewById(R.id.bcdelete);

        imgView = (ImageView) findViewById(R.id.imgView);
        emailBtn = (ImageView) findViewById(R.id.emailbtn);
        callBtn = (ImageView) findViewById(R.id.callbtn);
        msgBtn = (ImageView) findViewById(R.id.msgbtn);
    }

    void setTestString() {
        bcname_str = "가나다";
        bclevel_str = "직급직급";
        bccom_str = "회사이름이야";
        bcphone_str = "010-1234-4567";
        bcemail_str = "abc@naver.com";
        bcadd_str = "부산광역시 부산진구 주소주소 주소주소(회사명)";
        no = bcphone_str.replaceAll("-", "");
    }

    void setTextView() {
        bcname.setText(bcname_str);
        bclevel.setText(bclevel_str);
        bccom.setText(bccom_str);
        bcphone.setText(bcphone_str);
        bcemail.setText(bcemail_str);
        bcadd.setText(bcadd_str);
    }

    void btnClick() {
        bcedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goEdit = new Intent(getApplicationContext(), BCEditActivity.class);

                goEdit.putExtra("bcname", bcname_str);
                goEdit.putExtra("bclevel", bclevel_str);
                goEdit.putExtra("bccom", bccom_str);
                goEdit.putExtra("bcphone", bcphone_str);
                goEdit.putExtra("bcemail", bcemail_str);
                goEdit.putExtra("bcadd", bcadd_str);

                startActivity(goEdit);
            }
        });

        bcdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "삭제 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = "tel:" + bcphone_str;
                Intent goCall = new Intent(Intent.ACTION_CALL, Uri.parse(tel));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("BCDetailActivity", "no call Permission");
                    return;
                }
                startActivity(goCall);
            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri smsUri = Uri.parse("sms:" + no);
                Intent goSMS = new Intent(Intent.ACTION_SENDTO, smsUri);
                startActivity(goSMS);
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goEmail = new Intent(Intent.ACTION_SEND);
                goEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {bcemail_str});
                goEmail.setType("text/html");
                goEmail.setPackage("com.google.android.gm");

                if(goEmail.resolveActivity(getPackageManager()) != null) {
                    startActivity(goEmail);
                }
            }
        });

    }
}
