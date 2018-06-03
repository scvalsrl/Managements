package com.example.mingi.management;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;

public class BCDetailActivity extends AppCompatActivity {
    ImageView imgView, emailBtn, callBtn, msgBtn;
    TextView bcname, bclevel, bccom, bcphone, bcemail, bcadd;
    Button bcedit, bcdelete;

    String bcname_str, bclevel_str, bccom_str, bcphone_str, bcemail_str, bcadd_str, no, bclat_str, bclon_str, bcphoto_str;

    String isGPSEnable, nowLat, nowLon, nowName ,userID;


    private String permissions[] = {"Manifest.permission.CALL_PHONE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail);


        Intent intent = getIntent();

        isGPSEnable = intent.getStringExtra("isGPSEnable");
        nowLat = intent.getStringExtra("nowLat");
        nowLon = intent.getStringExtra("nowLon");
        nowName = intent.getStringExtra("nowName");
        userID = intent.getStringExtra("userID");

        bcname_str = intent.getStringExtra("BC_name");
        bclevel_str = intent.getStringExtra("BC_level");
        bccom_str = intent.getStringExtra("BC_com");
        bcphone_str = intent.getStringExtra("BC_phone");
        bcemail_str = intent.getStringExtra("BC_mail");
        bcadd_str = intent.getStringExtra("BC_add");
        bclat_str = intent.getStringExtra("BC_lat");
        bclon_str = intent.getStringExtra("BC_lon");
        bcphoto_str = intent.getStringExtra("BC_photo");

        initView();
        btnClick();
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


        new BCDetailActivity.DownloadImageTask((ImageView) findViewById(R.id.imgView))
                .execute("http://scvalsrl.cafe24.com/uploads/"+bcphoto_str);

        emailBtn = (ImageView) findViewById(R.id.emailbtn);
        callBtn = (ImageView) findViewById(R.id.callbtn);
        msgBtn = (ImageView) findViewById(R.id.msgbtn);
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
                    ActivityCompat.requestPermissions(BCDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

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


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
